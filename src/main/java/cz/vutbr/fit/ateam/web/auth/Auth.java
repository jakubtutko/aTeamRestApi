package cz.vutbr.fit.ateam.web.auth;

import cz.vutbr.fit.ateam.persistence.models.User;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Class offering basic authorization utils.
 */
public class Auth {

    /**
     * Creates 255 chars long random string, combined from A..Z, a..z, 0..9 chars.
     *
     * @return 255 chars long token
     */
    private static String createAuthToken() {
        String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnoprstuvwxyz0123456789";
        String token = "";
        Random random = new SecureRandom();

        for (int i = 0; i < 255; i++) {
            token += charSet.charAt(random.nextInt(charSet.length()));
        }

        return token;
    }

    /**
     * Creates 256 long hashed string from given text.
     *
     * @param text text to hash
     * @return hashed string or null if error occurred
     */
    public static String createHash(String text) {
        if (text == null) return null;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();

            for (byte aHash : hash) {
                String hex = Integer.toHexString(0xff & aHash);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Compares plain text with hashed text if they are equal.
     *
     * @param text plain text
     * @param hash hashed text
     * @return true if plain text is the same as hashed text
     */
    public static boolean compareHash(String text, String hash) {
        if (text == null || hash == null) return false;

        String hashedText = createHash(text);

        return hashedText.equals(hash);
    }

    public static String generateAuthToken(Session session) {
        Criteria cr;
        String token;
        do {
            token = Auth.createAuthToken();
            cr = session.createCriteria(User.class).add(Restrictions.eq("authToken", token));
        } while (cr.uniqueResult() != null);
        return token;
    }

    public static User getUserByAuthToken(Session session, String token) {
        return (User) session.createCriteria(User.class).add(Restrictions.eq("authToken", token)).uniqueResult();
    }
}
