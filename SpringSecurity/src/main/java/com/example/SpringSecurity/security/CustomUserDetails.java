package com.example.SpringSecurity.security;

import com.example.SpringSecurity.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {
    private final Long userId;
    private final String email;
    private final String fullName;
    private final String password;
    private final String avatarUrl;

    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Long userId, String email, String fullName, String password, String avatarUrl,
                             Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.password = password;
        this.avatarUrl = avatarUrl;
        this.authorities = authorities;
    }

    public static CustomUserDetails fromUserEntity(User user) {
        String avatarUrl = (user.getProfileImage() != null)
                ? user.getProfileImage().getUrl()
                : null;

        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPassword(),
                avatarUrl,
                List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }

    public CustomUserDetails withUserId(Long userId) {
        return new CustomUserDetails(
                userId,
                this.getEmail(),
                this.getFullName(),
                this.getPassword(),
                this.getAvatarUrl(),
                this.getAuthorities()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
