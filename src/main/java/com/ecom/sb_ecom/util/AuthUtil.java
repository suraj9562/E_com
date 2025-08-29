package com.ecom.sb_ecom.util;

import com.ecom.sb_ecom.exceptions.ResourceNotFoundException;
import com.ecom.sb_ecom.model.User;
import com.ecom.sb_ecom.repository.UserRepository;
import com.ecom.sb_ecom.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    @Autowired
    UserRepository userRepository;

    public User getLoggerInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetailsImpl =  (UserDetailsImpl) authentication.getPrincipal();

        return userRepository.findById(userDetailsImpl.getId()).orElseThrow(
                () -> new ResourceNotFoundException("User", "userId", userDetailsImpl.getId())
        );
    }
}
