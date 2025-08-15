package com.ecom.sb_ecom.controller;

import com.ecom.sb_ecom.security.payload.LoginRequest;
import com.ecom.sb_ecom.security.payload.MessageResponse;
import com.ecom.sb_ecom.security.payload.SignupRequest;
import com.ecom.sb_ecom.security.payload.UserInfoResponse;
import com.ecom.sb_ecom.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    AuthService authService;

    AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signin")
    public ResponseEntity<UserInfoResponse> signIn(@RequestBody LoginRequest loginRequest){
        return authService.signIn(loginRequest);
    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> signUp(@RequestBody SignupRequest signupRequest){
        return new ResponseEntity<>(authService.signUp(signupRequest), HttpStatus.CREATED);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signOut(){
        ResponseCookie cookie = authService.signOut();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                        cookie.toString())
                .body(new MessageResponse("You've been signed out!"));
    }

    @GetMapping("/username")
    public ResponseEntity<String> getCurrentUserName(){
        return new ResponseEntity<>(authService.getCurrentUserName(), HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<UserInfoResponse> getCurrentUser(){
        return new ResponseEntity<>(authService.getCurrentUser(), HttpStatus.OK);
    }

    @GetMapping("/sellers")
    public ResponseEntity getSellers(){
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
