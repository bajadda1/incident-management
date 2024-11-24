package ma.bonmyd.backendincident.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {
    //    @Value("${secret.signing.key}")
    private String SECRET_KEY = "7A619C292814B3EDDD7E73FBE6D67";
    //    @Value("${jwt.expiration.duration}")
    private long JWT_EXPIRATION = 10000;

    Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    String base64Key = Encoders.BASE64.encode(key.getEncoded());

    public String extractUsername(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    //generic method to extract different claims (one claim)
    private <T> T extractClaim(String jwt, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(jwt);
        return claimResolver.apply(claims);
    }

    //method to extract all different claims from a jwt
    private Claims extractAllClaims(String jwt) {
        return Jwts.parser().verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

//    public String generateToken(UserDetails userDetails) {
//        return generateToken(userDetails);
//    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(Map.of(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, JWT_EXPIRATION);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long jwtExpiration) {
        var authorities = userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return Jwts.
                builder()
                .claims(extraClaims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .subject(userDetails.getUsername())
                .signWith(getSignInKey())
                .claim("authorities", authorities)
                .compact();
    }

    public boolean isValidToken(String token, UserDetails userDetails) {
        final String username = this.extractUsername(token);
        return (Objects.equals(username, userDetails.getUsername()) && !isExpiredToken(token));
    }

    private boolean isExpiredToken(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return this.extractClaim(token, Claims::getExpiration);
    }

    //    private Key getSignInKey() {
//        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
    private SecretKey getSignInKey() {
        byte[] bytes = Decoders.BASE64.decode(base64Key);
        return Keys.hmacShaKeyFor(bytes);
    }
}

