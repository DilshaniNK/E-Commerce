package com.Code.Service.Impl;

import com.Code.Service.AuthService;
import com.Code.Service.EmailService;
import com.Code.config.JwtProvider;
import com.Code.domain.USER_ROLE;
import com.Code.model.Cart;
import com.Code.model.Seller;
import com.Code.model.User;
import com.Code.model.VerificationCode;
import com.Code.repository.CartRepository;
import com.Code.repository.SellerRepository;
import com.Code.repository.UserRepository;
import com.Code.repository.VerificationCodeRepository;
import com.Code.request.LoginRequest;
import com.Code.response.AuthResponse;
import com.Code.response.SignupRequest;
import com.Code.util.Otputil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartRepository cartRepository;
    private final JwtProvider  jwtProvider;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    private final CustomUserServiceImpl customUserService;
    private final SellerRepository sellerRepository;

    @Override
    public void sendLoginOtp(String email, USER_ROLE role) throws Exception {
        String SIGNING_PREFIX = "signing_";
//        String SELLER_PREFIX = "seller_";
        if (email.startsWith(SIGNING_PREFIX)) {
            email = email.substring(SIGNING_PREFIX.length());

            if(role.equals(USER_ROLE.ROLE_SELLER)){
                Seller seller = sellerRepository.findByEmail(email);
                if(seller == null){
                    throw new Exception("Seller not found with provided email");
                }

            }
            else{
                User user = userRepository.findByEmail(email);
                if (user == null) {
                    throw new Exception("User not found with provided email");

                }
            }

        }
        VerificationCode isExist = verificationCodeRepository.findByEmail(email);
        if (isExist != null) {
            verificationCodeRepository.delete(isExist);
        }

        String otp = Otputil.generateOtp();
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setOtp(otp);
        verificationCode.setEmail(email);
        verificationCodeRepository.save(verificationCode);

        String subject  = "dil E-commerce web side login/signup otp";
        String text = "Your Login/Signup Otp is - " + otp;
        emailService.sendVarificationOtpEmail(email, otp, subject, text);

    }

    @Override
    public String createUser(SignupRequest req) throws Exception {


        VerificationCode verificationCode=  verificationCodeRepository.findByEmail(req.getEmail());
        if(verificationCode == null || !verificationCode.getOtp().equals(req.getOtp())){
            throw new Exception("Wrong otp ...");

        }


        User user = userRepository.findByEmail(req.getEmail());


        if(user==null){
            User createdUser = new User();
            createdUser.setEmail(req.getEmail());
            createdUser.setFullName(req.getFullName());
            createdUser.setRole(USER_ROLE.ROLE_CUSTOMER);
            createdUser.setMobile("0714819714");
            createdUser.setPassword(passwordEncoder.encode(req.getOtp()));
            user= userRepository.save(createdUser);

            Cart cart= new Cart();
            cart.setUser(user);
            cartRepository.save(cart);

        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(USER_ROLE.ROLE_CUSTOMER.toString()));

        Authentication authentication = new UsernamePasswordAuthenticationToken(req.getEmail(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtProvider.generateToken(authentication);
    }

    @Override
    public AuthResponse signing(LoginRequest req) {
        String username = req.getEmail();
        String otp = req.getOtp();
        Authentication authentication = authenticate(username,otp);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);
        AuthResponse authResponse = new AuthResponse();

        authResponse.setJwt(token);
        authResponse.setMessage("Login successful");

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String roleName = authorities.isEmpty() ? null : authorities.iterator().next().getAuthority();

        authResponse.setRole(USER_ROLE.valueOf(roleName));
        return authResponse;
    }

    private Authentication authenticate(String username, String otp) {
        UserDetails userDetails= customUserService.loadUserByUsername(username);
        if(userDetails == null) {
            throw new BadCredentialsException("Invalid username or password");

        }
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(username);
        if(verificationCode == null || !verificationCode.getOtp().equals(otp)) {
            throw new BadCredentialsException("Wrong otp ...");

        }
        return new  UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
