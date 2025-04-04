package aj.FiTracker.FiTracker.Security;

import aj.FiTracker.FiTracker.Entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Component
public class PasswordEncoder {
    private final Logger logger = LoggerFactory.getLogger(PasswordEncoder.class);
    @Value("${password.pepper}")
    private String passwordPepper;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final SecureRandom random = new SecureRandom();

    public User encryptPassword(User user) {
        logger.info("Starting password encryption for user: {}", user.getId());

        char[] saltedPass = setSalt(user);
        logger.debug("Salted password created for user: {}", user.getId());

        char[] pepperedPass = setPepper(saltedPass);
        logger.debug("Peppered password created for user: {}", user.getId());

        String hashedPassword = encoder.encode(new String(pepperedPass));
        logger.debug("Password hashed using BCrypt for user: {}", user.getId());

        Arrays.fill(pepperedPass, '0');
        Arrays.fill(saltedPass, '0');
        Arrays.fill(user.getRawPassword(), '0');
        user.setRawPassword(null);
        user.setPassword(hashedPassword);
        logger.info("Password encryption completed for user: {}", user.getId());

        return user;
    }

    public boolean checkPass(User userToAuth, User userToCheck) {
        logger.info("Starting password check for user: {}", userToAuth.getId());

        char[] saltedPass = getSalt(userToAuth, userToCheck);
        logger.debug("Salted password retrieved for user: {}", userToAuth.getId());

        char[] pepperedPass = setPepper(saltedPass);
        logger.debug("Peppered password created for user: {}", userToAuth.getId());

        boolean isPasswordCorrect = encoder.matches(new String(pepperedPass), userToCheck.getPassword());
        logger.info("Password check result for user {}: {}", userToAuth.getId(), isPasswordCorrect);

        userToAuth.setRawPassword(null);
        userToCheck.setPassword(null);
        return isPasswordCorrect;
    }

    private char[] setPepper(char[] rawPass) {
        logger.debug("Applying pepper to the password");

        String rawPassString = new String(rawPass);
        String pepperString = rawPassString + passwordPepper;
        char[] pepperedPass = pepperString.toCharArray();
        Arrays.fill(rawPass, '0');
        rawPassString = null;
        pepperString = null;
        logger.debug("Pepper applied to the password");

        return pepperedPass;
    }

    private char[] setSalt(User user) {
        logger.debug("Generating salt for user {}", user.getId());

        String salt = generateSalt();
        user.setSalt(salt);
        String rawPassString = new String(user.getRawPassword());
        String saltedPassString = user.getLogin() + rawPassString  + salt;
        char[] saltedPass = saltedPassString.toCharArray();
        Arrays.fill(user.getRawPassword(), '0');
        rawPassString = null;
        saltedPassString = null;
        logger.debug("Salt generated for user {}", user.getId());

        return saltedPass;
    }

    private char[] getSalt(User userToAuth, User userToCheck) {
        logger.debug("Retrieving salt for user: {}", userToAuth.getId());
        String rawPassString = new String(userToAuth.getRawPassword());
        String saltedPassString = userToAuth.getLogin() + rawPassString  + userToCheck.getSalt();
        char[] saltedPass = saltedPassString.toCharArray();
        Arrays.fill(userToAuth.getRawPassword(), '0');
        rawPassString = null;
        saltedPassString = null;
        logger.debug("Retrieved salt for user {}", userToAuth.getId());
        return saltedPass;
    }

    public String generateSalt() {
        logger.debug("Generating new salt");

        byte[] salt = new byte[16];
        random.nextBytes(salt);
        logger.debug("New salt generated");
        return Base64.getEncoder().encodeToString(salt);
    }
}
