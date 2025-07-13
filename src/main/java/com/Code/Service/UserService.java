package com.Code.Service;

import com.Code.model.User;

public interface UserService {

     User findUserByJwtToken(String jwt) throws Exception;
     User findUserByEmail(String email) throws Exception;


}
