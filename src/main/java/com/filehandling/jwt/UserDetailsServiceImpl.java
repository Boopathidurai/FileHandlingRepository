package com.filehandling.jwt;


import com.filehandling.model.User;
import com.filehandling.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser=userRepository.findFirstByEmail(username);
            if (optionalUser.isEmpty()) throw new UsernameNotFoundException("username not found",null);
        return new org.springframework.security.core.userdetails.User(optionalUser.get().getEmail(),
                optionalUser.get().getPassword(),
                new ArrayList<>());
    }
}
