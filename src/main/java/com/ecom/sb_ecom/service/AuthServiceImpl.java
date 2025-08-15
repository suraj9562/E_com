package com.ecom.sb_ecom.service;

import com.ecom.sb_ecom.exceptions.ApiException;
import com.ecom.sb_ecom.model.AppRole;
import com.ecom.sb_ecom.model.Role;
import com.ecom.sb_ecom.model.User;
import com.ecom.sb_ecom.repository.RoleRepository;
import com.ecom.sb_ecom.repository.UserRepository;
import com.ecom.sb_ecom.security.jwt.JwtUtils;
import com.ecom.sb_ecom.security.payload.LoginRequest;
import com.ecom.sb_ecom.security.payload.MessageResponse;
import com.ecom.sb_ecom.security.payload.SignupRequest;
import com.ecom.sb_ecom.security.payload.UserInfoResponse;
import com.ecom.sb_ecom.security.services.UserDetailsImpl;
import org.modelmapper.ModelMapper;
import org.springframework.boot.Banner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {


    RoleRepository roleRepository;
    AuthenticationManager authenticationManager;
    JwtUtils jwtUtils;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    ModelMapper modelMapper;

    AuthServiceImpl(AuthenticationManager authenticationManager,
                    JwtUtils jwtUtils,
                    UserRepository userRepository,
                    RoleRepository roleRepository,
                    PasswordEncoder passwordEncoder,
                    ModelMapper modelMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }


    @Override
    public ResponseEntity<UserInfoResponse> signIn(LoginRequest loginRequest) {
        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
        }
        catch (Exception e) {
            throw new ApiException("Authentication Error : " + e.getMessage());
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();

        // generate auth token
        ResponseCookie cookie = jwtUtils.generateJwtCookie(user);
        List<String> roles = user.getAuthorities().stream().map(
                authority -> authority.getAuthority()
        ).toList();

        UserInfoResponse userInfoResponse = new UserInfoResponse();
        userInfoResponse.setId(user.getId());
        userInfoResponse.setUsername(user.getUsername());
        userInfoResponse.setRoles(roles);
        userInfoResponse.setJwtToken(cookie.getValue());


        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(userInfoResponse);
    }

    @Override
    public MessageResponse signUp(SignupRequest signupRequest) {
        if(userRepository.existsByUserName(signupRequest.getUsername())) {
            throw new ApiException("Username already exists");
        }

        if(userRepository.existsByEmail(signupRequest.getEmail())){
            throw new ApiException("Email already exists");
        }

        Set<Role> roles = new HashSet<>();
        Set<String> requestBodyRoles = signupRequest.getRoles();
        Role role;

        if(requestBodyRoles.isEmpty()){
             role = roleRepository.findByRoleName(AppRole.ROLE_USER).orElseThrow(
                    () -> new RuntimeException("Error: Role is not found.")
            );

            roles.add(role);
        }
        else{
            for(String roleName : requestBodyRoles){
                switch (roleName){
                    case "admin":
                        role = roleRepository.findByRoleName(AppRole.ROLE_ADMIN).orElseThrow(
                                () -> new RuntimeException("Error: Role is not found.")
                        );
                        break;

                    case "user":
                        role = roleRepository.findByRoleName(AppRole.ROLE_USER).orElseThrow(
                                () -> new RuntimeException("Error: Role is not found.")
                        );
                        break;

                    case "seller":
                        role = roleRepository.findByRoleName(AppRole.ROLE_SELLER).orElseThrow(
                                () -> new RuntimeException("Error: Role is not found.")
                        );
                        break;

                    default:
                        throw new ApiException("Invalid role");
                }

                roles.add(role);
            }
        }

        User user =  new User();
        user.setUserName(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setRoles(roles);

        userRepository.save(user);
        return new MessageResponse("User registered successfully!");
    }

    @Override
    public ResponseCookie signOut() {
        return jwtUtils.signOut();
    }

    @Override
    public String getCurrentUserName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();

        return user.getUsername();
    }

    @Override
    public UserInfoResponse getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = user.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .toList();

        UserInfoResponse response = new UserInfoResponse(user.getId(),
                user.getUsername(), roles);

        return response;
    }
}
