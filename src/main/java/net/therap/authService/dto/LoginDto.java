package net.therap.authService.dto;

import java.io.Serializable;

/**
 * @author erfan
 * @since 7/9/23
 */
public class LoginDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;

    private String password;

    public LoginDto() {
    }

    public LoginDto(String username, String password) {
        this.username = username;
        this.password = password;
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
}
