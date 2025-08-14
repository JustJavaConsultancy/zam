package tech.justjava.zam.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    private Long conversationId;
    private String senderId;
    private String receiverId;
    private String content;
    private String receiverName;
    private String senderName;
    private Boolean channel;
    private Boolean townHall;
}
