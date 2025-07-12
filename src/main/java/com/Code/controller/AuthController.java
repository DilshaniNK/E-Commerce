package com.Code.controller;

import com.Code.Service.AuthService;
import com.Code.domain.USER_ROLE;
import com.Code.model.User;
import com.Code.model.VerificationCode;
import com.Code.repository.UserRepository;
import com.Code.request.LoginRequest;
import com.Code.response.ApiResponse;
import com.Code.response.AuthResponse;
import com.Code.response.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody SignupRequest req) throws Exception {

        String jwt = authService.createUser(req);

        AuthResponse res = new AuthResponse();
        res.setJwt(jwt);
        res.setMessage("Register Successfully");
        res.setRole(USER_ROLE.ROLE_CUSTOMER);

        return ResponseEntity.ok(res);
    }

    @PostMapping("/sent/login-signup-otp")
    public ResponseEntity<ApiResponse> sentOtpHandler(@RequestBody VerificationCode req) throws Exception {

        authService.sendLoginOtp(req.getEmail());

        ApiResponse res = new ApiResponse();

        res.setMessage("otp sent successfully");


        return ResponseEntity.ok(res);
    }

    @PostMapping("/signing")
    public ResponseEntity<AuthResponse> LoginHandler(@RequestBody LoginRequest req) throws Exception {

        AuthResponse authResponse= authService.signing(req);
        return ResponseEntity.ok(authResponse);
    }
}
