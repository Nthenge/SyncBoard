package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.repository.UserRespository;
import com.eclectics.collaboration.Tool.security.CustomUserDetails;
import com.eclectics.collaboration.Tool.service.UserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    private final UserRespository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new CustomUserDetails(user);
    }
}

