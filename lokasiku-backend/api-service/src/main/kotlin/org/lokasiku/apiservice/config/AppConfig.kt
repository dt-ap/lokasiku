package org.lokasiku.apiservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "lokasiku")
class AppConfig {
    var jwt = JwtConfig()

    class JwtConfig {
        var secret = "Th1s1sD3faultV4lue"
        var expirationDuration = 8_640_0000
    }
}
