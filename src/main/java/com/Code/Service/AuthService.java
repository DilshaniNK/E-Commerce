package com.Code.Service;

import com.Code.request.LoginRequest;
import com.Code.response.AuthResponse;
import com.Code.response.SignupRequest;

public interface AuthService {


    void sendLoginOtp(String email) throws Exception;
    String createUser (SignupRequest req) throws Exception;
    AuthResponse signing(LoginRequest req) ;

}
