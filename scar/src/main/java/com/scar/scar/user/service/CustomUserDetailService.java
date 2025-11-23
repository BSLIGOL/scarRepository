package com.scar.scar.user.service;

import com.scar.scar.user.domain.User;
import com.scar.scar.user.repository.UserRepository;
import com.scar.scar.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("?????Β???????? ????????⑸늅????????????롮쾸?椰??????????怨몄）."));
        return new CustomUserDetails(user);
    }
}

