package org.example.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Override
    @Transactional(readOnly = true)
    public abstract UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}