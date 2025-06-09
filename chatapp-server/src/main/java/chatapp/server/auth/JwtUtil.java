package chatapp.server.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

import java.time.Instant;

import java.security.Key;
import java.util.Date;

public class JwtUtil {
    private static Key key;
    private static final long ACCESS_EXP_MS  = 15 * 60_000;
    private static final long REFRESH_EXP_MS = 30L * 24*60*60_000;

    public static void init(Key signingKey) {
        key = signingKey;
    }
    public String generateAccessToken(String sub) {
        return buildToken(sub, ACCESS_EXP_MS, key);
    }
    public String generateRefreshToken(String sub) {
        return buildToken(sub, REFRESH_EXP_MS, key);
    }

    private String buildToken(String sub, long ttl, Key key) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(sub)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + ttl))
                .signWith(key)
                .compact();
    }

    private static Jws<Claims> validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        } catch (JwtException e) {
            return null;
        }
    }

    public static String getUsernameFromToken(String token) {
        Jws<Claims> claims = validateToken(token);
        return (claims != null) ? claims.getBody().getSubject() : null;
    }

    public static boolean isTokenUpToDate(String token) {
        Jws<Claims> claims = validateToken(token);
        return claims != null && claims.getBody().getExpiration().before(new Date());
    }

    public Instant getRefreshExpiry() {
        return Instant.now().plusMillis(REFRESH_EXP_MS);
    }
}
