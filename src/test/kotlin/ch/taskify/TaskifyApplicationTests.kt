package ch.taskify

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Disabled("Requires a running local PostgreSQL instance with project environment variables")
class TaskifyApplicationTests {

    @Test
    fun contextLoads() {
    }

}
