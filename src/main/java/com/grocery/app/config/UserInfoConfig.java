package com.grocery.app.config;

import com.grocery.app.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoConfig implements UserDetails {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String username;
    private String password;
    private String role;
    private Boolean isActive;
    private Set<String> authProviderType=new HashSet<>();

    public UserInfoConfig(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.role = user.getRole().getName();
        this.isActive = user.getIsActivated();
        this.id = user.getId();

    }
    @Override
    public List<GrantedAuthority> getAuthorities() {
        return null;
    }


    @Override
    public boolean isAccountNonExpired() {
        return isActive;
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
