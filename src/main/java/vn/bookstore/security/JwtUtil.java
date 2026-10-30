
package vn.bookstore.security;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Sinh và kiểm tra JWT cho REST API /api/**
 *
 * JSP/web vẫn sử dụng Session của Spring Security.
 */
@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    /**
     * Tạo signing key từ secret.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                jwtSecret.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Tạo JWT.
     */
    public String generateToken(
            String username,
            String roleName,
            Integer userId,
            Integer storeId) {

        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(username)
                .claim("role", roleName)
                .claim("userId", userId)
                .claim("storeId", storeId)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Lấy username từ JWT.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Lấy role từ JWT.
     */
    public String extractRole(String token) {
        return extractAllClaims(token)
                .get("role", String.class);
    }

    /**
     * Lấy userId từ JWT.
     */
    public Integer extractUserId(String token) {
        return extractAllClaims(token)
                .get("userId", Integer.class);
    }

    /**
     * Lấy storeId từ JWT.
     */
    public Integer extractStoreId(String token) {
        return extractAllClaims(token)
                .get("storeId", Integer.class);
    }

    /**
     * Lấy thời gian hết hạn.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Lấy claim bất kỳ.
     */
    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver) {

        Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    /**
     * Parse và kiểm tra chữ ký JWT.
     *
     * JJWT 0.12.x sử dụng:
     * Jwts.parser()
     *     .verifyWith(...)
     *     .build()
     */
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Kiểm tra token có hợp lệ với username hay không.
     */
    public boolean isTokenValid(
            String token,
            String username) {

        try {

            String extractedUsername =
                    extractUsername(token);

            return extractedUsername != null
                    && extractedUsername.equals(username)
                    && !isTokenExpired(token);

        } catch (JwtException | IllegalArgumentException e) {

            return false;
        }
    }

    /**
     * Kiểm tra token hết hạn.
     */
    private boolean isTokenExpired(String token) {

        return extractExpiration(token)
                .before(new Date());
    }

    /**
     * Kiểm tra JWT mà không ném exception ra ngoài.
     */
    public boolean validateTokenSilently(String token) {

        try {

            extractAllClaims(token);

            return true;

        } catch (JwtException
                | IllegalArgumentException e) {

            return false;
        }
    }
}
