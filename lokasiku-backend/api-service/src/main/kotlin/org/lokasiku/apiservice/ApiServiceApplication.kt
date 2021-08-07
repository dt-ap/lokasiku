package org.lokasiku.apiservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.support.beans
import java.time.Clock

@SpringBootApplication
class ApiServiceApplication

fun main(args: Array<String>) {
    runApplication<ApiServiceApplication>(*args)

    beans {
        bean<Clock> { Clock.systemDefaultZone() }
    }
}