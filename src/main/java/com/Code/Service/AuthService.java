package com.Code.Service;

import com.Code.response.SignupRequest;

public interface AuthService {


    void sendLoginOtp(String email) throws Exception;
    String createUser (SignupRequest req) throws Exception;
}
