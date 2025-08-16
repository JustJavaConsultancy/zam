package tech.justjava.zam.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.justjava.zam.chat.entity.ChatGroup;

public interface ChatGroupRepository extends JpaRepository<ChatGroup, Long> {
}