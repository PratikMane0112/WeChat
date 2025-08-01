package com.pratikmane.service;

import com.pratikmane.dto.request.CreateUserRequest;
import com.pratikmane.dto.request.UpdateUserRequest;
import com.pratikmane.dto.response.UserResponse;
import com.pratikmane.entity.User;
import com.pratikmane.exception.UserNotFoundException;
import com.pratikmane.exception.EmailAlreadyExistsException;
import com.pratikmane.repository.UserRepository;
import com.pratikmane.service.impl.UserServiceImpl;
import com.pratikmane.utils.CloudinaryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private MultipartFile profileImage;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private CreateUserRequest createUserRequest;
    private UpdateUserRequest updateUserRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("John")
                .lastName("Doe")
                .profilePicture("https://cloudinary.com/profile.jpg")
                .bio("Test bio")
                .isVerified(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createUserRequest = CreateUserRequest.builder()
                .email("test@example.com")
                .password("password123")
                .firstName("John")
                .lastName("Doe")
                .bio("Test bio")
                .build();

        updateUserRequest = UpdateUserRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .bio("Updated bio")
                .build();
    }

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully() {
        // Given
        when(userRepository.existsByEmail(createUserRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(createUserRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserResponse result = userService.createUser(createUserRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getBio()).isEqualTo("Test bio");
        assertThat(result.isVerified()).isFalse();

        verify(userRepository).existsByEmail(createUserRequest.getEmail());
        verify(passwordEncoder).encode(createUserRequest.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(createUserRequest.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createUser(createUserRequest))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage("Email already exists: test@example.com");

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Should find user by ID successfully")
    void shouldFindUserByIdSuccessfully() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        UserResponse result = userService.getUserById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@example.com");

        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when user not found by ID")
    void shouldThrowExceptionWhenUserNotFoundById() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with id: 1");

        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Should find user by email successfully")
    void shouldFindUserByEmailSuccessfully() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        UserResponse result = userService.getUserByEmail("test@example.com");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");

        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {
        // Given
        User updatedUser = testUser.toBuilder()
                .firstName("Jane")
                .lastName("Smith")
                .bio("Updated bio")
                .updatedAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        UserResponse result = userService.updateUser(1L, updateUserRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getBio()).isEqualTo("Updated bio");

        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should update profile picture successfully")
    void shouldUpdateProfilePictureSuccessfully() {
        // Given
        String imageUrl = "https://cloudinary.com/new-profile.jpg";
        User updatedUser = testUser.toBuilder()
                .profilePicture(imageUrl)
                .updatedAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cloudinaryService.uploadImage(profileImage)).thenReturn(imageUrl);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        UserResponse result = userService.updateProfilePicture(1L, profileImage);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getProfilePicture()).isEqualTo(imageUrl);

        verify(userRepository).findById(1L);
        verify(cloudinaryService).uploadImage(profileImage);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should search users by name successfully")
    void shouldSearchUsersByNameSuccessfully() {
        // Given
        User user2 = User.builder()
                .id(2L)
                .email("jane@example.com")
                .firstName("Jane")
                .lastName("Doe")
                .build();

        List<User> users = Arrays.asList(testUser, user2);
        when(userRepository.searchByName("John")).thenReturn(users);

        // When
        List<UserResponse> result = userService.searchUsers("John");

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFirstName()).isEqualTo("John");
        assertThat(result.get(1).getFirstName()).isEqualTo("Jane");

        verify(userRepository).searchByName("John");
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository).findById(1L);
        verify(userRepository).delete(testUser);
    }

    @Test
    @DisplayName("Should verify user email successfully")
    void shouldVerifyUserEmailSuccessfully() {
        // Given
        User verifiedUser = testUser.toBuilder()
                .isVerified(true)
                .updatedAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(verifiedUser);

        // When
        UserResponse result = userService.verifyUserEmail(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isVerified()).isTrue();

        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle empty search results")
    void shouldHandleEmptySearchResults() {
        // Given
        when(userRepository.searchByName("NonExistent")).thenReturn(List.of());

        // When
        List<UserResponse> result = userService.searchUsers("NonExistent");

        // Then
        assertThat(result).isEmpty();

        verify(userRepository).searchByName("NonExistent");
    }

    @Test
    @DisplayName("Should validate user creation request")
    void shouldValidateUserCreationRequest() {
        // Given
        CreateUserRequest invalidRequest = CreateUserRequest.builder()
                .email("")
                .password("123")
                .firstName("")
                .lastName("")
                .build();

        // When & Then
        assertThatThrownBy(() -> userService.createUser(invalidRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
