package aj.FiTracker.FiTracker.Security;

import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public final class RSAUtil {
    public static RSAPublicKey getRSAPubKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] decoedKey = Base64.getDecoder().decode(key.trim());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoedKey);
        RSAPublicKey rsaPublicKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
        return rsaPublicKey;
    }

    public static String generateKidFromPublicKey(RSAPublicKey publicKey) {
        String modulus = publicKey.getModulus().toString();
        String exponent = publicKey.getPublicExponent().toString();
        String fingerprint = modulus + ":" + exponent;
        return DigestUtils.sha256Hex(fingerprint);
    }
}
