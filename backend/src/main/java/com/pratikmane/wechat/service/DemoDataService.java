package com.pratikmane.wechat.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pratikmane.wechat.model.Chat;
import com.pratikmane.wechat.model.Comments;
import com.pratikmane.wechat.model.Message;
import com.pratikmane.wechat.model.Notification;
import com.pratikmane.wechat.model.Post;
import com.pratikmane.wechat.model.Reels;
import com.pratikmane.wechat.model.Story;
import com.pratikmane.wechat.model.User;
import com.pratikmane.wechat.repository.ChatRepository;
import com.pratikmane.wechat.repository.CommentRepository;
import com.pratikmane.wechat.repository.MessageRepository;
import com.pratikmane.wechat.repository.NotificationRepository;
import com.pratikmane.wechat.repository.PostRepository;
import com.pratikmane.wechat.repository.ReelsRepository;
import com.pratikmane.wechat.repository.StoryRepository;
import com.pratikmane.wechat.repository.UserRepository;

@Service
@Transactional
public class DemoDataService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private StoryRepository storyRepository;
    
    @Autowired
    private ReelsRepository reelsRepository;
    
    @Autowired
    private ChatRepository chatRepository;
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void populateAllDemoData() {
        // Clear existing data first
        clearAllData();
        
        // Create demo data in order
        List<User> users = createDemoUsers();
        createFollowerRelationships(users);
        List<Post> posts = createDemoPosts(users);
        createDemoComments(posts, users);
        createDemoStories(users);
        createDemoReels(users);
        List<Chat> chats = createDemoChats(users);
        createDemoMessages(chats, users);
        createDemoNotifications(users);
    }

    public void clearAllData() {
        messageRepository.deleteAll();
        chatRepository.deleteAll();
        notificationRepository.deleteAll();
        storyRepository.deleteAll();
        reelsRepository.deleteAll();
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    private List<User> createDemoUsers() {
        List<User> users = new ArrayList<>();
        String encodedPassword = passwordEncoder.encode("demoPassword123");

        String[][] userData = {
            {"alex_chen", "alex.chen@gmail.com", "Alex", "Chen", "+1-555-0101", "https://alexchen.dev", "Software Engineer | Tech Enthusiast | Coffee Lover ☕", "Male", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=400&fit=crop&crop=face"},
            {"sophia_maria", "sophia.maria@gmail.com", "Sophia", "Maria", "+1-555-0102", "https://sophiamaria.com", "Travel Blogger 🌍 | Photography | Digital Nomad", "Female", "https://images.unsplash.com/photo-1494790108755-2616b612b8e5?w=400&h=400&fit=crop&crop=face"},
            {"ryan_developer", "ryan.dev@gmail.com", "Ryan", "Thompson", "+1-555-0103", "https://github.com/ryan-dev", "Full Stack Developer | Open Source Contributor", "Male", "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=400&h=400&fit=crop&crop=face"},
            {"emma_design", "emma.watson@gmail.com", "Emma", "Watson", "+1-555-0104", "https://emmadesigns.portfolio", "UI/UX Designer | Creative Thinker | Art Lover 🎨", "Female", "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=400&h=400&fit=crop&crop=face"},
            {"michael_fitness", "mike.johnson@gmail.com", "Michael", "Johnson", "+1-555-0105", "https://fitnesswithmike.com", "Fitness Coach | Nutrition Expert | Motivational Speaker 💪", "Male", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&h=400&fit=crop&crop=face"},
            {"lisa_foodie", "lisa.chef@gmail.com", "Lisa", "Rodriguez", "+1-555-0106", "https://lisaskitchen.blog", "Professional Chef | Food Blogger | Recipe Creator 👩‍🍳", "Female", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&h=400&fit=crop&crop=face"},
            {"david_music", "david.beats@gmail.com", "David", "Miller", "+1-555-0107", "https://soundcloud.com/davidbeats", "Music Producer | DJ | Sound Engineer 🎵", "Male", "https://images.unsplash.com/photo-1507591064344-4c6ce005b128?w=400&h=400&fit=crop&crop=face"},
            {"nina_artist", "nina.paints@gmail.com", "Nina", "Anderson", "+1-555-0108", "https://ninaart.gallery", "Digital Artist | Illustrator | Creative Director 🎭", "Female", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400&h=400&fit=crop&crop=face"},
            {"james_entrepreneur", "james.startup@gmail.com", "James", "Wilson", "+1-555-0109", "https://jameswilson.co", "Startup Founder | Tech Investor | Innovation Enthusiast 🚀", "Male", "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=400&h=400&fit=crop&crop=face"},
            {"sarah_writer", "sarah.stories@gmail.com", "Sarah", "Davis", "+1-555-0110", "https://sarahwrites.medium.com", "Content Writer | Storyteller | Book Author 📚", "Female", "https://images.unsplash.com/photo-1489424731084-a5d8b219a5bb?w=400&h=400&fit=crop&crop=face"}
        };

        for (String[] data : userData) {
            User user = new User();
            user.setUsername(data[0]);
            user.setEmail(data[1]);
            user.setFirstName(data[2]);
            user.setLastName(data[3]);
            user.setMobile(data[4]);
            user.setWebsite(data[5]);
            user.setBio(data[6]);
            user.setGender(data[7]);
            user.setImage(data[8]);
            user.setPassword(encodedPassword);
            users.add(userRepository.save(user));
        }

        return users;
    }

    private void createFollowerRelationships(List<User> users) {
        // Create realistic follower relationships
        int[][] relationships = {
            {0, 1}, {0, 2}, {0, 3}, {0, 4}, {0, 7}, // Alex follows
            {1, 0}, {1, 3}, {1, 5}, {1, 8}, {1, 9}, // Sophia follows
            {2, 0}, {2, 6}, {2, 8}, {2, 1}, // Ryan follows
            {3, 0}, {3, 1}, {3, 7}, {3, 9}, {3, 5}, // Emma follows
            {4, 0}, {4, 2}, {4, 6}, {4, 8}, // Michael follows
            {5, 1}, {5, 3}, {5, 4}, {5, 9}, // Lisa follows
            {6, 2}, {6, 4}, {6, 8}, {6, 0}, // David follows
            {7, 0}, {7, 3}, {7, 1}, {7, 9}, // Nina follows
            {8, 0}, {8, 2}, {8, 6}, {8, 4}, // James follows
            {9, 1}, {9, 3}, {9, 5}, {9, 7}  // Sarah follows
        };

        for (int[] rel : relationships) {
            User follower = users.get(rel[0]);
            User following = users.get(rel[1]);
            following.getFollower().add(follower);
            userRepository.save(following);
        }
    }

    private List<Post> createDemoPosts(List<User> users) {
        List<Post> posts = new ArrayList<>();
        
        String[][] postData = {
            {"Just launched my new portfolio website! 🚀 Excited to share my latest projects with the world. #WebDevelopment #Portfolio", "https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=800&h=600", "San Francisco, CA", "0"},
            {"Sunset views from Santorini never get old! 🌅 This magical place continues to inspire my photography journey. #Travel #Photography #Santorini", "https://images.unsplash.com/photo-1570077188670-e3a8d69ac5ff?w=800&h=600", "Santorini, Greece", "1"},
            {"Contributing to open source feels amazing! Just merged my first major PR to a popular React library. The community support has been incredible! 💻 #OpenSource #React #Community", "https://images.unsplash.com/photo-1556075798-4825dfaaf498?w=800&h=600", "Remote", "2"},
            {"New UI design for a fintech app! Clean, minimal, and user-friendly. What do you think about this dashboard concept? 🎨 #UIDesign #UXDesign #Fintech", "https://images.unsplash.com/photo-1551650975-87deedd944c3?w=800&h=600", "New York, NY", "3"},
            {"Morning workout complete! 💪 Remember, consistency beats perfection every time. Small daily improvements lead to massive results! #Fitness #Motivation #HealthyLifestyle", "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=800&h=600", "Los Angeles, CA", "4"},
            {"Homemade pasta with truffle cream sauce! 🍝 Sometimes the simplest ingredients create the most extraordinary flavors. Recipe in comments! #FoodLover #Cooking #Homemade", "https://images.unsplash.com/photo-1551183053-bf91a1d81141?w=800&h=600", "Chicago, IL", "5"},
            {"Studio session vibes! 🎵 Working on some new beats that blend electronic with classical elements. Can't wait to share the full track! #MusicProduction #StudioLife #Electronic", "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=800&h=600", "Nashville, TN", "6"},
            {"Digital art piece: \"Dreams in Motion\" 🎭 Exploring the intersection of technology and human emotion through vibrant colors and flowing forms. #DigitalArt #CreativeProcess #ArtisticVision", "https://images.unsplash.com/photo-1541961017774-22349e4a1262?w=800&h=600", "Portland, OR", "7"},
            {"Exciting news! Our startup just secured Series A funding! 🚀 Grateful for an amazing team and supportive investors. The journey continues! #Startup #Entrepreneurship #TechNews", "https://images.unsplash.com/photo-1559136555-9303baea8ebd?w=800&h=600", "Austin, TX", "8"},
            {"New blog post is live! \"The Art of Storytelling in the Digital Age\" 📚 Exploring how technology shapes the way we tell and consume stories. Link in bio! #Writing #Storytelling #DigitalAge", "https://images.unsplash.com/photo-1455390582262-044cdead277a?w=800&h=600", "Boston, MA", "9"}
        };

        for (int i = 0; i < postData.length; i++) {
            Post post = new Post();
            post.setCaption(postData[i][0]);
            post.setImage(postData[i][1]);
            post.setLocation(postData[i][2]);
            post.setUser(users.get(Integer.parseInt(postData[i][3])));
            post.setCreatedAt(LocalDateTime.now().minusDays(i));
            
            // Add some likes
            Set<User> likes = new HashSet<>();
            for (int j = 0; j < 5; j++) {
                int randomUser = (i + j + 1) % users.size();
                likes.add(users.get(randomUser));
            }
            post.setLiked(likes);
            
            posts.add(postRepository.save(post));
        }

        // Add saved posts
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            List<Post> savedPosts = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                int postIndex = (i + j) % posts.size();
                savedPosts.add(posts.get(postIndex));
            }
            user.setSavedPosts(savedPosts);
            userRepository.save(user);
        }

        return posts;
    }

    private void createDemoComments(List<Post> posts, List<User> users) {
        String[] commentTexts = {
            "Amazing work! Love the creativity! 🔥",
            "This is absolutely incredible! Great job! 👏",
            "So inspiring! Thanks for sharing this! ✨",
            "Beautiful colors and composition! 🎨",
            "Your work always motivates me! Keep it up! 💪",
            "This looks delicious! Recipe please! 🤤",
            "Love the energy in this photo! 📸",
            "Such a great tutorial! Very helpful! 📚",
            "The attention to detail is amazing! 👌",
            "Can't wait to see more content like this! 🚀"
        };

        for (int i = 0; i < posts.size() && i < commentTexts.length; i++) {
            Comments comment = new Comments();
            comment.setContent(commentTexts[i]);
            comment.setUser(users.get((i + 1) % users.size()));
            comment.setCreatedAt(LocalDateTime.now().minusHours(i));
            
            // Add some likes to comments
            Set<User> commentLikes = new HashSet<>();
            for (int j = 0; j < 2; j++) {
                commentLikes.add(users.get((i + j + 2) % users.size()));
            }
            comment.setLiked(commentLikes);
            
            Comments savedComment = commentRepository.save(comment);
            
            // Add comment to post
            Post post = posts.get(i);
            post.getComments().add(savedComment);
            postRepository.save(post);
        }
    }

    private void createDemoStories(List<User> users) {
        String[] storyImages = {
            "https://images.unsplash.com/photo-1517077304055-6e89abbf09b0?w=400&h=600",
            "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&h=600",
            "https://images.unsplash.com/photo-1551650975-87deedd944c3?w=400&h=600",
            "https://images.unsplash.com/photo-1541963463532-d68292c34d19?w=400&h=600",
            "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&h=600"
        };

        String[] storyCaptions = {
            "Good morning! Starting the day with fresh coffee ☕",
            "Golden hour at the beach 🌅 Nature is amazing!",
            "Late night coding session 🌙 Building something cool!",
            "Sketching new design ideas 🎨 Inspiration everywhere!",
            "Pre-workout energy! Let's crush this session 💪"
        };

        for (int i = 0; i < Math.min(users.size(), storyImages.length); i++) {
            Story story = new Story();
            story.setUser(users.get(i));
            story.setImage(storyImages[i]);
            story.setCaptions(storyCaptions[i]);
            story.setTimestamp(LocalDateTime.now().minusHours(i));
            storyRepository.save(story);
        }
    }

    private void createDemoReels(List<User> users) {
        String[] reelTitles = {
            "Coding Productivity Tips",
            "Travel Photography Behind the Scenes",
            "Open Source Contribution Guide",
            "UI Design Process Walkthrough",
            "5-Minute Morning Workout",
            "Quick Pasta Recipe",
            "Beat Making Tutorial",
            "Digital Art Time-lapse",
            "Startup Pitch Practice",
            "Writing Tips for Beginners"
        };

        for (int i = 0; i < Math.min(users.size(), reelTitles.length); i++) {
            Reels reel = new Reels();
            reel.setTitle(reelTitles[i]);
            reel.setUser(users.get(i));
            reel.setVideo("https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_1mb.mp4");
            reelsRepository.save(reel);
        }
    }

    private List<Chat> createDemoChats(List<User> users) {
        List<Chat> chats = new ArrayList<>();

        // Create private chats
        for (int i = 0; i < 3; i++) {
            Chat chat = new Chat();
            chat.setIs_group(false);
            chat.setCreated_by(users.get(i));
            List<User> chatUsers = new ArrayList<>();
            chatUsers.add(users.get(i));
            chatUsers.add(users.get((i + 1) % users.size()));
            chat.setUsers(chatUsers);
            chats.add(chatRepository.save(chat));
        }

        // Create group chats
        String[] groupNames = {"Tech Enthusiasts", "Creative Minds", "Fitness Motivation"};
        String[] groupImages = {
            "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=200&h=200",
            "https://images.unsplash.com/photo-1515378791036-0648a814c963?w=200&h=200",
            "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=200&h=200"
        };

        for (int i = 0; i < groupNames.length; i++) {
            Chat groupChat = new Chat();
            groupChat.setChat_name(groupNames[i]);
            groupChat.setChat_image(groupImages[i]);
            groupChat.setIs_group(true);
            groupChat.setCreated_by(users.get(i + 2));
            
            List<User> groupUsers = new ArrayList<>();
            for (int j = 0; j < 4; j++) {
                groupUsers.add(users.get((i + j + 2) % users.size()));
            }
            groupChat.setUsers(groupUsers);
            chats.add(chatRepository.save(groupChat));
        }

        return chats;
    }

    private void createDemoMessages(List<Chat> chats, List<User> users) {
        String[][] messageTexts = {
            {"Hey! How's your project going?", "Great! Just finished the MVP. Thanks for asking!", "That's awesome! Can't wait to see it!"},
            {"Welcome everyone! 👋", "Excited to be here!", "Let's share knowledge and grow together!", "Thanks for creating this group!"},
            {"Morning workout complete! 💪", "Nice! What did you focus on today?", "Cardio and strength training combo", "Inspiring as always!"}
        };

        for (int i = 0; i < Math.min(chats.size(), messageTexts.length); i++) {
            Chat chat = chats.get(i);
            List<User> chatUsers = chat.getUsers();
            
            for (int j = 0; j < messageTexts[i].length; j++) {
                Message message = new Message();
                message.setContent(messageTexts[i][j]);
                message.setUser(chatUsers.get(j % chatUsers.size()));
                message.setChat(chat);
                message.setTimeStamp(LocalDateTime.now().minusHours(i * 2 + j));
                message.setIs_read(j < messageTexts[i].length - 1);
                messageRepository.save(message);
            }
        }
    }

    private void createDemoNotifications(List<User> users) {
        String[] notificationMessages = {
            " started following you",
            " liked your post",
            " commented on your post",
            " mentioned you in a comment",
            " shared your post",
            " sent you a message",
            " added you to a group chat",
            " liked your story",
            " viewed your profile",
            " saved your post"
        };

        for (int i = 0; i < users.size(); i++) {
            for (int j = 0; j < 3; j++) {
                Notification notification = new Notification();
                User fromUser = users.get((i + j + 1) % users.size());
                notification.setMessage(fromUser.getFirstName() + notificationMessages[j % notificationMessages.length]);
                notification.setUser(users.get(i));
                notification.setIs_seen(j == 0);
                notification.setTimestamp(LocalDateTime.now().minusHours(j * 2));
                notificationRepository.save(notification);
            }
        }
    }
}
