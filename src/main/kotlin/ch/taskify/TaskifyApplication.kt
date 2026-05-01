package ch.taskify

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class TaskifyApplication

fun main(args: Array<String>) {
    runApplication<TaskifyApplication>(*args)
}
