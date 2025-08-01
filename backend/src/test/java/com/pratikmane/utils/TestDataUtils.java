package com.pratikmane.utils;

import com.pratikmane.entity.User;
import com.pratikmane.entity.Post;
import com.pratikmane.entity.Message;
import com.pratikmane.entity.Chat;
import com.pratikmane.dto.request.CreateUserRequest;
import com.pratikmane.dto.request.CreatePostRequest;
import com.pratikmane.dto.request.LoginRequest;
import com.pratikmane.dto.request.RegisterRequest;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

/**
 * Utility class for creating test data objects
 */
public class TestDataUtils {

    public static User createTestUser(Long id, String email, String firstName, String lastName) {
        return User.builder()
                .id(id)
                .email(email)
                .password("encodedPassword")
                .firstName(firstName)
                .lastName(lastName)
                .profilePicture("https://test.com/profile.jpg")
                .bio("Test user bio")
                .isVerified(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static User createTestUser() {
        return createTestUser(1L, "test@example.com", "Test", "User");
    }

    public static Post createTestPost(Long id, String content, User user) {
        return Post.builder()
                .id(id)
                .content(content)
                .imageUrl("https://test.com/post-image.jpg")
                .user(user)
                .likes(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Post createTestPost(User user) {
        return createTestPost(1L, "Test post content", user);
    }

    public static Chat createTestPrivateChat(Long id, User user1, User user2) {
        return Chat.builder()
                .id(id)
                .chatName("Private Chat")
                .isGroupChat(false)
                .users(Set.of(user1, user2))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Chat createTestGroupChat(Long id, String chatName, User admin, Set<User> users) {
        return Chat.builder()
                .id(id)
                .chatName(chatName)
                .isGroupChat(true)
                .admin(admin)
                .users(users)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Message createTestMessage(Long id, String content, User sender, Chat chat) {
        return Message.builder()
                .id(id)
                .content(content)
                .sender(sender)
                .chat(chat)
                .timestamp(LocalDateTime.now())
                .isRead(false)
                .build();
    }

    public static CreateUserRequest createUserRequest() {
        return CreateUserRequest.builder()
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .bio("Test user bio")
                .build();
    }

    public static CreateUserRequest createUserRequest(String email, String firstName, String lastName) {
        return CreateUserRequest.builder()
                .email(email)
                .password("password123")
                .firstName(firstName)
                .lastName(lastName)
                .bio("Test user bio")
                .build();
    }

    public static CreatePostRequest createPostRequest() {
        return CreatePostRequest.builder()
                .content("Test post content")
                .build();
    }

    public static CreatePostRequest createPostRequest(String content) {
        return CreatePostRequest.builder()
                .content(content)
                .build();
    }

    public static LoginRequest createLoginRequest() {
        return LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();
    }

    public static LoginRequest createLoginRequest(String email, String password) {
        return LoginRequest.builder()
                .email(email)
                .password(password)
                .build();
    }

    public static RegisterRequest createRegisterRequest() {
        return RegisterRequest.builder()
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .build();
    }

    public static RegisterRequest createRegisterRequest(String email, String firstName, String lastName) {
        return RegisterRequest.builder()
                .email(email)
                .password("password123")
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }

    // Validation test data
    public static CreateUserRequest createInvalidUserRequest() {
        return CreateUserRequest.builder()
                .email("invalid-email")
                .password("123")
                .firstName("")
                .lastName("")
                .build();
    }

    public static LoginRequest createInvalidLoginRequest() {
        return LoginRequest.builder()
                .email("")
                .password("")
                .build();
    }

    public static RegisterRequest createInvalidRegisterRequest() {
        return RegisterRequest.builder()
                .email("invalid-email")
                .password("123")
                .firstName("")
                .lastName("")
                .build();
    }

    // Helper methods for assertions
    public static void assertUserEquals(User expected, User actual) {
        if (expected == null || actual == null) {
            if (expected != actual) {
                throw new AssertionError("Users are not equal: expected=" + expected + ", actual=" + actual);
            }
            return;
        }

        if (!expected.getEmail().equals(actual.getEmail()) ||
            !expected.getFirstName().equals(actual.getFirstName()) ||
            !expected.getLastName().equals(actual.getLastName())) {
            throw new AssertionError("Users are not equal: expected=" + expected + ", actual=" + actual);
        }
    }

    public static void assertPostEquals(Post expected, Post actual) {
        if (expected == null || actual == null) {
            if (expected != actual) {
                throw new AssertionError("Posts are not equal: expected=" + expected + ", actual=" + actual);
            }
            return;
        }

        if (!expected.getContent().equals(actual.getContent()) ||
            !expected.getUser().getId().equals(actual.getUser().getId())) {
            throw new AssertionError("Posts are not equal: expected=" + expected + ", actual=" + actual);
        }
    }
}
