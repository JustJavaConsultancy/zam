package tech.justjava.zam.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.justjava.zam.chat.entity.TownHall;

public interface TownHallRepository extends JpaRepository<TownHall, Long> {
}