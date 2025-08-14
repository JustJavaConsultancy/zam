package tech.justjava.zam.chat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import tech.justjava.zam.keycloak.UserGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    private String firstName;

    private String lastName;

    private String email;

    private Boolean status;

    @Transient
    private Boolean online;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORG_ID")
    private Organization organization;

    @ManyToMany(mappedBy = "users", fetch = FetchType.LAZY)
    private Set<ChatGroup> chatGroup = new HashSet<>();

//    @ElementCollection(targetClass = Roles.class, fetch = FetchType.EAGER)
//    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
//    @Column(name = "role")
//    @Enumerated(EnumType.STRING)
//    private Set<Roles> roles = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "user_group_id")
    private UserGroup userGroup;

    @ManyToMany(mappedBy = "members", fetch = FetchType.LAZY)
    private List<Conversation> conversations = new ArrayList<>();

    public String getName() {
        return firstName+" "+lastName;
    }
    public String getStatus() {
        return status?"Enabled":"Disabled";
    }

    public String getAvatar() {
        return String.valueOf(this.firstName.charAt(0));
    }
}
