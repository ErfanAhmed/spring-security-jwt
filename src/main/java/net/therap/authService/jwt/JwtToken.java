package net.therap.authService.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author erfan
 * @since 8/17/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class JwtToken implements Serializable {

    private String token;

//    private Date lastLoginDate;

    //todo: expire time/ttl; cron to remove all token from blacklist
}
