package vn.bookstore.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import vn.bookstore.entity.User;

public class UserPrincipal implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Integer getUserId() {
        return user.getUserId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        if (user.getRole() == null ||
            user.getRole().getRoleName() == null) {

            return List.of();
        }

        String roleName =
                user.getRole()
                    .getRoleName()
                    .trim()
                    .toUpperCase();

        /*
         * ADMIN      -> ROLE_ADMIN
         * ROLE_ADMIN -> ROLE_ADMIN
         */
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }

        return List.of(
            new SimpleGrantedAuthority(roleName)
        );
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getStatus() != 2;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == 1;
    }
}
