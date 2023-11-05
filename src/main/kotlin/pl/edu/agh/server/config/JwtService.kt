package pl.edu.agh.server.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.security.Key
import java.util.*

@Service
class JwtService {

    @Value("\${application.security.jwt.secret}")
    lateinit var secretKey: String // create bean for creating secret key

    @Value("\${application.security.jwt.expirationTimeMs}")
    lateinit var accessTokenExpirationTime: Number // create bean for creating secret key

    @Value("\${application.security.jwt.refresh-token.expirationTimeMs}")
    lateinit var refreshExpirationTime: Number // create bean for creating secret key

    fun extractUsername(token: String): String {
        return extractClaims(token, Claims::getSubject)
    }

    fun <T> extractClaims(token: String, claimsResolver: (Claims) -> T): T {
        val claims = extractAllClaims(token)
        return claimsResolver(claims)
    }

    fun generateToken(userDetails: UserDetails): String {
        return generateToken(emptyMap(), userDetails)
    }

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return username == userDetails.username && !isTokenExpired(token)
    }

    fun isTokenExpired(token: String): Boolean {
        return extractClaims(token, Claims::getExpiration).before(Date(System.currentTimeMillis()))
    }

    fun generateToken(extraClaims: Map<String, Any>, userDetails: UserDetails): String {
        return createToken(extraClaims, userDetails, accessTokenExpirationTime.toLong())
    }

    fun generateRefreshToken(userDetails: UserDetails): String {
        return createToken(emptyMap(), userDetails, refreshExpirationTime.toLong())
    }

    fun createToken(extraClaims: Map<String, Any>, userDetails: UserDetails, expirationTime: Long): String {
        return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.username)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + expirationTime))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact()
    }

    fun extractAllClaims(token: String): Claims {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).body
    }

    private fun getSigningKey(): Key {
        val keyBytes = Decoders.BASE64.decode(secretKey)
        return Keys.hmacShaKeyFor(keyBytes)
    }
}
