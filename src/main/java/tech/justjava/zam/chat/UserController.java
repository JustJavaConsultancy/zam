package tech.justjava.zam.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.justjava.zam.account.AuthenticationManager;
import tech.justjava.zam.chat.dto.CreateOrgDTO;
import tech.justjava.zam.chat.service.ChatService;
import tech.justjava.zam.keycloak.KeycloakService;
import tech.justjava.zam.keycloak.UserDTO;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final KeycloakService keycloakService;
    private final ChatService chatService;
    private final OnlineEventListener onlineEventListener;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getUsers(){
        List<UserDTO> users = chatService.getUsers();
        return ResponseEntity.ok().body(users);
    }

    @PostMapping("/conversations")
    public ResponseEntity<?> createConversation(@RequestParam List<String> conversationIds){
        String conversationId = chatService.createConversation(conversationIds).getId().toString();
        return ResponseEntity.ok(conversationId);
    }

    @GetMapping("/conversations")
    public ResponseEntity<?> getConversations(/*@PathVariable String userId*/){
        String userId = authenticationManager.get("sub").toString();
        return ResponseEntity.ok(chatService.getConversations(userId));
    }

    @PostMapping("/create-organization")
    public ResponseEntity<?> createOrganization(@RequestParam CreateOrgDTO dto){
        try {
            chatService.createOrganization(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/online-users")
    public ResponseEntity<?> getOnlineUsers(){
        return ResponseEntity.ok(onlineEventListener.getOnlineUsers());
    }
}
