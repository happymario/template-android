package com.victoria.bleled

import com.victoria.bleled.util.CommonUtil
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun checkEmailValid() {
        val  valid = CommonUtil.isValidEmail("");
        assertEquals(false, valid)
    }
}
