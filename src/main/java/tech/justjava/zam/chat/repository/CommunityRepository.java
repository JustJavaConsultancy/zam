package tech.justjava.zam.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.justjava.zam.chat.entity.Community;

public interface CommunityRepository extends JpaRepository<Community, Long> {
}