package aj.FiTracker.FiTracker.AbstractTest;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@Testcontainers
public class OAuthServerTestContainer extends GenericContainer {
    private static OAuthServerTestContainer instance;
    private static final String IMAGE_NAME = "nginx:latest";
    private final int JWKS_PORT = 8080;
    private final String JWKS_DATA_PATH = "jwks.json";
    private final String TEST_CONTAINER_FILE_PATH = "/usr/share/nginx/html/jwks.json";

    private OAuthServerTestContainer() {
        super(DockerImageName.parse(IMAGE_NAME));
        this.withExposedPorts(JWKS_PORT)
                .withCopyFileToContainer(MountableFile.forClasspathResource(JWKS_DATA_PATH), TEST_CONTAINER_FILE_PATH)
                .withCommand("nginx", "-g", "daemon off; server { listen 8080; location /oauth2/jwks { alias /usr/share/nginx/html/jwks.json; types { } default_type application/json; } location / { root /usr/share/nginx/html; index index.html index.htm; } }");
    }

    public static OAuthServerTestContainer getInstance() {
        if (instance == null) {
            instance = new OAuthServerTestContainer();
        }
        return instance;
    }

    public static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("jwks.api.uri", () -> instance.getHost() + "/oauth2/jwks");
    }
}
