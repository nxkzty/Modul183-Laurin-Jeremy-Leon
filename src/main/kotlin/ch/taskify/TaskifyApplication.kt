package ch.taskify

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TaskifyApplication

fun main(args: Array<String>) {
    runApplication<TaskifyApplication>(*args)
}
