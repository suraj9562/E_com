package com.ecom.sb_ecom.controller;

import com.ecom.sb_ecom.model.User;
import com.ecom.sb_ecom.service.AuthService;
import com.ecom.sb_ecom.service.AuthServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    AuthService authService;

    AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(name = "/signin")
    public ResponseEntity signIn(){
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(name = "/signup")
    public ResponseEntity signUp(){
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(name = "/signout")
    public ResponseEntity signOut(){
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/username")
    public ResponseEntity getCurrentUserName(){
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity getCurrentUser(){
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/sellers")
    public ResponseEntity getSellers(){
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
