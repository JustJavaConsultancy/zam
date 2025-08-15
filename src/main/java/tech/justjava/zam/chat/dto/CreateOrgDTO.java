package tech.justjava.zam.chat.dto;

import lombok.Data;

@Data
public class CreateOrgDTO {

    private String orgName;
    private String orgDescription;
    private String channelName;
    private String channelDescription;
    private String supportChannelName;
    private String supportChannelDescription;
    private String townHallName;
    private String townHallDescription;
    private String adminEmail;

}
