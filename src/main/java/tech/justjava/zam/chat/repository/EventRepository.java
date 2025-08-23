package tech.justjava.zam.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.justjava.zam.chat.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}