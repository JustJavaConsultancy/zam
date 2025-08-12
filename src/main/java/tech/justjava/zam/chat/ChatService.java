package tech.justjava.zam.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.justjava.zam.chat.dto.ConversationDto;
import tech.justjava.zam.chat.entity.Conversation;
import tech.justjava.zam.chat.entity.Message;
import tech.justjava.zam.chat.entity.User;
import tech.justjava.zam.chat.repository.ConversationRepository;
import tech.justjava.zam.chat.repository.MessageRepository;
import tech.justjava.zam.chat.repository.UserRepository;
import tech.justjava.zam.keycloak.UserDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public List<UserDTO> getUsers() {
        return mapUsersToDTO(userRepository.findAll());
    }
    @Transactional
    public List<ConversationDto> getConversations(String userId) {
        List<Conversation> conversations = conversationRepository.findAllByMembers_UserId(userId);
        return mapConversationsToDTO(conversations, userId);
    }

    private List<UserDTO> mapUsersToDTO(List<User> users){
        List<UserDTO> dtos = new ArrayList<>();
        for (User user : users) {
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(user.getUserId());
            userDTO.setFirstName(user.getFirstName());
            userDTO.setLastName(user.getLastName());
            userDTO.setEmail(user.getEmail());
            userDTO.setStatus(user.getStatus());
            userDTO.setGroup(user.getUserGroup() != null? user.getUserGroup().getGroupName(): "");
            userDTO.setAvatar(user.getAvatar());
            dtos.add(userDTO);
        }
        return dtos;
    }

    @Transactional
    public Conversation createConversation(List<String> conversationIds) {
        Optional<Conversation> conversation1 = conversationRepository.findConversationByExactUserIds(conversationIds, conversationIds.size());
        if (conversation1.isPresent()) {
            return conversation1.get();
        }
        Set<User> users = userRepository.findAllByUserIdIn(conversationIds);
        Conversation conversation = new Conversation();
        if (users.size() > 2) {
            conversation.setGroup(true);
        }
        conversation = conversationRepository.save(conversation);
        conversation.setMembers(users);
        conversation = conversationRepository.save(conversation);
        return conversation;
    }

    @Async
    @Transactional
    public void newMessage(ChatMessage chatMessage) {
//        Optional<Conversation> conversation = conversationRepository.findById(chatMessage.getConversationId());
        List<String> userIds = List.of(chatMessage.getSenderId(),chatMessage.getReceiverId());
        Optional<Conversation> conversation = conversationRepository
                .findConversationByExactUserIds(userIds, 2);
//        User user = userRepository.findByUserId(chatMessage.getSenderId());
        if (conversation.isPresent()) {
            Message message = new Message();
            message.setConversation(conversation.get());
            message.setSenderId(chatMessage.getSenderId());
            message.setContent(chatMessage.getContent());
            conversation.get().getMessages().add(message);
//            user.getMessages().add(message);
            messageRepository.save(message);

        }else {
            Conversation newConversation = createConversation(userIds);
            Message message = new Message();
            message.setConversation(newConversation);
            message.setSenderId(chatMessage.getSenderId());
            message.setContent(chatMessage.getContent());
            newConversation.getMessages().add(message);
            messageRepository.save(message);
        }
    }

    private List<ConversationDto> mapConversationsToDTO(List<Conversation> conversations, String userId) {
        List<ConversationDto> dtos = new ArrayList<>();
        for (Conversation conversation : conversations) {
            ConversationDto conversationDto = new ConversationDto();
            conversationDto.setId(conversation.getId());
            conversationDto.setGroup(conversation.getGroup());
            conversationDto.setCreatedAt(conversation.getCreatedAt());
            conversationDto.setMessages(mapMessagesToDTO(conversation.getMessages(), userId));
            if (conversation.getGroup()) {
                conversationDto.setTitle(conversation.getTitle());
            }else {
                conversationDto.setReceiverId(conversation.getReceiverId(userId));
                conversationDto.setReceiverName(conversation.getReceiverName(userId));
                conversationDto.setTitle(conversation.getReceiverName(userId));
            }
            dtos.add(conversationDto);
        }
        return dtos;
    }

    private List<ConversationDto.MessageDto> mapMessagesToDTO(List<Message> messages, String userId) {
        List<ConversationDto.MessageDto> messageDtos = new ArrayList<>();
        for (Message m : messages) {
            ConversationDto.MessageDto messageDto = new ConversationDto.MessageDto();
            messageDto.setContent(m.getContent());
            messageDto.setSender(m.getSender(userId));
            messageDto.setSentAt(m.getSentAt());
            messageDtos.add(messageDto);
        }
        return messageDtos;
    }

    @Transactional
    public String deleteConversation(Long conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId).orElseThrow(() -> new RuntimeException("Not found"));
        conversation.getMembers().clear();
        conversationRepository.save(conversation);
        conversationRepository.delete(conversation);
        return null;
    }
}
