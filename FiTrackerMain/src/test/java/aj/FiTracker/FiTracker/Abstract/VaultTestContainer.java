package aj.FiTracker.FiTracker.Abstract;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class VaultTestContainer extends GenericContainer<VaultTestContainer> {
    private static final String VAULT_TOKEN = "root-token";
    private static final String IMAGE_V = "hashicorp/vault:1.13";
    private static final String TRANSIT_KEY_NAME = "jwt-rsa-key";
    private static final int VAULT_PORT = 8200;

    @Container
    private static VaultTestContainer vaultContainer = new VaultTestContainer()
            .withExposedPorts(8200)
            .withEnv("VAULT_DEV_ROOT_TOKEN_ID", VAULT_TOKEN)
            .withEnv("VAULT_DEV_LISTEN_ADDRESS", "0.0.0.0:" + VAULT_PORT)
            .withEnv("VAULT_ADDR", "http://0.0.0.0:" + VAULT_PORT)
            .waitingFor(Wait.forHttp("/v1/sys/health").forStatusCode(200))
            .withCommand("server", "-dev", "-dev-root-token-id=" + VAULT_TOKEN);

    private VaultTestContainer() {
        super(IMAGE_V);
    }

    public static VaultTestContainer getInstance() {

        return vaultContainer;
    }

    static void vaultProperties(DynamicPropertyRegistry registry) throws Exception {

        String vaultUrl = "http://" + vaultContainer.getHost() + ":" + vaultContainer.getMappedPort(8200);
        registry.add("spring.cloud.vault.uri", () -> vaultUrl);
        registry.add("spring.cloud.vault.token", () -> VAULT_TOKEN);
        registry.add("spring.cloud.vault.authentication", () -> "TOKEN");
        registry.add("spring.cloud.vault.transit.enabled", () -> "true");
        registry.add("spring.cloud.vault.transit.key-name", () -> TRANSIT_KEY_NAME);

        initializeTransitEngineWithRetry();
    }

    private static void initializeTransitEngineWithRetry() throws Exception {
        ExecResult enableResult = vaultContainer.execInContainer(
                "vault", "login", "-method=token", "-no-print", "token=" + VAULT_TOKEN
        );
        ExecResult listResult = vaultContainer.execInContainer(
                "vault", "secrets", "list", "-format=json"
        );

        if (listResult.getExitCode() == 0) {
            String jsonOutput = listResult.getStdout();
            JsonNode secrets = new ObjectMapper().readTree(jsonOutput);

            if (secrets.has("transit/")) {
                return;
            }
        }
        enableResult = vaultContainer.execInContainer(
                "vault", "secrets", "enable", "transit"
        );

        if (enableResult.getExitCode() != 0) {
            throw new IllegalStateException("Failed to enable transit: " + enableResult.getStderr());
        }

        ExecResult keyCheck = vaultContainer.execInContainer(
                "vault", "read", "transit/keys/" + TRANSIT_KEY_NAME
        );

        if (keyCheck.getExitCode() != 0) {
            ExecResult keyResult = vaultContainer.execInContainer(
                    "vault", "write", "-f",
                    "transit/keys/" + TRANSIT_KEY_NAME,
                    "type=rsa-4096"
            );
            if (keyResult.getExitCode() != 0) {
                throw new IllegalStateException("Failed to create key: " + keyResult.getStderr());
            }
        }
    }
}
