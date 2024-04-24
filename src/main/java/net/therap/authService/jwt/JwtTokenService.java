package net.therap.authService.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author erfan
 * @since 8/17/23
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class JwtTokenService {

    /**
     * room for improvements -
     * use zset: (exp-time token); scan periodical scan to remove expired token
     */
    private static final String WHITELIST_KEY = "whitelist:token";
    private static final String BLACKLIST_KEY = "blacklist:token";
    private static final String KEY_SEPARATOR = "-";

    @Value("${jwt.token-validity}")
    private long EXPIRE_TIME;

    private final RedisTemplate<String, Object> redisTemplate;


    public JwtToken get(String userName) {

        JwtToken token = (JwtToken) redisTemplate.opsForHash().get(
                WHITELIST_KEY.concat(KEY_SEPARATOR).concat(userName), userName);

        return token;
    }

    public void save(String username, JwtToken token) {
        String key = WHITELIST_KEY.concat(KEY_SEPARATOR).concat(username);

        redisTemplate.opsForHash().put(key, username, token);
        redisTemplate.expire(key, EXPIRE_TIME, TimeUnit.SECONDS);
    }

    public boolean isValidToken(String token) {
        return Objects.isNull(redisTemplate.opsForZSet().score(BLACKLIST_KEY, token));
    }

    public void invalidate(String username, long expireTime) {
        JwtToken token = get(username);

        if (Objects.isNull(token)) {
            throw new InvalidDataAccessApiUsageException("No such token exists!");
        }

        redisTemplate.opsForHash().delete(
                WHITELIST_KEY.concat(KEY_SEPARATOR).concat(username),
                username);

        redisTemplate.opsForZSet()
                .add(BLACKLIST_KEY, token.getToken(), expireTime);
    }

    public List<String> getExpiredTokens(long score) {
        List<String> expiredTokens = redisTemplate.opsForZSet()
                .rangeByScoreWithScores(BLACKLIST_KEY, Double.NEGATIVE_INFINITY, score)
                .stream()
                .map(obj -> obj.getValue().toString())
                .collect(Collectors.toList());

        return expiredTokens;
    }

    public void removeExpiredTokens(long score) {
        long count = redisTemplate.opsForZSet().removeRangeByScore(BLACKLIST_KEY, Double.NEGATIVE_INFINITY, score);

        if (count > 0) {
            log.info("Removed {} expired tokens", count);
        }
    }
}
