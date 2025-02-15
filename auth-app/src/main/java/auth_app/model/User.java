package auth_app.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "users") // Rename the table to "users"
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles", // Rename the join table
            joinColumns = @JoinColumn(name = "user_id"), // Reference the "users" table
            inverseJoinColumns = @JoinColumn(name = "role_id") // Reference the "roles" table
    )
    private Set<Role> roles;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    //

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles; // Roles now implement GrantedAuthority
    }
}