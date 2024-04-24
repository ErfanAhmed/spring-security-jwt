package net.therap.authService.dto;

import lombok.Data;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

/**
 * @author erfan
 * @since 9/20/23
 */
@Data
public class JwtTokenResponseDto {
    private String token;
    private String expireDate;

    public JwtTokenResponseDto(String token, Date expireDate) {
        this.token = token;
        setExpireDate(expireDate);
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = DateFormatUtils.format(expireDate, "dd/MM/yyyy HH:mm:ss");
    }
}
