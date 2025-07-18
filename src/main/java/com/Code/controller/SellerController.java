package com.Code.controller;


import com.Code.Service.AuthService;
import com.Code.Service.EmailService;
import com.Code.Service.SellerService;
import com.Code.domain.AccountStatus;
import com.Code.model.Seller;
import com.Code.model.SellerReport;
import com.Code.model.User;
import com.Code.model.VerificationCode;
import com.Code.repository.SellerRepository;
import com.Code.repository.VerificationCodeRepository;
import com.Code.request.LoginOtpRequest;
import com.Code.request.LoginRequest;
import com.Code.response.ApiResponse;
import com.Code.response.AuthResponse;
import com.Code.util.Otputil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seller")
public class SellerController {

    private final SellerService sellerService;
    private final VerificationCodeRepository verificationCodeRepository;
    private final AuthService authService;
    private final SellerRepository sellerRepository;
    private final EmailService emailService;


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginSeller(@RequestBody LoginRequest req) throws Exception {

        String otp = req.getOtp();
        String email = req.getEmail();
        req.setEmail("seller_" + email);

        AuthResponse authResponse = authService.signing(req);
        return ResponseEntity.ok(authResponse);
    }

    @PatchMapping("/verify/{otp}")
    public ResponseEntity<Seller> verifySellerEmail(@PathVariable String otp) throws Exception {
        VerificationCode verificationCode = verificationCodeRepository.findByOtp(otp);

        if(verificationCode == null || !verificationCode.getOtp().equals(otp)) {
            throw new Exception("Invalid OTP");
        }
        Seller seller = sellerService.verifyEmail(verificationCode.getEmail(),otp);
        return new ResponseEntity<>(seller, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Seller> createSeller(@RequestBody Seller seller) throws Exception, MessagingException {
            Seller savedSeller = sellerService.createSeller(seller);
            String otp = Otputil.generateOtp();
            VerificationCode verificationCode = new VerificationCode();
            verificationCode.setOtp(otp);
            verificationCode.setEmail(seller.getEmail());
            verificationCodeRepository.save(verificationCode);

            String subject = "Dil platform verification Code :";
            String text = "Welcome to the online new world , verify your account using following link :";
            String frontend_url = "http://localhost:3000/verify-seller/";
            emailService.sendVarificationOtpEmail(seller.getEmail(),verificationCode.getOtp(),subject,text + frontend_url);
            return new ResponseEntity<>(savedSeller, HttpStatus.CREATED);
    }

    @GetMapping("{/id}")
    public ResponseEntity<Seller> getSellerById(@PathVariable Long id) throws Exception {
        Seller seller = sellerService.getSellerById(id);
        return new ResponseEntity<>(seller,HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<Seller> getSellerByJwt(@RequestHeader ("Authorization") String jwt) throws Exception {
        Seller seller = sellerService.getSellerProfile(jwt);
        return new ResponseEntity<>(seller,HttpStatus.OK);
    }

//    @GetMapping("/report")
//    public ResponseEntity<SellerReport> getSellerReport(@RequestHeader ("Authorization") String jwt) throws Exception {
//
//    }

    @GetMapping
    public ResponseEntity<List<Seller>> getAllSellers(@RequestParam(required = false) AccountStatus status ) throws Exception {
        List<Seller> sellers = sellerService.getAllSellers(status);
        return ResponseEntity.ok(sellers);

    }

    @PatchMapping()
    public ResponseEntity<Seller> updateSeller(@RequestHeader("Authorization") String jwt,
                                               @RequestBody Seller seller) throws Exception {
        Seller profile = sellerService.getSellerProfile(jwt);
        Seller updatedSeller = sellerService.updateSeller(profile.getId(), seller);
        return ResponseEntity.ok(updatedSeller);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeller(@PathVariable Long id) throws Exception {
        sellerService.deleteSeller(id);
        return ResponseEntity.noContent().build();
    }

}
