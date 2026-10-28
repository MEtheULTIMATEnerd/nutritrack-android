package com.fit2081.fit2081assignment1

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordHasherTest {
    @Test
    fun hashRoundTripAcceptsOnlyOriginalPassword() {
        val encoded = PasswordHasher.hash("DemoPass!23")

        assertTrue(PasswordHasher.verify("DemoPass!23", encoded))
        assertFalse(PasswordHasher.verify("wrong-password", encoded))
    }
}
