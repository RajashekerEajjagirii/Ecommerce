package com.raj.ecommerce.security;

import com.raj.ecommerce.domain.Role;
import com.raj.ecommerce.domain.User;
import com.raj.ecommerce.repo.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;


@Component
public class UserInfoDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserInfoDetailsService(UserRepository userRepo){
        this.userRepository=userRepo;
    }

    /**
     * Loads a user by email (used as username).
     */
    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("User not found with email: "+email));

        return new org.springframework.security.core.userdetails.User(
              user.getEmail(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
        );
    }

    /**
     * * Convenience method to fetch domain User by email.*
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    /**
     * * Register a new user (with encoded password).
     * */
    public User register(User user, org.springframework.security.crypto.password.PasswordEncoder encoder) {
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
