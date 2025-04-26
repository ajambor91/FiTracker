package aj.FiTracker.FiTracker.Security;


import aj.FiTracker.FiTracker.Entities.User;
import aj.FiTracker.FiTracker.TestUtils.UserDataTestFactory;
import aj.FiTracker.FiTracker.UserInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@ActiveProfiles("unit")
public class PasswordEncoderUnitTest {
    private PasswordEncoder passwordEncoder;
    private User user;

    @BeforeEach
    public void setup() {
        passwordEncoder = new PasswordEncoder();
        user = UserDataTestFactory.createTestUser();
    }

    @Test
    @DisplayName("Should prepare User for register with new hashed password and salt")
    public void testPrepareForRegister() {
        UserInterface userWithEncryptedPassword = passwordEncoder.prepareForRegister(user);
        assertNotNull(userWithEncryptedPassword.getPassword());
        assertNull(userWithEncryptedPassword.getRawPassword());

    }

    @Test
    @DisplayName("Should compare passwords and returns true when password is correct")
    public void testShouldComparePasswords() {
        UserInterface userWithEncryptedPassword = passwordEncoder.prepareForRegister(user);
        User userToLogin = UserDataTestFactory.createTestUser();
        boolean isCorrectPassword = passwordEncoder.checkPass(userToLogin, userWithEncryptedPassword);
        assertNull(userWithEncryptedPassword.getRawPassword());
        assertTrue(isCorrectPassword);

    }

    @Test
    @DisplayName("Should compare passwords and returns false when password is not correct")
    public void testShouldComparePasswordsAndReturnFalseWhenPasswordIsIncorrect() {
        UserInterface userWithEncryptedPassword = passwordEncoder.prepareForRegister(user);
        User userToLogin = UserDataTestFactory.createTestUser();
        userToLogin.setRawPassword(new String("IncorrectPasswrd").toCharArray());
        boolean isCorrectPassword = passwordEncoder.checkPass(userToLogin, userWithEncryptedPassword);
        assertNull(userWithEncryptedPassword.getRawPassword());
        assertFalse(isCorrectPassword);

    }
}
