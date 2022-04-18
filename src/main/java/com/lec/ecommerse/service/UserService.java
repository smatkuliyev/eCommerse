package com.lec.ecommerse.service;

import com.lec.ecommerse.domain.Role;
import com.lec.ecommerse.domain.User;
import com.lec.ecommerse.domain.enumeration.UserRole;
import com.lec.ecommerse.exception.AuthException;
import com.lec.ecommerse.exception.BadRequestException;
import com.lec.ecommerse.exception.ConflictException;
import com.lec.ecommerse.exception.ResourceNotFoundException;
import com.lec.ecommerse.repository.RoleRepository;
import com.lec.ecommerse.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(User user) throws BadRequestException {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ConflictException("Error: Email is already in use!");
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        Set<Role> roles = new HashSet<>();
        Role customerRole = roleRepository.findByName(UserRole.ROLE_CUSTOMER)
                .orElseThrow(() -> new ResourceNotFoundException("Error: Role is not found."));

        roles.add(customerRole);
        user.setRoles(roles);

        userRepository.save(user);

    }

    public void login(String email, String password) throws AuthException {
        try {

            Optional<User> user = userRepository.findByEmail(email);

            if (!BCrypt.checkpw(password, user.get().getPassword())) { //girilen password ile sistemdeki kontrol eder
                throw new AuthException("Invalid credentials");
            }

        } catch (Exception e) {
            throw new AuthException("Invalid credentials");
        }
    }

}
