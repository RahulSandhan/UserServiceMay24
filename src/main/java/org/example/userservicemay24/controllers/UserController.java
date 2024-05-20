package org.example.userservicemay24.controllers;

import org.example.userservicemay24.dtos.LoginRequestDto;
import org.example.userservicemay24.dtos.SignupRequestDto;
import org.example.userservicemay24.dtos.ValidateTokenRequestDto;
import org.example.userservicemay24.models.Token;
import org.example.userservicemay24.models.User;
import org.example.userservicemay24.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> signUp(@RequestBody SignupRequestDto requestDto){
        try {
            //TODO add basic validations
            User user = userService.signup(requestDto.getName(), requestDto.getEmail(), requestDto.getPassword());
            return new ResponseEntity<>(user, HttpStatusCode.valueOf(201));
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        }
    }

    @PostMapping("/login")     // /user/login will be post end point, a new token will be created and will be sent to frontend
    public ResponseEntity<Token> login(@RequestBody LoginRequestDto requestDto){
        return null;
    }

    @PostMapping("/validate-token")
    public ResponseEntity<Token> validateToken(@RequestBody ValidateTokenRequestDto requestDto){
         return null;
    }
}
