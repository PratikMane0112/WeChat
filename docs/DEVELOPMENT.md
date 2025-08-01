# 🔧 Development Guide

## Getting Started

This guide provides comprehensive instructions for setting up and developing the WeChat Social Platform locally.

## Prerequisites

### Required Software
- **Java 17+** (OpenJDK or Oracle JDK)
- **Node.js 18+** and npm/yarn
- **Docker** and Docker Compose
- **PostgreSQL 15** (if running without Docker)
- **Redis 7** (if running without Docker)
- **Git** for version control

### Optional Tools
- **Postman** for API testing
- **VS Code** with Java Extension Pack
- **IntelliJ IDEA** for Java development
- **pgAdmin** for database management

## Project Setup

### 1. Clone Repository
```bash
git clone https://github.com/PratikMane0112/WeChat.git
cd WeChat
```

### 2. Environment Configuration

#### Backend Configuration
```bash
cd backend
cp application-local.properties.example application-local.properties
```

Edit `application-local.properties`:
```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/wechat_db
spring.datasource.username=postgres
spring.datasource.password=your_password

# Redis Configuration
spring.redis.host=localhost
spring.redis.port=6379

# JWT Configuration
jwt.secret=your_jwt_secret_key_here
jwt.expiration=86400000

# Email Configuration (for development)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password

# Cloudinary Configuration
cloudinary.cloud_name=your_cloud_name
cloudinary.api_key=your_api_key
cloudinary.api_secret=your_api_secret
```

#### Frontend Configuration
```bash
cd frontend
cp .env.example .env
```

Edit `.env`:
```env
REACT_APP_API_BASE_URL=http://localhost:8080
REACT_APP_WEBSOCKET_URL=ws://localhost:8080/ws
REACT_APP_CLOUDINARY_CLOUD_NAME=your_cloud_name
REACT_APP_ZEGOCLOUD_APP_ID=your_zego_app_id
REACT_APP_ZEGOCLOUD_SERVER_SECRET=your_zego_server_secret
```

### 3. Database Setup

#### Using Docker (Recommended)
```bash
docker-compose up -d postgres redis
```

#### Manual Setup
```sql
-- Connect to PostgreSQL as superuser
CREATE DATABASE wechat_db;
CREATE USER wechat_user WITH ENCRYPTED PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE wechat_db TO wechat_user;
```

## Development Workflow

### 1. Docker Development (Easiest)
```bash
# Start all services
docker-compose up --build

# Start specific services
docker-compose up postgres redis
docker-compose up backend frontend
```

### 2. Local Development

#### Backend Development
```bash
cd backend

# Install dependencies and run
./mvnw clean install
./mvnw spring-boot:run

# Or using task runner
task dev:backend
```

#### Frontend Development
```bash
cd frontend

# Install dependencies
npm install
# or
yarn install

# Start development server
npm start
# or  
yarn start

# Or using task runner
task dev:frontend
```

### 3. Full Development Setup
```bash
# Using task runner for complete setup
task dev:setup    # Setup environment
task dev:run      # Run all services
task dev:test     # Run all tests
```

## Code Structure

### Backend Architecture

```
backend/src/main/java/com/pratikmane/
├── config/          # Configuration classes
│   ├── SecurityConfig.java
│   ├── WebSocketConfig.java
│   └── CloudinaryConfig.java
├── controller/      # REST controllers
│   ├── AuthController.java
│   ├── UserController.java
│   ├── PostController.java
│   └── MessageController.java
├── dto/            # Data Transfer Objects
│   ├── request/
│   └── response/
├── entity/         # JPA entities
│   ├── User.java
│   ├── Post.java
│   └── Message.java
├── repository/     # JPA repositories
├── service/        # Business logic
│   ├── impl/       # Service implementations
└── utils/          # Utility classes
```

### Frontend Architecture

```
frontend/src/
├── components/     # Reusable components
│   ├── Authentication/
│   ├── Posts/
│   ├── Messages/
│   └── Common/
├── pages/         # Page components
│   ├── HomePage/
│   ├── Profile/
│   └── Messages/
├── redux/         # State management
│   ├── store.js
│   ├── Auth/
│   ├── Post/
│   └── Message/  
├── utils/         # Utility functions
├── config/        # Configuration
└── styles/        # CSS files
```

## Development Standards

### Code Style

#### Java (Backend)
```java
// Use Google Java Style Guide
// Configure in IDE or use Spotless plugin

// Example controller method
@PostMapping("/api/posts")
public ResponseEntity<PostResponse> createPost(
    @RequestBody @Valid CreatePostRequest request,
    Authentication authentication) {
    
    User user = (User) authentication.getPrincipal();
    PostResponse post = postService.createPost(request, user);
    
    return ResponseEntity.status(HttpStatus.CREATED).body(post);
}
```

#### JavaScript/React (Frontend)
```javascript
// Use Prettier + ESLint configuration
// Configure in package.json

// Example functional component
import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';

const PostCard = ({ post, onLike, onComment }) => {
  const [isLiked, setIsLiked] = useState(false);
  const currentUser = useSelector(state => state.auth.user);
  
  useEffect(() => {
    setIsLiked(post.likes.includes(currentUser.id));
  }, [post.likes, currentUser.id]);
  
  const handleLike = () => {
    onLike(post.id);
    setIsLiked(!isLiked);
  };
  
  return (
    <div className="post-card">
      {/* Component JSX */}
    </div>
  );
};

export default PostCard;
```

### Git Workflow

#### Branch Naming
```bash
feature/add-video-calling
bugfix/fix-message-pagination  
hotfix/security-vulnerability
release/v1.2.0
```

#### Commit Messages
```bash
# Format: type(scope): description

feat(auth): add JWT refresh token functionality
fix(posts): resolve image upload timeout issue
docs(readme): update deployment instructions
style(frontend): improve mobile responsiveness
test(api): add integration tests for message endpoints
```

### Testing Strategy

#### Backend Testing
```java
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class UserServiceTest {
    
    @Autowired
    private UserService userService;
    
    @MockBean
    private UserRepository userRepository;
    
    @Test
    void shouldCreateUserSuccessfully() {
        // Given
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("test@example.com");
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        // When
        UserResponse response = userService.createUser(request);
        
        // Then
        assertEquals("test@example.com", response.getEmail());
        verify(userRepository).save(any(User.class));
    }
}
```

#### Frontend Testing
```javascript
import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { Provider } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';
import PostCard from '../components/PostCard';

const mockStore = configureStore({
  reducer: {
    auth: (state = { user: { id: 1 } }) => state,
    posts: (state = {}) => state
  }
});

describe('PostCard', () => {
  const mockPost = {
    id: 1,
    content: 'Test post content',
    likes: [1, 2, 3],
    user: { id: 2, name: 'John Doe' }
  };
  
  it('should render post content', () => {
    render(
      <Provider store={mockStore}>
        <PostCard post={mockPost} />
      </Provider>
    );
    
    expect(screen.getByText('Test post content')).toBeInTheDocument();
  });
  
  it('should handle like button click', () => {
    const onLikeMock = jest.fn();
    
    render(
      <Provider store={mockStore}>
        <PostCard post={mockPost} onLike={onLikeMock} />
      </Provider>
    );
    
    fireEvent.click(screen.getByTestId('like-button'));
    expect(onLikeMock).toHaveBeenCalledWith(1);
  });
});
```

## API Development

### API Documentation

#### Using Swagger/OpenAPI
```java
@RestController
@RequestMapping("/api/posts")
@Tag(name = "Posts", description = "Post management endpoints")
public class PostController {
    
    @PostMapping
    @Operation(summary = "Create a new post")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Post created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<PostResponse> createPost(
        @RequestBody @Valid CreatePostRequest request) {
        // Implementation
    }
}
```

#### Postman Collection
```json
{
  "info": {
    "name": "WeChat API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Authentication",
      "item": [
        {
          "name": "Login",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"email\": \"test@example.com\",\n  \"password\": \"password123\"\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/api/auth/login",
              "host": ["{{baseUrl}}"],
              "path": ["api", "auth", "login"]
            }
          }
        }
      ]
    }
  ]
}
```

### WebSocket Development

#### Backend WebSocket Configuration
```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
```

#### Frontend WebSocket Implementation
```javascript
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

class WebSocketService {
  constructor() {
    this.stompClient = null;
    this.connected = false;
  }
  
  connect(token) {
    const socket = new SockJS('/ws');
    this.stompClient = Stomp.over(socket);
    
    const headers = {
      'Authorization': `Bearer ${token}`
    };
    
    this.stompClient.connect(headers, (frame) => {
      this.connected = true;
      console.log('Connected: ' + frame);
      
      // Subscribe to user-specific messages
      this.stompClient.subscribe('/user/queue/messages', (message) => {
        const messageData = JSON.parse(message.body);
        this.onMessageReceived(messageData);
      });
    });
  }
  
  sendMessage(chatId, content) {
    if (this.stompClient && this.connected) {
      this.stompClient.send('/app/chat', {}, JSON.stringify({
        chatId: chatId,
        content: content
      }));
    }
  }
  
  disconnect() {
    if (this.stompClient) {
      this.stompClient.disconnect();
      this.connected = false;
    }
  }
}

export default new WebSocketService();
```

## Database Development

### Migration Management

#### Flyway Configuration
```properties
# application.properties
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
```

#### Migration Example
```sql
-- V1__Create_users_table.sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    profile_picture VARCHAR(500),
    bio TEXT,
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_created_at ON users(created_at);
```

### JPA Best Practices

```java
@Entity
@Table(name = "posts")
@EntityListeners(AuditingEntityListener.class)
public class Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
    
    @ManyToMany
    @JoinTable(
        name = "post_likes",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> likes = new HashSet<>();
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

## Performance Optimization

### Backend Optimization

#### Database Query Optimization
```java
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    // Use @Query for complex queries
    @Query("SELECT p FROM Post p " +
           "JOIN FETCH p.user " +
           "LEFT JOIN FETCH p.comments c " +
           "LEFT JOIN FETCH c.user " +
           "WHERE p.user.id IN :followingIds " +
           "ORDER BY p.createdAt DESC")
    Page<Post> findPostsByFollowingUsers(
        @Param("followingIds") List<Long> followingIds, 
        Pageable pageable
    );
    
    // Use native queries for performance-critical operations
    @Query(value = "SELECT p.*, u.first_name, u.last_name " +
                   "FROM posts p " +
                   "JOIN users u ON p.user_id = u.id " +
                   "WHERE p.created_at >= :since " +
                   "ORDER BY p.created_at DESC " +
                   "LIMIT :limit", 
           nativeQuery = true)
    List<Object[]> findRecentPostsOptimized(
        @Param("since") LocalDateTime since,
        @Param("limit") int limit
    );
}
```

#### Caching Strategy
```java
@Service
@CacheConfig(cacheNames = "posts")
public class PostService {
    
    @Cacheable(key = "#postId")
    public PostResponse getPost(Long postId) {
        // Method implementation
    }
    
    @CacheEvict(key = "#postId")
    public void deletePost(Long postId) {
        // Method implementation
    }
    
    @CachePut(key = "#result.id")
    public PostResponse updatePost(Long postId, UpdatePostRequest request) {
        // Method implementation
    }
}
```

### Frontend Optimization

#### Code Splitting
```javascript
import React, { Suspense, lazy } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

// Lazy load components
const HomePage = lazy(() => import('./pages/HomePage/HomePage'));
const Profile = lazy(() => import('./pages/Profile/Profile'));
const Messages = lazy(() => import('./pages/Messages/Messages'));

function App() {
  return (
    <Router>
      <Suspense fallback={<div>Loading...</div>}>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/messages" element={<Messages />} />
        </Routes>
      </Suspense>
    </Router>
  );
}
```

#### Image Optimization
```javascript
import React from 'react';

const OptimizedImage = ({ src, alt, width, height }) => {
  const cloudinaryTransform = (url, w, h) => {
    return url.replace('/upload/', `/upload/w_${w},h_${h},c_fill,f_auto,q_auto/`);
  };
  
  return (
    <img
      src={cloudinaryTransform(src, width, height)}
      alt={alt}
      loading="lazy"
      style={{ width, height }}
    />
  );
};
```

## Debugging Guide

### Backend Debugging

#### Application Logs
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PostService {
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);
    
    public PostResponse createPost(CreatePostRequest request, User user) {
        logger.info("Creating post for user: {}", user.getEmail());
        
        try {
            // Service logic
            logger.debug("Post created successfully with ID: {}", post.getId());
            return postResponse;
        } catch (Exception e) {
            logger.error("Error creating post for user: {}", user.getEmail(), e);
            throw new PostCreationException("Failed to create post", e);
        }
    }
}
```

#### Database Query Logging
```properties
# application-local.properties
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### Frontend Debugging

#### Redux DevTools
```javascript
import { configureStore } from '@reduxjs/toolkit';
import { authSlice } from './auth/authSlice';
import { postSlice } from './post/postSlice';

export const store = configureStore({
  reducer: {
    auth: authSlice.reducer,
    posts: postSlice.reducer,
  },
  devTools: process.env.NODE_ENV !== 'production',
});
```

#### Error Boundary
```javascript
import React from 'react';

class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false };
  }
  
  static getDerivedStateFromError(error) {
    return { hasError: true };
  }
  
  componentDidCatch(error, errorInfo) {
    console.error('Error caught by boundary:', error, errorInfo);
    // Log to error reporting service
  }
  
  render() {
    if (this.state.hasError) {
      return <h1>Something went wrong.</h1>;
    }
    
    return this.props.children;
  }
}
```

## Contributing Guidelines

### Pull Request Process

1. **Fork and Clone**: Fork the repository and clone locally
2. **Branch**: Create a feature branch from `develop`
3. **Code**: Implement changes following coding standards
4. **Test**: Ensure all tests pass and add new tests
5. **Document**: Update documentation if needed
6. **PR**: Create pull request with detailed description

### Code Review Checklist

- [ ] Code follows project style guidelines
- [ ] All tests pass
- [ ] No new security vulnerabilities
- [ ] Documentation updated
- [ ] Performance impact considered
- [ ] Error handling implemented
- [ ] Logging added where appropriate

## Support

For development questions:
- **Slack**: #development-help
- **Email**: dev-team@wechat.com
- **Documentation**: Internal wiki
- **Office Hours**: Tuesdays 2-4 PM EST
