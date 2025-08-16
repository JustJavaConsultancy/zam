package tech.justjava.zam.chat.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.AccessType;

import java.io.Serializable;

/**
 * DTO for {@link Organization}
 */
@Value
@Data
@AllArgsConstructor
public class OrganizationDto implements Serializable {
    Long id;
    String name;
    String description;
    ChannelDto channel;
    SupportChannelDto supportChannel;
    TownHallDto townHall;


    /**
     * DTO for {@link Channel}
     */
    @Value
    @Data
    @AllArgsConstructor
    public static class ChannelDto implements Serializable {
        Long id;
        String name;
        String description;
    }

    /**
     * DTO for {@link SupportChannel}
     */
    @Value
    @Data
    @AllArgsConstructor
    public static class SupportChannelDto implements Serializable {
        Long id;
        String name;
        String description;
    }

    /**
     * DTO for {@link TownHall}
     */
    @Value
    @Data
    @AllArgsConstructor
    public static class TownHallDto implements Serializable {
        Long id;
        String name;
        String description;
    }
}