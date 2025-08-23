package tech.justjava.zam.chat.dto;

import lombok.Data;

@Data
public class CreateChatDTO {

    private String groupName;
    private Long communityId;
    private String channelName;
    private String channelDescription;
    private String townHallName;
    private String townHallDescription;

}
