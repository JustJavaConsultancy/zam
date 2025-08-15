package tech.justjava.zam.chat.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;

import java.util.Set;

@Getter
@Setter
@Entity
@JsonIgnoreProperties({"organizationAdmin"})
public class Organization extends AuditableEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @OneToOne
    private Channel channel;

    @OneToOne
    private SupportChannel supportChannel;

    @OneToOne
    private TownHall townHall;

    @JsonBackReference
    @OneToOne
    private User organizationAdmin;

    @JsonIgnore
    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    private Set<User> users;
}
