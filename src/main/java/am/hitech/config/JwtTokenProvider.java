package am.hitech.config;

import am.hitech.util.exceptions.JwtAuthenticationException;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {
    @Autowired
    private UserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.header}")
    private String authorizationHeader;
    @Value("${jwt.expiration}")
    private long validityInMilliseconds;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String username, String password) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("password", password);
        long date = System.currentTimeMillis() * 1000;
        Date now = new Date(date);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date validity = new Date(now.getTime() + validityInMilliseconds * 100);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public boolean validateToken(String token) throws JwtAuthenticationException {
        try {

            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);

            return !claimsJws.getBody().getExpiration().before(new Date(System.currentTimeMillis() + validityInMilliseconds));
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("JWT token is expired or invalid", HttpStatus.UNAUTHORIZED);

        }
    }


    public Authentication getAuthentication(String token) {
        UserDetails details = this.userDetailsService.loadUserByUsername(getUsername(token));

        return new UsernamePasswordAuthenticationToken(details, "", details.getAuthorities());

    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(authorizationHeader);
        return resolveToken(token);
    }

    public String resolveToken(String token) {
        return token == null ? null : token.replaceAll("Bearer", "").trim();
    }
}
