package com.eclectics.collaboration.Tool.security;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserDetailsService {
    CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}

