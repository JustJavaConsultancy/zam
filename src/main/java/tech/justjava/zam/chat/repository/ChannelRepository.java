package tech.justjava.zam.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.justjava.zam.chat.entity.Channel;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
}