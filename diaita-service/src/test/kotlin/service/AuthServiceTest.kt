package com.diaita.service

import com.diaita.auth.AuthResult
import com.diaita.auth.AuthService
import com.diaita.auth.PasswordHasher
import com.diaita.auth.TokenService
import com.diaita.dto.LoginRequestDto
import com.diaita.dto.RegisterRequestDto
import com.diaita.repo.AuthRepo
import com.diaita.testDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class AuthServiceTest {
    @Test
    fun registration_login_and_logout_manage_a_revocable_session() {
        val service = AuthService(
            AuthRepo(testDatabase()),
            PasswordHasher(),
            TokenService("test-secret", "test", "test", 3600)
        )
        val registered = assertIs<AuthResult.Success>(
            service.register(RegisterRequestDto("person@example.com", "password1", "Test Person"))
        ).session
        assertTrue(registered.accessToken.isNotBlank())

        val loggedIn = assertIs<AuthResult.Success>(
            service.login(LoginRequestDto("PERSON@example.com", "password1"))
        ).session
        assertEquals(registered.user.id, loggedIn.user.id)
    }

    @Test
    fun login_rejects_an_invalid_password() {
        val service = AuthService(
            AuthRepo(testDatabase()),
            PasswordHasher(),
            TokenService("test-secret", "test", "test", 3600)
        )
        service.register(RegisterRequestDto("person@example.com", "password1", "Test Person"))
        assertIs<AuthResult.Failure>(service.login(LoginRequestDto("person@example.com", "wrongpass1")))
    }
}
