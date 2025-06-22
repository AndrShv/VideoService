package org.example.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Getter
public class UserDetailsImpl implements UserDetails {

    private final UUID id;
    private final String email;
    private final String username;

    public UserDetailsImpl(UUID id, String email, String username) {
        this.id = id;
        this.email = email;
        this.username = username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // Без ролей
    }

    @Override
    public String getPassword() {
        return null; // Пароль не потрібен
    }

    @Override
    public String getUsername() {
        return email; // або username
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

