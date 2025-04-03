package aj.FiTracker.FiTracker.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.core.VaultTransitOperations;

@Configuration
public class VaultConfig {

    @Bean
    public VaultTransitOperations vaultTransitOperations(VaultTemplate vaultTemplate) {
        return vaultTemplate.opsForTransit();
    }
}
