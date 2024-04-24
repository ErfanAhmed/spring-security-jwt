package net.therap.authService.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.therap.authService.jwt.JwtTokenService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author erfan
 * @since 9/25/23
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class Scheduler {

    private final JwtTokenService tokenService;

    @Scheduled(cron = "0 0 1 * * ?")
    public void removeExpiredTokenFromBlacklist() {
        log.info("Started Cron to remove expired token from blacklist");
        tokenService.removeExpiredTokens(new Date().getTime());
    }
}
