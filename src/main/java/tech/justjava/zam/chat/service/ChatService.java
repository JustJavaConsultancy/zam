package tech.justjava.zam.chat.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.justjava.zam.account.AuthenticationManager;
import tech.justjava.zam.chat.ChatMessage;
import tech.justjava.zam.chat.dto.ConversationDto;
import tech.justjava.zam.chat.dto.CreateOrgDTO;
import tech.justjava.zam.chat.entity.Channel;
import tech.justjava.zam.chat.entity.Conversation;
import tech.justjava.zam.chat.entity.Message;
import tech.justjava.zam.chat.entity.Organization;
import tech.justjava.zam.chat.entity.OrganizationDto;
import tech.justjava.zam.chat.entity.SupportChannel;
import tech.justjava.zam.chat.entity.TownHall;
import tech.justjava.zam.chat.entity.User;
import tech.justjava.zam.chat.repository.ChannelRepository;
import tech.justjava.zam.chat.repository.ConversationRepository;
import tech.justjava.zam.chat.repository.MessageRepository;
import tech.justjava.zam.chat.repository.OrganizationRepository;
import tech.justjava.zam.chat.repository.SupportChannelRepository;
import tech.justjava.zam.chat.repository.TownHallRepository;
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
    private final AuthenticationManager authenticationManager;
    private final ChannelRepository channelRepository;
    private final TownHallRepository townHallRepository;
    private final SupportChannelRepository supportChannelRepository;
    private final OrganizationRepository organizationRepository;
    private final SimpMessagingTemplate messagingTemplate;


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

    public void sendTownHallMessage(ChatMessage message) {
        User user = userRepository.findByUserId(message.getSenderId());
        TownHall townHall = user.getOrganization().getTownHall();
        String destination = "/topic/townhall/" + townHall.getId();
        messagingTemplate.convertAndSend(destination, message);
    }

    public void sendChannelMessage(ChatMessage message) {
        User user = userRepository.findByUserId(message.getSenderId());
        Channel channel = user.getOrganization().getChannel();
        if (user.equals(user.getOrganization().getOrganizationAdmin())) {
            String destination = "/topic/townhall/" + channel.getId();
            messagingTemplate.convertAndSend(destination, message);
        }
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

    @Transactional
    public Organization createOrganization(CreateOrgDTO dto){
        User user;
        if (dto.getAdminEmail() == null || dto.getAdminEmail().isEmpty()) {
            String userId = (String) authenticationManager.get("sub");
             user= userRepository.findByUserId(userId);
            if (user == null) {
                throw new EntityNotFoundException("User not found with ID: " + userId);
            }
        }else {
            user = userRepository.findByEmail(dto.getAdminEmail());
        }
        Channel channel = new Channel();
        channel.setName(dto.getChannelName());
        channel.setDescription(dto.getChannelDescription());
        channel = channelRepository.save(channel);

        TownHall townHall = new TownHall();
        townHall.setName(dto.getTownHallName());
        townHall.setDescription(dto.getTownHallDescription());
        townHall = townHallRepository.save(townHall);

        SupportChannel supportChannel = new SupportChannel();
        supportChannel.setName(dto.getSupportChannelName());
        supportChannel.setDescription(dto.getSupportChannelDescription());
        supportChannel = supportChannelRepository.save(supportChannel);

        Organization organization = new Organization();
        organization.setName(dto.getOrgName());
        organization.setDescription(dto.getOrgDescription());
        organization.setChannel(channel);
        organization.setTownHall(townHall);
        organization.setSupportChannel(supportChannel);
        organization.setOrganizationAdmin(user);
        organization = organizationRepository.save(organization);
        user.setOrganization(organization);
        userRepository.save(user);
        organization.setOrganizationAdmin(null);
        organization.setUsers(null);
        return organization;
    }

    public void addUserToOrganization(String email, Long orgId){
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new EntityNotFoundException("Organization does not exist"));
        User user = userRepository.findByEmail(email);
        user.setOrganization(organization);
        userRepository.save(user);
    }

    public Object getOrgMembers(Long orgId) {
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new EntityNotFoundException("Organization does not exist"));
        Set<User> users = organization.getUsers();
        return mapUsersToDTO(users.stream().toList());
    }

    public Object getOrganizations(){

        List<Organization> orgs =  organizationRepository.findAll();
        for (Organization org : orgs) {
            org.setOrganizationAdmin(null);
            org.setUsers(null);
        }
        return orgs;
    }

//    private OrganizationDto mapOrg (Organization org){
//        OrganizationDto dto = new OrganizationDto();
//
//    }
}
