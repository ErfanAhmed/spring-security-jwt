package net.therap.authService.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author erfan
 * @since 7/3/23
 */
@Table(name = "auth_user")
@Entity
@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@ToString
public class User extends Persistent implements UserDetails {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userSeq")
    @SequenceGenerator(name = "userSeq", sequenceName = "user_seq", allocationSize = 1)
    private int id;

    @Column(nullable = false, unique = true, length = 20)
    @NotBlank
    private String username;

    @Column(nullable = false, length = 255)
    @NotBlank
    private String password;

    @Column(nullable = false, unique = true, length = 50)
    @NotBlank
    private String email;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,}, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "user_authority",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id"))
    private Set<Authority> authoritySet;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Status status;

    public User() {
        roles = new HashSet<>();
        authoritySet = new HashSet<>();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        getRoles().forEach(
                role -> grantedAuthorities
                        .add(new SimpleGrantedAuthority(role.getRoleName()))
        );

        getAuthoritySet().forEach(
                authority -> grantedAuthorities.add(new SimpleGrantedAuthority(authority.getAuthorityName()))
        );

        return grantedAuthorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
