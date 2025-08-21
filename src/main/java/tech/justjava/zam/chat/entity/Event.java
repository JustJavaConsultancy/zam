package tech.justjava.zam.chat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Event extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @OneToOne(optional = false)
    private Organization organization;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    private Community community;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    private ChatGroup chatGroup;
}
