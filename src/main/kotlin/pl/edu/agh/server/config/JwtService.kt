package pl.edu.agh.server.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.security.Key
import java.util.*

@Service
class JwtService {
    private val SECRET_KEY: String =
        "f7a44a02a4932ce59a078f5a08cc790df0900c5bd9d6cc7218c78f8d3df6ddb7" // TODO move to env

    fun extractUsername(token: String): String {
        println(token + "extractUsername")
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
        println(token + "isTokenExpired")
        return extractClaims(token, Claims::getExpiration).before(Date(System.currentTimeMillis()))
    }

    fun generateToken(extraClaims: Map<String, Any>, userDetails: UserDetails): String {
        return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.username)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 24))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact()
    }

    fun extractAllClaims(token: String): Claims {
        println(token)
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).body
    }

    private fun getSigningKey(): Key {
        val keyBytes = Decoders.BASE64.decode(SECRET_KEY)
        return Keys.hmacShaKeyFor(keyBytes)
    }
}
