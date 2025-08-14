package tech.justjava.zam.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.justjava.zam.chat.entity.Organization;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
}