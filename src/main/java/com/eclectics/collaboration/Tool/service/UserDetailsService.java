package com.eclectics.collaboration.Tool.service;

import com.eclectics.collaboration.Tool.security.CustomUserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserDetailsService {
    CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}

