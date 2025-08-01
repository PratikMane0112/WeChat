package com.pratikmane.integration;

import com.pratikmane.dto.request.CreatePostRequest;
import com.pratikmane.dto.request.LoginRequest;
import com.pratikmane.dto.request.RegisterRequest;
import com.pratikmane.dto.response.AuthResponse;
import com.pratikmane.dto.response.PostResponse;
import com.pratikmane.entity.User;
import com.pratikmane.repository.UserRepository;
import com.pratikmane.repository.PostRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Integration Tests")
class WeChat ApplicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        // Clean up database
        postRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = User.builder()
                .email("integration@example.com")
                .password(passwordEncoder.encode("password123"))
                .firstName("Integration")
                .lastName("Test")
                .bio("Integration test user")
                .isVerified(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(testUser);

        // Login to get JWT token
        LoginRequest loginRequest = LoginRequest.builder()
                .email("integration@example.com")
                .password("password123")
                .build();

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();

        String loginResponse = loginResult.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(loginResponse, AuthResponse.class);
        jwtToken = authResponse.getToken();
    }

    @Test
    @DisplayName("Should complete user registration flow")
    void shouldCompleteUserRegistrationFlow() throws Exception {
        // Given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("newuser@example.com")
                .password("password123")
                .firstName("New")
                .lastName("User")
                .build();

        // When - Register new user
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.user.firstName").value("New"))
                .andExpect(jsonPath("$.user.lastName").value("User"));

        // Then - Verify user exists in database
        User savedUser = userRepository.findByEmail("newuser@example.com").orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getFirstName()).isEqualTo("New");
        assertThat(savedUser.getLastName()).isEqualTo("User");
    }

    @Test
    @DisplayName("Should complete post creation and retrieval flow")
    void shouldCompletePostCreationAndRetrievalFlow() throws Exception {
        // Given
        CreatePostRequest createPostRequest = CreatePostRequest.builder()
                .content("This is an integration test post")
                .build();

        // When - Create post
        MvcResult createResult = mockMvc.perform(post("/api/posts")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPostRequest))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("This is an integration test post"))
                .andExpect(jsonPath("$.user.email").value("integration@example.com"))
                .andReturn();

        String createResponse = createResult.getResponse().getContentAsString();
        PostResponse createdPost = objectMapper.readValue(createResponse, PostResponse.class);

        // Then - Retrieve post
        mockMvc.perform(get("/api/posts/{postId}", createdPost.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdPost.getId()))
                .andExpect(jsonPath("$.content").value("This is an integration test post"));
    }

    @Test
    @DisplayName("Should handle post like/unlike flow")
    void shouldHandlePostLikeUnlikeFlow() throws Exception {
        // Given - Create a post first
        CreatePostRequest createPostRequest = CreatePostRequest.builder()
                .content("Post to be liked")
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/posts")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPostRequest))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andReturn();

        String createResponse = createResult.getResponse().getContentAsString();
        PostResponse createdPost = objectMapper.readValue(createResponse, PostResponse.class);

        // When - Like the post
        mockMvc.perform(post("/api/posts/{postId}/like", createdPost.getId())
                .header("Authorization", "Bearer " + jwtToken)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likesCount").value(1))
                .andExpect(jsonPath("$.likedByCurrentUser").value(true));

        // Then - Unlike the post
        mockMvc.perform(post("/api/posts/{postId}/unlike", createdPost.getId())
                .header("Authorization", "Bearer " + jwtToken)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likesCount").value(0))
                .andExpect(jsonPath("$.likedByCurrentUser").value(false));
    }

    @Test
    @DisplayName("Should handle user profile update flow")
    void shouldHandleUserProfileUpdateFlow() throws Exception {
        // Given
        String updateJson = """
            {
                "firstName": "Updated",
                "lastName": "Name",
                "bio": "Updated bio"
            }
            """;

        // When - Update profile
        mockMvc.perform(put("/api/users/{userId}", testUser.getId())
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("Name"))
                .andExpect(jsonPath("$.bio").value("Updated bio"));

        // Then - Verify update in database
        User updatedUser = userRepository.findById(testUser.getId()).orElse(null);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getFirstName()).isEqualTo("Updated");
        assertThat(updatedUser.getLastName()).isEqualTo("Name");
        assertThat(updatedUser.getBio()).isEqualTo("Updated bio");
    }

    @Test
    @DisplayName("Should handle unauthorized access")
    void shouldHandleUnauthorizedAccess() throws Exception {
        // When - Try to access protected endpoint without token
        mockMvc.perform(get("/api/posts/feed"))
                .andExpect(status().isUnauthorized());

        // When - Try to access protected endpoint with invalid token
        mockMvc.perform(get("/api/posts/feed")
                .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should handle validation errors")
    void shouldHandleValidationErrors() throws Exception {
        // Given - Invalid registration request
        RegisterRequest invalidRequest = RegisterRequest.builder()
                .email("invalid-email")
                .password("123")
                .firstName("")
                .lastName("")
                .build();

        // When - Try to register with invalid data
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle resource not found")
    void shouldHandleResourceNotFound() throws Exception {
        // When - Try to get non-existent post
        mockMvc.perform(get("/api/posts/{postId}", 99999L)
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());

        // When - Try to get non-existent user
        mockMvc.perform(get("/api/users/{userId}", 99999L)
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should handle user search functionality")
    void shouldHandleUserSearchFunctionality() throws Exception {
        // Given - Create another user for search
        User searchUser = User.builder()
                .email("searchable@example.com")
                .password(passwordEncoder.encode("password"))
                .firstName("Searchable")
                .lastName("User")
                .bio("Findable user")
                .isVerified(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(searchUser);

        // When - Search for users
        mockMvc.perform(get("/api/users/search")
                .param("query", "Searchable")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].firstName").value("Searchable"));
    }

    @Test
    @DisplayName("Should handle pagination")
    void shouldHandlePagination() throws Exception {
        // When - Request posts with pagination
        mockMvc.perform(get("/api/posts/feed")
                .param("page", "0")
                .param("size", "10")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable").exists())
                .andExpect(jsonPath("$.totalElements").exists());
    }

    @Test
    @DisplayName("Should complete authentication flow with refresh token")
    void shouldCompleteAuthenticationFlowWithRefreshToken() throws Exception {
        // Given - Login to get refresh token
        LoginRequest loginRequest = LoginRequest.builder()
                .email("integration@example.com")
                .password("password123")
                .build();

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();

        String loginResponse = loginResult.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(loginResponse, AuthResponse.class);

        // When - Use refresh token to get new access token
        mockMvc.perform(post("/api/auth/refresh")
                .param("refreshToken", authResponse.getRefreshToken())
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }
}
