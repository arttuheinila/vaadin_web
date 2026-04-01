package com.example.application.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.application.data.AppUser;
import com.example.application.data.repository.AppUserRepository;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    public AppUserDetailsService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        AppUser user = appUserRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Käyttäjää ei löytynyt: " + username));

        String[] authorities = user.getRoles().stream()
                .map(role -> "ROLE_" + role.name())
                .sorted()
                .toArray(String[]::new);

        return User.withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .disabled(!user.isActive())
                .authorities(authorities)
                .build();
    }
}
