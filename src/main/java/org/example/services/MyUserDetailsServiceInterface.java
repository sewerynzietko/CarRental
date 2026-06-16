package org.example.services;

import org.springframework.security.core.userdetails.UserDetails;

public interface MyUserDetailsServiceInterface {
    public UserDetails loadUserByUsername(String username);
}
