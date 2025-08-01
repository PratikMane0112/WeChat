package com.pratikmane.service;

import com.pratikmane.dto.request.SendMessageRequest;
import com.pratikmane.dto.response.MessageResponse;
import com.pratikmane.dto.response.ChatResponse;
import com.pratikmane.entity.Message;
import com.pratikmane.entity.Chat;
import com.pratikmane.entity.User;
import com.pratikmane.exception.ChatNotFoundException;
import com.pratikmane.exception.MessageNotFoundException;
import com.pratikmane.repository.MessageRepository;
import com.pratikmane.repository.ChatRepository;
import com.pratikmane.repository.UserRepository;
import com.pratikmane.service.impl.MessageServiceImpl;

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
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Message Service Tests")
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private MessageServiceImpl messageService;

    private User sender;
    private User receiver;
    private Chat testChat;
    private Message testMessage;
    private SendMessageRequest sendMessageRequest;

    @BeforeEach
    void setUp() {
        sender = User.builder()
                .id(1L)
                .email("sender@example.com")
                .firstName("John")
                .lastName("Sender")
                .build();

        receiver = User.builder()
                .id(2L)
                .email("receiver@example.com")
                .firstName("Jane")
                .lastName("Receiver")
                .build();

        testChat = Chat.builder()
                .id(1L)
                .chatName("Test Chat")
                .isGroupChat(false)
                .users(Set.of(sender, receiver))
                .createdAt(LocalDateTime.now())
                .build();

        testMessage = Message.builder()
                .id(1L)
                .content("Hello, this is a test message")
                .sender(sender)
                .chat(testChat)
                .timestamp(LocalDateTime.now())
                .isRead(false)
                .build();

        sendMessageRequest = SendMessageRequest.builder()
                .content("Hello, this is a test message")
                .chatId(1L)
                .build();
    }

    @Test
    @DisplayName("Should send message successfully")
    void shouldSendMessageSuccessfully() {
        // Given
        when(chatRepository.findById(1L)).thenReturn(Optional.of(testChat));
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(messageRepository.save(any(Message.class))).thenReturn(testMessage);
        doNothing().when(messagingTemplate).convertAndSendToUser(anyString(), anyString(), any());

        // When
        MessageResponse result = messageService.sendMessage(sendMessageRequest, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Hello, this is a test message");
        assertThat(result.getSender().getId()).isEqualTo(1L);
        assertThat(result.getChatId()).isEqualTo(1L);
        assertThat(result.isRead()).isFalse();

        verify(chatRepository).findById(1L);
        verify(userRepository).findById(1L);
        verify(messageRepository).save(any(Message.class));
        verify(messagingTemplate).convertAndSendToUser(eq("2"), eq("/queue/messages"), any(MessageResponse.class));
    }

    @Test
    @DisplayName("Should throw exception when chat not found")
    void shouldThrowExceptionWhenChatNotFound() {
        // Given
        when(chatRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> messageService.sendMessage(sendMessageRequest, 1L))
                .isInstanceOf(ChatNotFoundException.class)
                .hasMessage("Chat not found with id: 1");

        verify(chatRepository).findById(1L);
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    @DisplayName("Should get chat messages with pagination")
    void shouldGetChatMessagesWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        List<Message> messages = Arrays.asList(testMessage);
        Page<Message> messagePage = new PageImpl<>(messages, pageable, 1);

        when(chatRepository.findById(1L)).thenReturn(Optional.of(testChat));
        when(messageRepository.findByChatIdOrderByTimestampDesc(1L, pageable)).thenReturn(messagePage);

        // When
        Page<MessageResponse> result = messageService.getChatMessages(1L, pageable, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getContent()).isEqualTo("Hello, this is a test message");

        verify(chatRepository).findById(1L);
        verify(messageRepository).findByChatIdOrderByTimestampDesc(1L, pageable);
    }

    @Test
    @DisplayName("Should mark message as read successfully")
    void shouldMarkMessageAsReadSuccessfully() {
        // Given
        Message unreadMessage = testMessage.toBuilder()
                .isRead(false)
                .build();
        
        Message readMessage = testMessage.toBuilder()
                .isRead(true)
                .build();

        when(messageRepository.findById(1L)).thenReturn(Optional.of(unreadMessage));
        when(messageRepository.save(any(Message.class))).thenReturn(readMessage);

        // When
        MessageResponse result = messageService.markAsRead(1L, 2L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isRead()).isTrue();

        verify(messageRepository).findById(1L);
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    @DisplayName("Should throw exception when message not found for marking as read")
    void shouldThrowExceptionWhenMessageNotFoundForMarkingAsRead() {
        // Given
        when(messageRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> messageService.markAsRead(1L, 2L))
                .isInstanceOf(MessageNotFoundException.class)
                .hasMessage("Message not found with id: 1");

        verify(messageRepository).findById(1L);
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    @DisplayName("Should get user chats successfully")
    void shouldGetUserChatsSuccessfully() {
        // Given
        List<Chat> chats = Arrays.asList(testChat);
        when(chatRepository.findByUsersIdOrderByUpdatedAtDesc(1L)).thenReturn(chats);

        // When
        List<ChatResponse> result = messageService.getUserChats(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getChatName()).isEqualTo("Test Chat");

        verify(chatRepository).findByUsersIdOrderByUpdatedAtDesc(1L);
    }

    @Test
    @DisplayName("Should create private chat successfully")
    void shouldCreatePrivateChatSuccessfully() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(chatRepository.findPrivateChat(1L, 2L)).thenReturn(Optional.empty());
        when(chatRepository.save(any(Chat.class))).thenReturn(testChat);

        // When
        ChatResponse result = messageService.createPrivateChat(1L, 2L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.isGroupChat()).isFalse();
        assertThat(result.getUsers()).hasSize(2);

        verify(userRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(chatRepository).findPrivateChat(1L, 2L);
        verify(chatRepository).save(any(Chat.class));
    }

    @Test
    @DisplayName("Should return existing private chat if already exists")
    void shouldReturnExistingPrivateChatIfAlreadyExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(chatRepository.findPrivateChat(1L, 2L)).thenReturn(Optional.of(testChat));

        // When
        ChatResponse result = messageService.createPrivateChat(1L, 2L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        verify(chatRepository).findPrivateChat(1L, 2L);
        verify(chatRepository, never()).save(any(Chat.class));
    }

    @Test
    @DisplayName("Should create group chat successfully")
    void shouldCreateGroupChatSuccessfully() {
        // Given
        User user3 = User.builder().id(3L).email("user3@example.com").build();
        List<Long> userIds = Arrays.asList(1L, 2L, 3L);
        List<User> users = Arrays.asList(sender, receiver, user3);
        
        Chat groupChat = Chat.builder()
                .id(2L)
                .chatName("Group Chat")
                .isGroupChat(true)
                .users(new HashSet<>(users))
                .admin(sender)
                .build();

        when(userRepository.findAllById(userIds)).thenReturn(users);
        when(chatRepository.save(any(Chat.class))).thenReturn(groupChat);

        // When
        ChatResponse result = messageService.createGroupChat("Group Chat", userIds, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getChatName()).isEqualTo("Group Chat");
        assertThat(result.isGroupChat()).isTrue();
        assertThat(result.getUsers()).hasSize(3);
        assertThat(result.getAdmin().getId()).isEqualTo(1L);

        verify(userRepository).findAllById(userIds);
        verify(chatRepository).save(any(Chat.class));
    }

    @Test
    @DisplayName("Should delete message successfully")
    void shouldDeleteMessageSuccessfully() {
        // Given
        when(messageRepository.findById(1L)).thenReturn(Optional.of(testMessage));
        doNothing().when(messageRepository).delete(testMessage);

        // When
        messageService.deleteMessage(1L, 1L);

        // Then
        verify(messageRepository).findById(1L);
        verify(messageRepository).delete(testMessage);
    }

    @Test
    @DisplayName("Should throw exception when trying to delete message by non-sender")
    void shouldThrowExceptionWhenTryingToDeleteMessageByNonSender() {
        // Given
        when(messageRepository.findById(1L)).thenReturn(Optional.of(testMessage));

        // When & Then
        assertThatThrownBy(() -> messageService.deleteMessage(1L, 2L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("You can only delete your own messages");

        verify(messageRepository).findById(1L);
        verify(messageRepository, never()).delete(any(Message.class));
    }

    @Test
    @DisplayName("Should get unread message count successfully")
    void shouldGetUnreadMessageCountSuccessfully() {
        // Given
        when(messageRepository.countUnreadMessagesForUser(1L)).thenReturn(5L);

        // When
        Long result = messageService.getUnreadMessageCount(1L);

        // Then
        assertThat(result).isEqualTo(5L);

        verify(messageRepository).countUnreadMessagesForUser(1L);
    }

    @Test
    @DisplayName("Should search messages successfully")
    void shouldSearchMessagesSuccessfully() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        List<Message> searchResults = Arrays.asList(testMessage);
        Page<Message> searchPage = new PageImpl<>(searchResults, pageable, 1);

        when(messageRepository.searchMessages("test", 1L, pageable)).thenReturn(searchPage);

        // When
        Page<MessageResponse> result = messageService.searchMessages("test", 1L, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getContent()).contains("test");

        verify(messageRepository).searchMessages("test", 1L, pageable);
    }

    @Test
    @DisplayName("Should handle empty message content")
    void shouldHandleEmptyMessageContent() {
        // Given
        SendMessageRequest emptyRequest = SendMessageRequest.builder()
                .content("")
                .chatId(1L)
                .build();

        // When & Then
        assertThatThrownBy(() -> messageService.sendMessage(emptyRequest, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Message content cannot be empty");
    }

    @Test
    @DisplayName("Should send message with attachment successfully")
    void shouldSendMessageWithAttachmentSuccessfully() {
        // Given
        Message messageWithAttachment = testMessage.toBuilder()
                .attachmentUrl("https://cloudinary.com/attachment.pdf")
                .attachmentType("document")
                .build();

        SendMessageRequest requestWithAttachment = SendMessageRequest.builder()
                .content("Check out this document")
                .chatId(1L)
                .attachmentUrl("https://cloudinary.com/attachment.pdf")
                .attachmentType("document")
                .build();

        when(chatRepository.findById(1L)).thenReturn(Optional.of(testChat));
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(messageRepository.save(any(Message.class))).thenReturn(messageWithAttachment);

        // When
        MessageResponse result = messageService.sendMessage(requestWithAttachment, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAttachmentUrl()).isEqualTo("https://cloudinary.com/attachment.pdf");
        assertThat(result.getAttachmentType()).isEqualTo("document");

        verify(messageRepository).save(any(Message.class));
    }
}
