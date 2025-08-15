package tech.justjava.zam.chat.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import tech.justjava.zam.chat.entity.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUserId(String userId);

    User findByUserId(String userId);

    Set<User> findAllByUserIdIn(Collection<String> userIds);
    @Override
    default List<User> findAll() {
        return findAll(Sort.by(Sort.Direction.ASC, "firstName"));
    }

    User findByEmail(String email);
}