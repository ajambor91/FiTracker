package aj.FiTracker.FiTracker.Security;

import aj.FiTracker.FiTracker.Entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Component
public class PasswordEncoder {
    @Value("${password.pepper}")
    private String passwordPepper;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final SecureRandom random = new SecureRandom();

    public User encryptPassword(User user) {
        char[] saltedPass = setSalt(user);
        char[] pepperedPass = setPepper(saltedPass);
        String hashedPassword = encoder.encode(new String(pepperedPass));
        Arrays.fill(pepperedPass, '0');
        Arrays.fill(saltedPass, '0');
        Arrays.fill(user.getRawPassword(), '0');
        user.setRawPassword(null);
        user.setPassword(hashedPassword);
        return user;
    }

    public boolean checkPass(User userToAuth, User userToCheck) {
        char[] saltedPass = getSalt(userToAuth, userToCheck);
        char[] pepperedPass = setPepper(saltedPass);
        boolean isPasswordCorrect = encoder.matches(new String(pepperedPass), userToCheck.getPassword());
        userToAuth.setRawPassword(null);
        userToCheck.setPassword(null);
        return isPasswordCorrect;
    }

    private char[] setPepper(char[] rawPass) {
        String rawPassString = new String(rawPass);
        String pepperString = rawPassString + passwordPepper;
        char[] pepperedPass = pepperString.toCharArray();
        Arrays.fill(rawPass, '0');
        rawPassString = null;
        pepperString = null;
        return pepperedPass;
    }

    private char[] setSalt(User user) {
        String salt = generateSalt();
        user.setSalt(salt);
        String rawPassString = new String(user.getRawPassword());
        String saltedPassString = user.getLogin() + rawPassString  + salt;
        char[] saltedPass = saltedPassString.toCharArray();
        Arrays.fill(user.getRawPassword(), '0');
        rawPassString = null;
        saltedPassString = null;
        return saltedPass;
    }

    private char[] getSalt(User userToAuth, User userToCheck) {
        String rawPassString = new String(userToAuth.getRawPassword());
        String saltedPassString = userToAuth.getLogin() + rawPassString  + userToCheck.getSalt();
        char[] saltedPass = saltedPassString.toCharArray();
        Arrays.fill(userToAuth.getRawPassword(), '0');
        rawPassString = null;
        saltedPassString = null;
        return saltedPass;
    }

    public String generateSalt() {
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
}
