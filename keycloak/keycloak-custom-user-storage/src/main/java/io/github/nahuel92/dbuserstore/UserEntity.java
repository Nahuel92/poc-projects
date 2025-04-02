package io.github.nahuel92.dbuserstore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;

@NamedQueries({
        @NamedQuery(name = "getUserByUsername", query = "SELECT u FROM UserEntity u WHERE u.username = :username"),
        @NamedQuery(name = "getUserByEmail", query = "SELECT u FROM UserEntity u WHERE u.email = :email"),
        @NamedQuery(name = "getUserCount", query = "SELECT COUNT(u) FROM UserEntity u"),
        @NamedQuery(name = "getAllUsers", query = "SELECT u FROM UserEntity u"),
        @NamedQuery(name = "searchForUser", query = """
                                SELECT u FROM UserEntity u
                                WHERE (
                                LOWER(u.username) LIKE :search OR \
                                LOWER(u.firstName) LIKE :search OR \
                                LOWER(u.lastName) LIKE :search OR \
                                u.email LIKE :search
                                ) ORDER BY u.username
                """
        ),
})
@Entity
public class UserEntity {
    @Id
    private String id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Long createdTimestamp;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String name) {
        this.firstName = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
}
