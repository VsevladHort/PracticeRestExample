package rest_api.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.xml.bind.DatatypeConverter;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

/*
 *  source: https://stormpath.com/blog/jwt-java-create-verify
 */
public class AuthToken {
    private static final Key signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public AuthToken(String key) {
        //this.key = key;
    }

    //Sample method to construct a JWT
    public String createJWT(String id, String issuer, String subject, long ttlMillis) {

        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //We will sign our JWT with our ApiKey secret
        // byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(key);
        //Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(signingKey, signatureAlgorithm);

        //if it has been specified, let's add the expiration
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    /**
     * @return null if not valid
     */
    public String parseJWT(String jwt) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(signingKey).build()
                    .parseClaimsJws(jwt).getBody();
            return claims.getId();
        } catch (Exception e) {
            return null;
        }
    }
}
