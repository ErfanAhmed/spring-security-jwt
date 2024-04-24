package net.therap.authService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.therap.authService.domain.Status;

import java.io.Serializable;
import java.util.List;

/**
 * @author erfan
 * @since 7/9/2023
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;

    private String username;

    private String password;

    private String email;

    private Status status;

    private List<Integer> roleIds;

    private List<Integer> authorityIds;
}
