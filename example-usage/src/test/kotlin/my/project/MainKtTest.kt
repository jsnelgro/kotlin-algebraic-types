package my.project

import kotlin.test.Test
import kotlin.test.assertIs

class MainKtTest {
    @Test
    fun `test generated from methods`() {
        assertIs<String>("hello")
    }
}