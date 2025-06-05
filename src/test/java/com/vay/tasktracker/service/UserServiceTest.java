package com.vay.tasktracker.service;

import com.vay.tasktracker.dto.auth.RegisterRequest;
import com.vay.tasktracker.exception.UserAlreadyExistsException;
import com.vay.tasktracker.model.User;
import com.vay.tasktracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private RegisterRequest registerRequest;
    private static final String USERNAME = "testuser";
    private static final String EMAIL = "test@example.com";
    private static final String PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "encodedPassword123";

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername(USERNAME);
        user.setEmail(EMAIL);
        user.setPassword(ENCODED_PASSWORD);

        registerRequest = new RegisterRequest();
        registerRequest.setUsername(USERNAME);
        registerRequest.setEmail(EMAIL);
        registerRequest.setPassword(PASSWORD);
    }

    @Test
    void loadUserByUsername_whenUserExists_shouldReturnUser() {
        // given
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));

        // when
        var result = userService.loadUserByUsername(USERNAME);

        // then
        assertThat(result).isEqualTo(user);
        verify(userRepository).findByUsername(USERNAME);
    }

    @Test
    void loadUserByUsername_whenUserDoesNotExist_shouldThrowException() {
        // given
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> userService.loadUserByUsername(USERNAME))
            .isInstanceOf(UsernameNotFoundException.class)
            .hasMessageContaining(USERNAME);
    }

    @Test
    void registerUser_whenUserDoesNotExist_shouldRegisterNewUser() {
        // given
        when(userRepository.existsByUsername(USERNAME)).thenReturn(false);
        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // when
        var result = userService.registerUser(registerRequest);

        // then
        assertThat(result).isEqualTo(user);
        verify(userRepository).existsByUsername(USERNAME);
        verify(userRepository).existsByEmail(EMAIL);
        verify(passwordEncoder).encode(PASSWORD);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_whenUsernameExists_shouldThrowException() {
        // given
        when(userRepository.existsByUsername(USERNAME)).thenReturn(true);

        // when/then
        assertThatThrownBy(() -> userService.registerUser(registerRequest))
            .isInstanceOf(UserAlreadyExistsException.class)
            .hasMessageContaining("username");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_whenEmailExists_shouldThrowException() {
        // given
        when(userRepository.existsByUsername(USERNAME)).thenReturn(false);
        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

        // when/then
        assertThatThrownBy(() -> userService.registerUser(registerRequest))
            .isInstanceOf(UserAlreadyExistsException.class)
            .hasMessageContaining("email");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findByUsername_whenUserExists_shouldReturnUser() {
        // given
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));

        // when
        var result = userService.findByUsername(USERNAME);

        // then
        assertThat(result).isEqualTo(user);
        verify(userRepository).findByUsername(USERNAME);
    }

    @Test
    void findByUsername_whenUserDoesNotExist_shouldThrowException() {
        // given
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> userService.findByUsername(USERNAME))
            .isInstanceOf(UsernameNotFoundException.class)
            .hasMessageContaining(USERNAME);
    }

    @Test
    void findByEmail_whenUserExists_shouldReturnUser() {
        // given
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        // when
        var result = userService.findByEmail(EMAIL);

        // then
        assertThat(result).isEqualTo(user);
        verify(userRepository).findByEmail(EMAIL);
    }

    @Test
    void findByEmail_whenUserDoesNotExist_shouldThrowException() {
        // given
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> userService.findByEmail(EMAIL))
            .isInstanceOf(UsernameNotFoundException.class)
            .hasMessageContaining(EMAIL);
    }
} 