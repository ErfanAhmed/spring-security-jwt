package net.therap.authService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import net.therap.authService.domain.User;

import java.util.List;

/**
 * @author erfan
 * @since 8/13/23
 */
@Data
@Builder
@AllArgsConstructor
public class UserResponseDto {

    private Integer id;
    private String username;
    private String email;
    private String status;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;
    private List<String> authorities;

    public static UserResponseDto prepareUserResponse(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .status(user.getStatus().name())
                .authorities(user.getAuthorities()
                        .stream()
                        .map(item -> item.getAuthority())
                        .toList())
                .accountNonExpired(user.isAccountNonExpired())
                .accountNonLocked(user.isAccountNonLocked())
                .credentialsNonExpired(user.isCredentialsNonExpired())
                .enabled(user.isEnabled())
                .build();
    }
}
