package com.pratikmane.controller;

import com.pratikmane.controller.AuthController;
import com.pratikmane.dto.request.LoginRequest;
import com.pratikmane.dto.request.RegisterRequest;
import com.pratikmane.dto.request.ResetPasswordRequest;
import com.pratikmane.dto.response.AuthResponse;
import com.pratikmane.dto.response.UserResponse;
import com.pratikmane.service.AuthService;
import com.pratikmane.security.JwtTokenProvider;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@DisplayName("Auth Controller Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private ResetPasswordRequest resetPasswordRequest;
    private AuthResponse authResponse;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        registerRequest = RegisterRequest.builder()
                .email("test@example.com")
                .password("password123")
                .firstName("John")
                .lastName("Doe")
                .build();

        resetPasswordRequest = ResetPasswordRequest.builder()
                .token("reset-token")
                .newPassword("newPassword123")
                .build();

        userResponse = UserResponse.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .isVerified(false)
                .build();

        authResponse = AuthResponse.builder()
                .token("jwt-token")
                .refreshToken("refresh-token")
                .user(userResponse)
                .build();
    }

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void shouldLoginSuccessfullyWithValidCredentials() throws Exception {
        // Given
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                .andExpect(jsonPath("$.user.firstName").value("John"))
                .andExpect(jsonPath("$.user.lastName").value("Doe"));
    }

    @Test
    @DisplayName("Should return bad request for invalid login credentials")
    void shouldReturnBadRequestForInvalidLoginCredentials() throws Exception {
        // Given
        LoginRequest invalidRequest = LoginRequest.builder()
                .email("")
                .password("")
                .build();

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUserSuccessfully() throws Exception {
        // Given
        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                .andExpected(jsonPath("$.user.isVerified").value(false));
    }

    @Test
    @DisplayName("Should return bad request for invalid registration data")
    void shouldReturnBadRequestForInvalidRegistrationData() throws Exception {
        // Given
        RegisterRequest invalidRequest = RegisterRequest.builder()
                .email("invalid-email")
                .password("123")
                .firstName("")
                .lastName("")
                .build();

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should request password reset successfully")
    void shouldRequestPasswordResetSuccessfully() throws Exception {
        // Given
        String email = "test@example.com";
        when(authService.requestPasswordReset(email)).thenReturn("Password reset email sent");

        // When & Then
        mockMvc.perform(post("/api/auth/forgot-password")
                .param("email", email)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset email sent"));
    }

    @Test
    @DisplayName("Should reset password successfully")
    void shouldResetPasswordSuccessfully() throws Exception {
        // Given
        when(authService.resetPassword(any(ResetPasswordRequest.class)))
                .thenReturn("Password reset successfully");

        // When & Then
        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordRequest))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset successfully"));
    }

    @Test
    @DisplayName("Should refresh token successfully")
    @WithMockUser
    void shouldRefreshTokenSuccessfully() throws Exception {
        // Given
        String refreshToken = "refresh-token";
        when(authService.refreshToken(refreshToken)).thenReturn(authResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                .param("refreshToken", refreshToken)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    @DisplayName("Should verify email successfully")
    void shouldVerifyEmailSuccessfully() throws Exception {
        // Given
        String verificationToken = "verification-token";
        when(authService.verifyEmail(verificationToken)).thenReturn("Email verified successfully");

        // When & Then
        mockMvc.perform(post("/api/auth/verify-email")
                .param("token", verificationToken)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Email verified successfully"));
    }

    @Test
    @DisplayName("Should logout successfully")
    @WithMockUser
    void shouldLogoutSuccessfully() throws Exception {
        // Given
        String token = "jwt-token";
        when(authService.logout(token)).thenReturn("Logged out successfully");

        // When & Then
        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer " + token)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out successfully"));
    }

    @Test
    @DisplayName("Should return unauthorized for invalid token")
    void shouldReturnUnauthorizedForInvalidToken() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer invalid-token")
                .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should validate email format in login request")
    void shouldValidateEmailFormatInLoginRequest() throws Exception {
        // Given
        LoginRequest invalidEmailRequest = LoginRequest.builder()
                .email("invalid-email-format")
                .password("password123")
                .build();

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidEmailRequest))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should validate password strength in registration")
    void shouldValidatePasswordStrengthInRegistration() throws Exception {
        // Given
        RegisterRequest weakPasswordRequest = RegisterRequest.builder()
                .email("test@example.com")
                .password("123")
                .firstName("John")
                .lastName("Doe")
                .build();

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(weakPasswordRequest))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }
}
