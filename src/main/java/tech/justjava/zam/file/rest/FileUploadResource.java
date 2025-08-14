package tech.justjava.zam.file.rest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tech.justjava.zam.chat.ChatMessage;
import tech.justjava.zam.chat.service.ChatService;
import tech.justjava.zam.file.model.FileData;
import tech.justjava.zam.file.service.FileDataService;


@RestController
public class FileUploadResource {

    private final FileDataService fileDataService;
    private final ChatService chatService;

    public FileUploadResource(final FileDataService fileDataService, ChatService chatService) {
        this.fileDataService = fileDataService;
        this.chatService = chatService;
    }

    @PostMapping(
            value = "/fileUpload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<FileData> fileUpload(@RequestPart("file") final MultipartFile file) {
        final FileData tempFile = fileDataService.saveUpload(file);
        return ResponseEntity.ok(tempFile);
    }

    @GetMapping("/test")
    public ResponseEntity<?> test(){
        ChatMessage chat =  new ChatMessage();
        chat.setConversationId(1L);
        chat.setContent("Hello ");
        chat.setSenderId("85c5f3ca-cd54-4484-8efc-669d9e6faf61");
        chat.setReceiverId("51e4aed8-6c12-4efa-8638-2eaab2a0dda3");
        chat.setSenderName("Just Java");
//        chatService.newMessage(chat);
        var v = chatService.getConversations("85c5f3ca-cd54-4484-8efc-669d9e6faf61");
//        var v = chatService.createConversation(List.of("85c5f3ca-cd54-4484-8efc-669d9e6faf61","51e4aed8-6c12-4efa-8638-2eaab2a0dda3"));
        return ResponseEntity.ok(v);
    }

}
