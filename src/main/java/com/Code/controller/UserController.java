package com.Code.controller;

import com.Code.Service.UserService;
import com.Code.domain.USER_ROLE;
import com.Code.model.User;
import com.Code.response.AuthResponse;
import com.Code.response.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/profile")
    public ResponseEntity<User> createUserHandler(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        User user = userService.findUserByJwtToken(jwt);

        return ResponseEntity.ok(user);
    }

}
