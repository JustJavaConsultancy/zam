package tech.justjava.zam.chat;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.justjava.zam.account.AuthenticationManager;
import tech.justjava.zam.chat.dto.CreateOrgDTO;
import tech.justjava.zam.chat.service.ChatService;
import tech.justjava.zam.keycloak.KeycloakService;
import tech.justjava.zam.keycloak.UserDTO;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api-v")
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
    public ResponseEntity<?> createOrganization(@RequestBody CreateOrgDTO dto){
        try {
            var o = chatService.createOrganization(dto);
            return ResponseEntity.ok(o);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("organization/add-user")
    public ResponseEntity<?> addUser(@RequestParam String email, @RequestParam Long orgId){
        try {
            chatService.addUserToOrganization(email, orgId);
            return ResponseEntity.ok("User successfully added");
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.ok(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/online-users")
    public ResponseEntity<?> getOnlineUsers(){
        return ResponseEntity.ok(onlineEventListener.getOnlineUsers());
    }


    @PostMapping("/auth")
    public ResponseEntity<?> authenticate(@RequestParam String username, @RequestParam String password){
        Map o = (Map) keycloakService.authenticate(username, password);
        return ResponseEntity.ok(o);
    }

    @GetMapping("/organization")
    public ResponseEntity<?> getOrganizations(){
        return ResponseEntity.ok(chatService.getOrganizations());
    }

    @GetMapping("/organization/members/{orgId}")
    public ResponseEntity<?> getOrganizationMembers(@PathVariable Long orgId){
        return ResponseEntity.ok(chatService.getOrgMembers(orgId));
    }
}
