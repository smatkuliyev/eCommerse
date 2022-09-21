package com.lec.ecommerse.service;

import com.lec.ecommerse.email.EmailService;
import com.lec.ecommerse.model.Role;
import com.lec.ecommerse.model.User;
import com.lec.ecommerse.model.enumeration.UserRole;
import com.lec.ecommerse.repository.RoleRepository;
import com.lec.ecommerse.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    private UserService userService;

    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, roleRepository, passwordEncoder, emailService);
    }

    @Test
    void fetchAllUsers() {
        userService.fetchAllUsers();
        verify(userRepository).findAll();
    }

}