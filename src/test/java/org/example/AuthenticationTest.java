package org.example;

import org.example.authentication.Authentication;
import org.example.authentication.IUserRepository;
import org.example.authentication.User;
import org.example.authentication.UserRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationTest {

    @Test
    void shouldAuthenticateUserWithCorrectLoginAndPassword() {
        IUserRepository userRepository = new UserRepository();
        Authentication authentication = new Authentication(userRepository);

        User user = authentication.authenticate("admin", "admin123");

        assertNotNull(user);
        assertEquals("admin", user.getLogin());
    }

    @Test
    void shouldNotAuthenticateUserWithWrongPassword() {
        IUserRepository userRepository = new UserRepository();
        Authentication authentication = new Authentication(userRepository);

        User user = authentication.authenticate("admin", "zlehaslo");

        assertNull(user);
    }

    @Test
    void shouldNotAuthenticateNonExistingUser() {
        IUserRepository userRepository = new UserRepository();
        Authentication authentication = new Authentication(userRepository);

        User user = authentication.authenticate("brak", "admin123");

        assertNull(user);
    }

    @Test
    void hashPasswordShouldReturnSameHashForSameInput() {
        String hash1 = Authentication.hashPassword("admin123");
        String hash2 = Authentication.hashPassword("admin123");

        assertEquals(hash1, hash2);
    }
}