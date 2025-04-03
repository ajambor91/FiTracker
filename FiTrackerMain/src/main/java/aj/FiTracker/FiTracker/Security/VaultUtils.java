package aj.FiTracker.FiTracker.Security;

import org.springframework.vault.support.VaultTransitKey;

import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public final class VaultUtils {

    private final static String PUBLIC_MAP_KEY = "public_key";
    private final static String KEY_FOOTER = "-----END PUBLIC KEY-----";
    private final static String KEY_HEADER = "-----BEGIN PUBLIC KEY-----";


    public static String extractKey(VaultTransitKey key) {
        Map<String, String> keyMap = getRSAKey(key);
        String pubKey = keyMap.get(PUBLIC_MAP_KEY);
        if (pubKey.isEmpty()) {
            throw new NoSuchElementException("Cannot find " + PUBLIC_MAP_KEY);
        }

        return removeHeaders(pubKey);

    }

    private static String removeHeaders(String pubKey) {
       String key = pubKey
               .replace(KEY_HEADER, "")
               .replace(KEY_FOOTER,"")
               .replaceAll("\\s", "")
               .trim();
       return key;
    }

    private static Map<String, String> getRSAKey(VaultTransitKey key) {
        int latestVersion =  key.getLatestVersion();
        Map<String, Object> keys =  key.getKeys();
        Map<String, String> extractedKey =extractValue(keys.get(String.valueOf(latestVersion)));
        return extractedKey;

    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> extractValue(Object packedKey) {
        Map<String, String> newMap = new LinkedHashMap<>();
        if (packedKey instanceof Map<?, ?> map) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
                    newMap.put((String) entry.getKey(), (String) entry.getValue());
                }
            }
        }
        return newMap;
    }

    public static String convertVaultSignatureToJWT(String vaultSignature) {
        String base64Signature = vaultSignature.replace("vault:v1:", "").trim();
        byte[] signatureBytes = Base64.getDecoder().decode(base64Signature);
        String jwtSignature = Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes).trim();
        return jwtSignature;
    }
}
