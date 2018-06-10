package com.dahham.tvshowmobile

import org.junit.Assert.assertEquals
import org.junit.Test

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
    lateinit var int : String

    @Test
    fun testRogue(){
        val a = "Helloo"
        val b = "Hello"
        val c = a

        System.out.println("a === b:${a === b}")
        System.out.println("a == b: ${a == b}")
        System.out.println("a === c: ${a === c}")
        System.out.println("a == c: ${a == c}")
    }



}
