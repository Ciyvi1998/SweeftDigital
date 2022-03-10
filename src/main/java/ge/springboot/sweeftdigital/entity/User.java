package ge.springboot.sweeftdigital.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.sun.istack.NotNull;
import ge.springboot.sweeftdigital.enums.Gender;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "\"user\"")
@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String surname;
    @NotNull
    private String email;
    @NotNull
    private String password;
    private int age;
    @Enumerated(value = EnumType.STRING)
    private Gender gender;
    private Date birthDate;
    private boolean enable = false;
    private boolean locked = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    List<Role> roles = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "server_id")
    private Server server;

    public User(Integer id, String name, String surname, String email, String password, int age, Gender gender, Date birthDate, boolean enable, boolean locked, List<Role> roles, Server server) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.age = age;
        this.gender = gender;
        this.birthDate = birthDate;
        this.enable = enable;
        this.locked = locked;
        this.roles = roles;
        this.server = server;
    }

    public User() {

    }


}
