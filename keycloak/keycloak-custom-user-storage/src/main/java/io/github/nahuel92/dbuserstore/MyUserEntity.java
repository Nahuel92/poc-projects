package io.github.nahuel92.dbuserstore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@NamedQueries({
        @NamedQuery(name = "getUserByUsername", query = "SELECT u FROM MyUserEntity u WHERE u.username = :username"),
        @NamedQuery(name = "getUserByEmail", query = "SELECT u FROM MyUserEntity u WHERE u.email = :email"),
        @NamedQuery(name = "getUserCount", query = "SELECT COUNT(u) FROM MyUserEntity u"),
        @NamedQuery(name = "getAllUsers", query = "SELECT u FROM MyUserEntity u"),
        @NamedQuery(name = "searchForUser", query = """
                                SELECT u FROM MyUserEntity u
                                WHERE LOWER(u.username) LIKE :search
                                    OR LOWER(u.firstName) LIKE :search
                                    OR LOWER(u.lastName) LIKE :search
                                    OR u.email LIKE :search
                                ORDER BY u.username
                """
        ),
})
@Entity
@Table(name = "my_users")
public class MyUserEntity {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "created_at")
    private LocalDateTime createdTimestamp;

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreatedTimestamp() {
        return createdTimestamp;
    }
}
