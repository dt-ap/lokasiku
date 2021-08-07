package org.lokasiku.apiservice.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.lokasiku.apiservice.config.AppConfig
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant
import java.util.*

@Service
class JwtService(val clock: Clock, val config: AppConfig) {
    fun createToken(username: String): String {
        val expiresAt = Date(Instant.now(clock).toEpochMilli() + config.jwt.expirationDuration)
        return JWT.create().withSubject(username)
            .withExpiresAt(expiresAt)
            .sign(Algorithm.HMAC512(config.jwt.secret))
    }

    fun validateToken(accessToken: String): DecodedJWT? {
        return JWT.require(Algorithm.HMAC512(config.jwt.secret)).build().verify(accessToken)
    }

    fun isDecodedExpired(decoded: DecodedJWT?): Boolean {
        return if (decoded != null)
            decoded.expiresAt.time <= Instant.now(clock).toEpochMilli()
        else
            false
    }
}