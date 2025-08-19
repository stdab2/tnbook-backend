package com.tnbook.tnbook.service;

import com.tnbook.tnbook.model.entity.User;
import com.tnbook.tnbook.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = this.userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException(String.format("User not found with email: %s.", email));
        } else {
            return user.get();
        }
    }

    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        Optional<User> user = this.userRepository.findById(id);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException(String.format("User not found with id: %s.", id));
        } else {
            return user.get();
        }
    }

}
