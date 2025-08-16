package tech.justjava.zam.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.justjava.zam.chat.entity.SupportChannel;

public interface SupportChannelRepository extends JpaRepository<SupportChannel, Long> {
}