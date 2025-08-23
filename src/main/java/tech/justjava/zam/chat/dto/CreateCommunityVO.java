package tech.justjava.zam.chat.dto;

import lombok.Data;

@Data
public class CreateCommunityVO {
    private String communityName;
    private String communityDescription;
    private String channelName;
    private String channelDescription;
    private String townHallName;
    private String townHallDescription;
    private String userEmail;
}
