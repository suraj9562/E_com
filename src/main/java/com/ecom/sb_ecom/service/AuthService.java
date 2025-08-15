package com.ecom.sb_ecom.service;

import com.ecom.sb_ecom.security.payload.LoginRequest;
import com.ecom.sb_ecom.security.payload.MessageResponse;
import com.ecom.sb_ecom.security.payload.SignupRequest;
import com.ecom.sb_ecom.security.payload.UserInfoResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<UserInfoResponse> signIn(LoginRequest loginRequest);
    MessageResponse signUp(SignupRequest signupRequest);
    ResponseCookie signOut();

    String getCurrentUserName();
    public UserInfoResponse getCurrentUser();
}
