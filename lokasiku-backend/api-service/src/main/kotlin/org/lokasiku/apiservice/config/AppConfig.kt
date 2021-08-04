package org.lokasiku.apiservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "lokasiku")
class AppConfig {
    var jwt = JwtConfig()

    class JwtConfig {
        var secret = "Th1s1sD3faultV4lue"
        var expirationDuration = 604_800_000 // 7 Days
    }
}
