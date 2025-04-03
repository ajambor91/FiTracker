package aj.FiTracker.FiTracker.TestUtils;

import org.jetbrains.annotations.NotNull;
import org.springframework.vault.support.VaultTransitKey;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import org.springframework.vault.support.VaultTransitKey;
import java.time.Instant;
import java.util.Map;
public class VaultDataFactory {
    public static VaultTransitKey createTestVaultTransitKey() {


        return new VaultTransitKey() {
            @Override
            public @NotNull String getName() {
                return "jwt-rsa-key";
            }

            @Override
            public @NotNull String getType() {
                return "rsa-4096";
            }

            @Override
            public boolean allowPlaintextBackup() {
                return false;
            }

            @Override
            public int getConvergentVersion() {
                return 0;
            }

            @Override
            public boolean isDeletionAllowed() {
                return false;
            }

            @Override
            public boolean isDerived() {
                return false;
            }

            @Override
            public boolean isExportable() {
                return false;
            }

            @Override
            public Map<String, Object> getKeys() {
                try {
                    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                    keyPairGenerator.initialize(4096); // Możesz zmienić rozmiar klucza
                    KeyPair keyPair = keyPairGenerator.generateKeyPair();
                    PublicKey publicKey = keyPair.getPublic();
                    String publicKeyBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(publicKey.getEncoded());

                    return Map.of(
                            "1", Map.of(
                                    "name","rsa-4096",
                                    "creation_time", "2025-03-28T16:13:23.974476006Z",
                                    "public_key", "-----BEGIN PUBLIC KEY-----MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAwoXs1R9cR2YGDg4PU8BIv1E4RCmuqTyQG/zvvYI9sb/5yHrN4UQopgsrfLblp3vh//MECNVwF4NmRJo7bXdUklRI19dpirE/0yjHmiWgmNEeIMLmbYnVzDLN9nAvgN8EmRm53vvm6NuJpN4JHlVKA49NkwWkxsr//bFXucmITC20si0I9RGVT6jk1sZf/1i8yyyUCwfOUWM3/UCzcDZpxxt4qTPSkB4K+yHNtPLyFltycI891U8oykhEsXkEgjOAeyAm39+UmI5ihceG0gqb8N9I46OKDIGLAJ88tbKM+/lAWs7C7z+vBeSLuIS6eFZSKEq1sO01h+BzMcbMMW8TiUljGS1gxWvw7/LpoV5WfgFcGD6Dk02lBjT8WS9DHv3ObsrKsRsyA29pZtgkZPvCgyMJWIxdYKhfxTly89Ce7hAqBO82sLn9216oB0VDYgt1HPFIDvjDw/z7OSjhhzMt9A5rmgNwjR2s65DlU6OTr8xknf15VA2EuFfwK1xu1hrYoeffmFDzfNiM7TW+OoBGCoG0/cYrwAsjZ3g2GYB2IEvJ3j9Urq3omI7kgFlbxUa0JeXye+moWicLaexHwRFjWM0dSQUrcDGWa/JtRKry11WRmi1shqmrScYHnXrhHHd0EK3AXWcidaeVwYAamim0NYb3XKz+OkYSfYPxL5ciN88CAwEAAQ==-----END PUBLIC KEY-----"
                            )
                    );
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            }


            @Override
            public int getLatestVersion() {
                return 1;
            }

            @Override
            public int getMinDecryptionVersion() {
                return 1;
            }

            @Override
            public int getMinEncryptionVersion() {
                return 1;
            }

            @Override
            public boolean supportsConvergentEncryption() {
                return false;
            }

            @Override
            public boolean supportsDecryption() {
                return false;
            }

            @Override
            public boolean supportsDerivation() {
                return false;
            }

            @Override
            public boolean supportsEncryption() {
                return false;
            }

            @Override
            public boolean supportsSigning() {
                return true;
            }
        };

    }
}
