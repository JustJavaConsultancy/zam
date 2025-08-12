package tech.justjava.zam.chat.model;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConversationDTO {
    private Long id;
    private String title;
    private Boolean group;
    private LocalDateTime createdAt;
    private List<ChatMessageDTO> messages;
} 