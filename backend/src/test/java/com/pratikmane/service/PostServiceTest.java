package com.pratikmane.service;

import com.pratikmane.dto.request.CreatePostRequest;
import com.pratikmane.dto.request.UpdatePostRequest;
import com.pratikmane.dto.response.PostResponse;
import com.pratikmane.entity.Post;
import com.pratikmane.entity.User;
import com.pratikmane.exception.PostNotFoundException;
import com.pratikmane.exception.UnauthorizedActionException;
import com.pratikmane.repository.PostRepository;
import com.pratikmane.repository.UserRepository;
import com.pratikmane.service.impl.PostServiceImpl;
import com.pratikmane.utils.CloudinaryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Post Service Tests")
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private MultipartFile imageFile;

    @InjectMocks
    private PostServiceImpl postService;

    private User testUser;
    private User anotherUser;
    private Post testPost;
    private CreatePostRequest createPostRequest;
    private UpdatePostRequest updatePostRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .profilePicture("https://cloudinary.com/profile.jpg")
                .build();

        anotherUser = User.builder()
                .id(2L)
                .email("another@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .build();

        testPost = Post.builder()
                .id(1L)
                .content("This is a test post")
                .imageUrl("https://cloudinary.com/post-image.jpg")
                .user(testUser)
                .likes(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createPostRequest = CreatePostRequest.builder()
                .content("This is a test post")
                .build();

        updatePostRequest = UpdatePostRequest.builder()
                .content("Updated post content")
                .build();
    }

    @Test
    @DisplayName("Should create post successfully")
    void shouldCreatePostSuccessfully() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // When
        PostResponse result = postService.createPost(createPostRequest, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("This is a test post");
        assertThat(result.getUser().getId()).isEqualTo(1L);
        assertThat(result.getLikesCount()).isEqualTo(0);

        verify(userRepository).findById(1L);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    @DisplayName("Should create post with image successfully")
    void shouldCreatePostWithImageSuccessfully() {
        // Given
        String imageUrl = "https://cloudinary.com/uploaded-image.jpg";
        Post postWithImage = testPost.toBuilder()
                .imageUrl(imageUrl)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cloudinaryService.uploadImage(imageFile)).thenReturn(imageUrl);
        when(postRepository.save(any(Post.class))).thenReturn(postWithImage);

        // When
        PostResponse result = postService.createPostWithImage(createPostRequest, imageFile, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getImageUrl()).isEqualTo(imageUrl);

        verify(cloudinaryService).uploadImage(imageFile);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    @DisplayName("Should get post by ID successfully")
    void shouldGetPostByIdSuccessfully() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        // When
        PostResponse result = postService.getPostById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getContent()).isEqualTo("This is a test post");

        verify(postRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when post not found")
    void shouldThrowExceptionWhenPostNotFound() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> postService.getPostById(1L))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessage("Post not found with id: 1");

        verify(postRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get user posts with pagination")
    void shouldGetUserPostsWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Post> posts = Arrays.asList(testPost);
        Page<Post> postPage = new PageImpl<>(posts, pageable, 1);

        when(postRepository.findByUserIdOrderByCreatedAtDesc(1L, pageable)).thenReturn(postPage);

        // When
        Page<PostResponse> result = postService.getUserPosts(1L, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);

        verify(postRepository).findByUserIdOrderByCreatedAtDesc(1L, pageable);
    }

    @Test
    @DisplayName("Should update post successfully")
    void shouldUpdatePostSuccessfully() {
        // Given
        Post updatedPost = testPost.toBuilder()
                .content("Updated post content")
                .updatedAt(LocalDateTime.now())
                .build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(updatedPost);

        // When
        PostResponse result = postService.updatePost(1L, updatePostRequest, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Updated post content");

        verify(postRepository).findById(1L);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    @DisplayName("Should throw exception when updating post by non-owner")
    void shouldThrowExceptionWhenUpdatingPostByNonOwner() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        // When & Then
        assertThatThrownBy(() -> postService.updatePost(1L, updatePostRequest, 2L))
                .isInstanceOf(UnauthorizedActionException.class)
                .hasMessage("You are not authorized to update this post");

        verify(postRepository).findById(1L);
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("Should delete post successfully")
    void shouldDeletePostSuccessfully() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        doNothing().when(postRepository).delete(testPost);

        // When
        postService.deletePost(1L, 1L);

        // Then
        verify(postRepository).findById(1L);
        verify(postRepository).delete(testPost);
    }

    @Test
    @DisplayName("Should throw exception when deleting post by non-owner")
    void shouldThrowExceptionWhenDeletingPostByNonOwner() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        // When & Then
        assertThatThrownBy(() -> postService.deletePost(1L, 2L))
                .isInstanceOf(UnauthorizedActionException.class)
                .hasMessage("You are not authorized to delete this post");

        verify(postRepository).findById(1L);
        verify(postRepository, never()).delete(any(Post.class));
    }

    @Test
    @DisplayName("Should like post successfully")
    void shouldLikePostSuccessfully() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        Post likedPost = testPost.toBuilder()
                .likes(Set.of(testUser))
                .build();
        when(postRepository.save(any(Post.class))).thenReturn(likedPost);

        // When
        PostResponse result = postService.likePost(1L, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLikesCount()).isEqualTo(1);
        assertThat(result.isLikedByCurrentUser()).isTrue();

        verify(postRepository).findById(1L);
        verify(userRepository).findById(1L);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    @DisplayName("Should unlike post successfully")
    void shouldUnlikePostSuccessfully() {
        // Given
        Post likedPost = testPost.toBuilder()
                .likes(Set.of(testUser))
                .build();
        
        when(postRepository.findById(1L)).thenReturn(Optional.of(likedPost));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        Post unlikedPost = testPost.toBuilder()
                .likes(new HashSet<>())
                .build();
        when(postRepository.save(any(Post.class))).thenReturn(unlikedPost);

        // When
        PostResponse result = postService.unlikePost(1L, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLikesCount()).isEqualTo(0);
        assertThat(result.isLikedByCurrentUser()).isFalse();

        verify(postRepository).findById(1L);
        verify(userRepository).findById(1L);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    @DisplayName("Should get feed posts for user")
    void shouldGetFeedPostsForUser() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Long> followingIds = Arrays.asList(2L, 3L);
        List<Post> feedPosts = Arrays.asList(testPost);
        Page<Post> feedPage = new PageImpl<>(feedPosts, pageable, 1);

        when(userRepository.findFollowingIds(1L)).thenReturn(followingIds);
        when(postRepository.findByUserIdInOrderByCreatedAtDesc(followingIds, pageable))
                .thenReturn(feedPage);

        // When
        Page<PostResponse> result = postService.getFeedPosts(1L, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(userRepository).findFollowingIds(1L);
        verify(postRepository).findByUserIdInOrderByCreatedAtDesc(followingIds, pageable);
    }

    @Test
    @DisplayName("Should search posts by content")
    void shouldSearchPostsByContent() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Post> searchResults = Arrays.asList(testPost);
        Page<Post> searchPage = new PageImpl<>(searchResults, pageable, 1);

        when(postRepository.findByContentContainingIgnoreCaseOrderByCreatedAtDesc("test", pageable))
                .thenReturn(searchPage);

        // When
        Page<PostResponse> result = postService.searchPosts("test", pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getContent()).contains("test");

        verify(postRepository).findByContentContainingIgnoreCaseOrderByCreatedAtDesc("test", pageable);
    }

    @Test
    @DisplayName("Should get trending posts")
    void shouldGetTrendingPosts() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        List<Post> trendingPosts = Arrays.asList(testPost);
        Page<Post> trendingPage = new PageImpl<>(trendingPosts, pageable, 1);

        when(postRepository.findTrendingPosts(any(LocalDateTime.class), eq(pageable)))
                .thenReturn(trendingPage);

        // When
        Page<PostResponse> result = postService.getTrendingPosts(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(postRepository).findTrendingPosts(any(LocalDateTime.class), eq(pageable));
    }

    @Test
    @DisplayName("Should handle empty feed")
    void shouldHandleEmptyFeed() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findFollowingIds(1L)).thenReturn(List.of());
        when(postRepository.findByUserIdInOrderByCreatedAtDesc(List.of(), pageable))
                .thenReturn(Page.empty());

        // When
        Page<PostResponse> result = postService.getFeedPosts(1L, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();

        verify(userRepository).findFollowingIds(1L);
    }
}
