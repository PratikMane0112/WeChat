package com.pratikmane.repository;

import com.pratikmane.entity.User;
import com.pratikmane.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("User Repository Tests")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        testUser1 = User.builder()
                .email("john.doe@example.com")
                .password("encodedPassword")
                .firstName("John")
                .lastName("Doe")
                .profilePicture("https://cloudinary.com/john.jpg")
                .bio("Software Developer")
                .isVerified(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testUser2 = User.builder()
                .email("jane.smith@example.com")
                .password("encodedPassword")
                .firstName("Jane")
                .lastName("Smith")
                .profilePicture("https://cloudinary.com/jane.jpg")
                .bio("Product Manager")
                .isVerified(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        entityManager.persistAndFlush(testUser1);
        entityManager.persistAndFlush(testUser2);
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        // When
        Optional<User> found = userRepository.findByEmail("john.doe@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
        assertThat(found.get().getLastName()).isEqualTo("Doe");
        assertThat(found.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    @DisplayName("Should return empty when user not found by email")
    void shouldReturnEmptyWhenUserNotFoundByEmail() {
        // When
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should check if email exists")
    void shouldCheckIfEmailExists() {
        // When
        boolean exists = userRepository.existsByEmail("john.doe@example.com");
        boolean notExists = userRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should search users by name")
    void shouldSearchUsersByName() {
        // When
        List<User> foundByFirstName = userRepository.searchByName("John");
        List<User> foundByLastName = userRepository.searchByName("Smith");
        List<User> foundByPartialName = userRepository.searchByName("Doe");

        // Then
        assertThat(foundByFirstName).hasSize(1);
        assertThat(foundByFirstName.get(0).getFirstName()).isEqualTo("John");

        assertThat(foundByLastName).hasSize(1);
        assertThat(foundByLastName.get(0).getLastName()).isEqualTo("Smith");

        assertThat(foundByPartialName).hasSize(1);
        assertThat(foundByPartialName.get(0).getLastName()).isEqualTo("Doe");
    }

    @Test
    @DisplayName("Should find verified users")
    void shouldFindVerifiedUsers() {
        // When
        List<User> verifiedUsers = userRepository.findByIsVerifiedTrue();

        // Then
        assertThat(verifiedUsers).hasSize(1);
        assertThat(verifiedUsers.get(0).getEmail()).isEqualTo("john.doe@example.com");
        assertThat(verifiedUsers.get(0).isVerified()).isTrue();
    }

    @Test
    @DisplayName("Should find unverified users")
    void shouldFindUnverifiedUsers() {
        // When
        List<User> unverifiedUsers = userRepository.findByIsVerifiedFalse();

        // Then
        assertThat(unverifiedUsers).hasSize(1);
        assertThat(unverifiedUsers.get(0).getEmail()).isEqualTo("jane.smith@example.com");
        assertThat(unverifiedUsers.get(0).isVerified()).isFalse();
    }

    @Test
    @DisplayName("Should find users created after specific date")
    void shouldFindUsersCreatedAfterSpecificDate() {
        // Given
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);

        // When
        List<User> recentUsers = userRepository.findByCreatedAtAfter(yesterday);

        // Then
        assertThat(recentUsers).hasSize(2);
    }

    @Test
    @DisplayName("Should save and retrieve user correctly")
    void shouldSaveAndRetrieveUserCorrectly() {
        // Given
        User newUser = User.builder()
                .email("new.user@example.com")
                .password("password")
                .firstName("New")
                .lastName("User")
                .bio("Test user")
                .isVerified(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        User saved = userRepository.save(newUser);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThatretrieved(saved.getId())
                .isPresent()
                .get()
                .satisfies(user -> {
                    assertThat(user.getEmail()).isEqualTo("new.user@example.com");
                    assertThat(user.getFirstName()).isEqualTo("New");
                    assertThat(user.getLastName()).isEqualTo("User");
                    assertThat(user.getBio()).isEqualTo("Test user");
                    assertThat(user.isVerified()).isFalse();
                });
    }

    @Test
    @DisplayName("Should update user correctly")
    void shouldUpdateUserCorrectly() {
        // Given
        User user = userRepository.findByEmail("john.doe@example.com").orElseThrow();
        user.setFirstName("Johnny");
        user.setBio("Updated bio");
        user.setUpdatedAt(LocalDateTime.now());

        // When
        User updated = userRepository.save(user);

        // Then
        assertThat(updated.getFirstName()).isEqualTo("Johnny");
        assertThat(updated.getBio()).isEqualTo("Updated bio");
        assertThat(updated.getEmail()).isEqualTo("john.doe@example.com"); // Should remain unchanged
    }

    @Test
    @DisplayName("Should delete user correctly")
    void shouldDeleteUserCorrectly() {
        // Given
        User user = userRepository.findByEmail("john.doe@example.com").orElseThrow();

        // When
        userRepository.delete(user);

        // Then
        Optional<User> deleted = userRepository.findByEmail("john.doe@example.com");
        assertThat(deleted).isEmpty();
    }

    @Test
    @DisplayName("Should handle case insensitive email search")
    void shouldHandleCaseInsensitiveEmailSearch() {
        // When
        Optional<User> found = userRepository.findByEmail("JOHN.DOE@EXAMPLE.COM");

        // Then
        // This test assumes your repository method handles case insensitivity
        // If not, you might need to implement it in your repository
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    @DisplayName("Should count total users")
    void shouldCountTotalUsers() {
        // When
        long count = userRepository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should find users with specific bio content")
    void shouldFindUsersWithSpecificBioContent() {
        // When
        List<User> developers = userRepository.findByBioContainingIgnoreCase("developer");
        List<User> managers = userRepository.findByBioContainingIgnoreCase("manager");

        // Then
        assertThat(developers).hasSize(1);
        assertThat(developers.get(0).getFirstName()).isEqualTo("John");

        assertThat(managers).hasSize(1);
        assertThat(managers.get(0).getFirstName()).isEqualTo("Jane");
    }
}
