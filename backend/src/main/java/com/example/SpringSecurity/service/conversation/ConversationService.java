package com.example.SpringSecurity.service.conversation;

import com.example.SpringSecurity.dto.mapper.ConversationMapper;
import com.example.SpringSecurity.dto.mapper.MessageMapper;
import com.example.SpringSecurity.dto.request.message.SendMessageRequestDTO;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.dto.response.conversation.ConversationDetailDTO;
import com.example.SpringSecurity.dto.response.conversation.ConversationPreviewDTO;
import com.example.SpringSecurity.dto.response.message.MessageDTO;
import com.example.SpringSecurity.enums.ConversationRole;
import com.example.SpringSecurity.enums.ConversationType;
import com.example.SpringSecurity.exception.AppException;
import com.example.SpringSecurity.model.ChatMessage;
import com.example.SpringSecurity.model.Conversation;
import com.example.SpringSecurity.model.ConversationParticipant;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.IChatMessageRepository;
import com.example.SpringSecurity.repository.IConversationParticipantRepository;
import com.example.SpringSecurity.repository.IConversationRepository;
import com.example.SpringSecurity.service.user.IUserValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConversationService implements IConversationService {

    private final IConversationRepository conversationRepository;
    private final IChatMessageRepository chatMessageRepository;
    private final IConversationParticipantRepository participantRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageMapper messageMapper;
    private final ConversationMapper conversationMapper;
    private final IUserValidationService userValidationService;

    @Override
    public ApiResponse<List<ConversationPreviewDTO>> getConversationForUser(Long userId) {
        // Thông tin của người đăng nhập
        User currentUser = userValidationService.findById(userId);

        // Tất cả hội thoại của người đăng nhập
        List<Conversation> conversations = conversationRepository.findConversationsByUserId(userId);

        List<ConversationPreviewDTO> dtos = conversations.stream()
                .map(convo -> {
                    // Lấy tin nhắn cuối cùng
                    ChatMessage lastMsg = chatMessageRepository
                            .findTopByConversationOrderByCreatedAtDesc(convo)
                            .orElse(null);

                    ConversationPreviewDTO dto = conversationMapper.toPreviewDTO(convo, lastMsg, currentUser);

                    // Nếu hội thoại là 1 1
                    if ("DIRECT_MESSAGE".equals(convo.getType().toString())) {
                        // Tìm  đối phương
                        User partner = convo.getParticipants().stream()
                                .map(ConversationParticipant::getUser)
                                .filter(u -> !u.getId().equals(currentUser.getId()))
                                .findFirst()
                                .orElse(null);

                        // Tìm thấy thì gán thông tin cho người kia
                        if (partner != null) {
                            dto.setUserId(partner.getId());
                            dto.setName(partner.getFullName());
                            dto.setAvatar(partner.getProfileImage() != null ? partner.getProfileImage().getUrl() : null);
                        }
                    }

                    return dto;
                })

                // đưa các cuộc trò chuyện có tin nhắn mới nhất lên đầu danh sách
                .sorted((c1, c2) -> {
                    if (c2.getLastMessageTimestamp() == null) return -1;
                    if (c1.getLastMessageTimestamp() == null) return 1;
                    return c2.getLastMessageTimestamp().compareTo(c1.getLastMessageTimestamp());
                })
                .toList();

        return new ApiResponse<>(200, true, "All conversations of user", dtos);
    }

    @Override
    public ApiResponse<List<MessageDTO>> getMessagesForConversation(Long conversationId, Long userId) {
        //  Kiểm tra thông tin người đăng nhập
        User currentUser = userValidationService.findById(userId);
        // Lấy message của cuộc trò chuyện đấy
        Conversation conversation = getConversationById(conversationId);

        // Kiểm tra Người dùng có thuộc convensation đó không
        checkUserIsParticipant(conversation, currentUser);

        // Lấy toàn bộ lịch sử tin nhắn của cuộc trò chuyện
        List<MessageDTO> messages = chatMessageRepository.findAllByConversationIdOrderByCreatedAtAsc(conversationId)
                .stream().map(messageMapper::mapToMessageDTO)
                .toList();

        return new ApiResponse<>(200, true, "Get messages of conversation", messages);
    }

    @Transactional
    @Override
    @CacheEvict(value = "user_conversations", key = "#userId")
    public ApiResponse<MessageDTO> sendMessage(Long conversationId, SendMessageRequestDTO request, Long userId) {
        // Kiểm tra người đăng nhập
        User sender = userValidationService.findById(userId);
        // Lấy cuộc trò chuyện
        Conversation conversation = getConversationById(conversationId);

        // check có trong cuộc trò chuyện không
        checkUserIsParticipant(conversation, sender);

        ChatMessage message = ChatMessage.builder()
                .content(request.getContent())
                .sender(sender)
                .conversation(conversation)
                .build();
        ChatMessage savedMessage = chatMessageRepository.save(message);

        MessageDTO messageDTO = messageMapper.mapToMessageDTO(savedMessage);

        // địa chỉ kênh mà tin nhắn sẽ được gửi tới
        messagingTemplate.convertAndSend("/topic/conversations/" + conversationId, messageDTO);

        return new ApiResponse<>(200, true, "Send message successfully", messageDTO);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = "user_conversations", key = "#userId"),
            @CacheEvict(value = "user_conversations", key = "#recipientId")
    })
    public ApiResponse<ConversationDetailDTO> startOrGetDirectConversation(Long recipientId, Long userId) {
        // Lấy user người dùng và người nhắn tin
        User sender = userValidationService.findById(userId);
        User recipient = userValidationService.findById(recipientId);

        // Check người dùng không được gửi tin nhắn cho bản thân
        if (sender.getId().equals(recipientId)) {
            throw new AppException("Cannot chat with yourself");
        }

        Conversation conversation = conversationRepository
                .findDirectConversationBetweenUsers(sender.getId(), recipient.getId())  // kiểm tra xem có cuộc trò chuyện chưa
                .orElseGet(() -> {
                    // Nếu chưa thì tạo cuộc trò chuyện

                    Conversation newConvo = Conversation.builder()
                            .type(ConversationType.DIRECT_MESSAGE)
                            .build();
                    conversationRepository.save(newConvo);

                    ConversationParticipant p1 = ConversationParticipant.builder()
                            .user(sender)
                            .conversation(newConvo)
                            .role(ConversationRole.MEMBER)
                            .build();
                    ConversationParticipant p2 = ConversationParticipant.builder()
                            .user(recipient)
                            .conversation(newConvo)
                            .role(ConversationRole.MEMBER)
                            .build();

                    // Lưu
                    participantRepository.saveAll(List.of(p1, p2));

                    // Set 2 người vào cuộc trò chuyện
                    newConvo.setParticipants(new ArrayList<>(List.of(p1, p2)));

                    return newConvo;
                });

        return new ApiResponse<>(200, true,
                "Conversation created successfully",
                conversationMapper.mapToConversationDetailDTO(conversation));
    }

    private Conversation getConversationById(Long id) {
        return conversationRepository.findById(id).orElseThrow(() -> new AppException("Conversation not found"));
    }

    private void checkUserIsParticipant(Conversation conversation, User user) {
        participantRepository.findByConversationAndUser(conversation, user)
                .orElseThrow(() -> new AppException("You are not a participant of this conversation"));
    }

}