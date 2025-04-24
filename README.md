<h1 style="text-align: center; font-size: 3em;  margin-top: -30px; margin-bottom: 60px">
FiT
</h1> 
<h2 style="text-align: center; font-size: 1.8em;  margin-top: -50px; margin-bottom: 60px">
Finance Tracker. Your application for tracking finances.
</h2> 

FiT is a finance tracker application dedicated for individual users and groups like teams, families.
## Key Features:
* **Creating dashboards called Zones:** These are for groups of people or managing multiple different budgets.
* **Microservices architecture:** This ensures maximum performance and reliability, with each service dedicated to different functions.
* **Security and privacy:** We don't track your data!.


### The Problem FiT Solves:
Managing multiple personal budgets or tracking shared finances for various groups (like families, teams, or projects) using traditional methods can be complex and chaotic. FiT provides a centralized, flexible, and easy-to-use platform to bring clarity and control to these diverse financial tracking needs.



## How it Works:

To start, you need to create your Zone. You can create a Zone just for yourself or for your group. When creating a new Zone, you can find your friends by email and add them to your dashboard. They'll have the same access to the Zone as you, so make sure you're adding the right person!

Next, you can create categories for your expenses, e.g., entertainment, bills, and others. Or perhaps you want to create a Zone for your work team? You can add categories like accessories, printers, oil, and others.

After that, you can add your first expense, for example, "Electricity" with a value of 50 EUR. Next, you'll be redirected to the dashboard, which will show charts with summaries by category, day, top expenses, etc.
## Architecture & Technologies:

1. Spring Boot 3 (Main Service, Zones Service, Expenses Service)
2. PostgreSQL
3. MongoDB
4. Kafka
5. HashiCorp Vault
6. Nginx
7. Docker
8. Grafana / Loki / Promtail
9. Portainer
10. Angular (with NgRx)
    *###Microservices Breakdown:

* **Users Service:**  This service handles user registration and login and acts as the OAuth Server for authorization for other services. It is integrated with a PostgreSQL database.

* **Zones Service:** Also built on Spring Boot, this service manages Zones. It uses MongoDB, where Zones are stored as documents within a collection. MongoDB is well-suited for documents that can be easily extended in the future, and Zones are not directly related to other data like expenses.

* **Expenses Service:** The third and currently final service, this one is responsible for managing expenses and categories. It is backed by its own dedicated PostgreSQL database instance. This database contains four tables with relationships between categories and expenses. While categories can have multiple expenses, expenses can belong to multiple categories (indicating a many-to-many relationship).
### Database Migrations:

**Flyway** is used to manage database schema migrations for the **Main Service** and **Expenses Service**, ensuring consistent database versions across different environments.

* Migration scripts for the main application environment are located in the `src/main/resources/db/migration` directory within the respective service modules.
* For integration tests, separate, dedicated migration scripts are provided in the `src/test/resources/db/migration` directory within the relevant service modules.

* **Supporting Infrastructure:**

* **Apache Kafka:** This acts as a **message broker**, facilitating asynchronous communication and decoupling between different services. For instance, messages related to adding or removing users from a Zone are transmitted via Kafka.
* **HashiCorp Vault:** This tool is used for securely **storing sensitive configurations and secrets**, specifically including the RSA Key required for OAuth authentication between the microservices.
* **Nginx:** Serving as the **API Gateway** for the entire application, Nginx manages incoming requests, routing them to the appropriate microservices and providing a single entry point to the API. (It can also be configured for load balancing, SSL termination, etc.)
* **Docker:** The **entire application stack is containerized** using Docker. This ensures consistency across development, testing, and production environments and simplifies deployment and scaling.
* **Grafana / Loki / Promtail:** This integrated stack provides comprehensive **observability and logging**. Promtail collects logs from the Docker containers, Loki efficiently stores and indexes these logs, and Grafana offers powerful visualization and dashboards, which are invaluable for monitoring application health, performance, and debugging.
* **Portainer:** Similar to the monitoring provided by Grafana, Portainer offers an **administrative interface** for the Docker environment. It provides a user-friendly way to manage, inspect, and monitor the health and status of the running containers.
* **Angular:** This is the **frontend framework** used to build the application's user interface. It leverages the **NgRx library** for state management, effectively handling and storing critical data like user information and Zone details to provide a dynamic and responsive user experience.

## Demo
### you can check demo by enter on this site:** https://fit.adamantum.site  
Demo of the FiT is developer on my own **Proxmox** cluster. That's provides an independecne from outer vendors. 
Example users login: 
```text
Emily
John
Witkacy
Peter
Nancy
```
**For all the users pasword is:** exampleDem0U$ser


## How to Run

Follow these steps to set up and run the application using Docker:

1.  **Clone the repository:**
    ```bash
    git clone <repository_url> # Replace with your actual repository URL
    cd <repository_directory> # Navigate to the cloned directory
    ```
2.  **Build the Docker containers:**
    Navigate to the root directory of the repository (where the `docker-compose.yml` file is located) and run:
    ```bash
    docker compose build
    ```
3.  **Configure Application Properties:**
    You need to create `application.properties` files for the MainService, ZonesService, and ExpensesService. Example configuration files are provided in the `cfg` directory within each service's module (e.g., `FiTrackerMain/cfg/application.example.properties`).
    **Copy** the content of the `.example.properties` files and create new `application.properties` files in the same directories. **Crucially, update these files with your own secure configurations, especially database credentials.**
    **Warning:** The example configurations include default passwords and logins (like `exampleUser`, `examplePassword`). **Do not use these configurations or passwords in a production environment!**
4.  **Run Docker Compose:**
    Start the application services using Docker Compose in detached mode:
    ```bash
    docker compose up -d
    ```
5.  **Access the Vault Container:**
    To configure HashiCorp Vault, you need to execute commands inside its container. Find the name of the Vault service in your `docker-compose.yml` (it's often a part of the main service or a dedicated vault container if you have one). Assuming your main application service is named `fit-main-app`, you can access a shell in its container:
    ```bash
    docker exec -it fit-main-app bash # Or use the specific service name for Vault if separate
    ```
6.  **Initialize Vault:**
    Inside the Vault container's shell, initialize Vault. This will generate the necessary master keys and a root token. **Keep this output secure!**
    ```bash
    vault operator init
    ```
    This command will initialize Vault and generate operator keys required to unseal it, along with an initial root token.
7.  **Unseal Vault:**
    Vault requires a certain number of master keys (defined during initialization) to be provided to unseal it and become operational. Use the keys generated in the previous step. You will need to run the `unseal` command multiple times with different keys as instructed by the `init` output.
    ```bash
    vault operator unseal <KEY_1>
    vault operator unseal <KEY_2>
    vault operator unseal <KEY_3>
    # Repeat until Vault is unsealed (usually 3 out of 5 keys)
    ```
    Once the required threshold of keys is provided, Vault will be unsealed.
8.  **Log in to Vault:**
    Log in to Vault using the initial root token generated during the initialization step (Step 6):
    ```bash
    vault login token=<YOUR_ROOT_TOKEN> # Replace with the token from step 6
    ```
9.  **Enable the Transit Secrets Engine:**
    Enable the Transit secrets engine, which is used for cryptographic functions like key generation and signing (necessary for JWTs):
    ```bash
    vault secrets enable transit
    ```
10. **Enable the AppRole Authentication Method:**
    Enable the AppRole authentication method, which allows applications to authenticate securely with Vault:
    ```bash
    vault auth enable approle
    ```
11. **Create RSA Key for JWTs:**
    Create the RSA key within the Transit engine. This key will be used for signing and verifying JWTs:
    ```bash
    vault write transit/keys/jwt-rsa-key type="rsa-4096" exportable=true allow_plaintext_backup=true
    ```
12. **Upload Vault Policy:**
    You need to define a policy that grants the application service the necessary permissions within Vault. An example policy file (`fit-main-policy.hcl`) should be located in a directory like `FitVault/policies` in your project structure. Upload this policy to Vault:
    ```bash
    vault policy write fit-main-app-policy /vault/policies/fit-main-policy.hcl
    ```
    *(Note: The exact path `/vault/policies/fit-main-policy.hcl` might need adjustment based on how your policy file is accessible from within the Vault container)*.
13. **Configure AppRole Role:**
    Configure the AppRole role that your application service will use to authenticate. Set the policies and token TTLs according to your security requirements.
    ```bash
    vault write auth/approle/role/fit-main-app-policy \
      token_policies="fit-main-app-policy" \
      token_ttl=24h \
      token_max_ttl=48h \
      secret_id_ttl=768h # Secret ID valid for 768 hours
    ```
    It is **strongly recommended to customize these values for production environments.**
14. **Retrieve AppRole Role ID:**
    Get the generated `role_id` for the configured AppRole role. You will need this for the MainService's configuration.
    ```bash
    vault read auth/approle/role/fit-main-app-policy/role-id
    ```
    Copy the displayed `role_id` and add it to the MainService's `application.properties` file (`FiTrackerMain/cfg/application.properties`) under the key:
    ```text
    spring.cloud.vault.app-role.role-id
    ```
15. **Generate AppRole Secret ID:**
    Generate a `secret_id` for the AppRole role. This is the secret credential the application service will use along with the `role_id` to authenticate with Vault.
    ```bash
    vault write -f auth/approle/role/fit-main-app-policy/secret-id
    ```
    From the output of this command, copy the generated `secret_id`. Paste this value into the MainService's `application.properties` file (`FiTrackerMain/cfg/application.properties`) under the key:
    ```text
    spring.cloud.vault.app-role.secret-id
    ```
16. **Recreate FitMainApp by using:**
``` bash
    docker-compose up -d fit-main-app
```
17. **Build the Frontend API Client:**
    Navigate to the frontend application directory:
    ```bash
    cd Web/web-app
    ```
    Then, build the API client library (this step is likely necessary to generate code based on your backend API):
    ```bash
    ng b api
    ```
18. **Run the Frontend Development Server:**
    From the `Web/web-app` directory, run the Angular development server. This will typically serve the application on a local port (e.g., `localhost:4200` by default, but your setup might use `fit-web` as a project name):
    ```bash
    ng serve fit-web
    ```
19. **Configure Web Server (Nginx) and Local Domain:**
    You need to configure a web server, such as Nginx, to serve the frontend application and map a local domain name to it. The default domain expected by the application is `fit.local`.

    * **Update Hosts File:** Ensure you have added the chosen local domain (e.g., `fit.local`) to your operating system's hosts file, mapping it to `127.0.0.1`. This allows your browser to resolve the domain name locally.
    * **Configure Nginx:** An example Nginx configuration file is provided in the `nginx-host` directory. Copy and adapt this configuration to your Nginx setup to serve the built frontend files and proxy API requests to your backend services.
    * **Change Domain (Optional):** If you want to use a different local domain, you can change `fit.local` in the Angular environment file located at `Web/web-app/fit-web/src/environments/environment.development.ts`.

    After completing these steps, you should be able to access the application in your web browser by navigating to the configured domain, for example: `https://fit.local`.

    **HTTPS Requirement:** Using HTTPS with a valid SSL certificate is necessary for the application to function correctly, particularly for secure communication with the backend services.

    **Optional: Running without SSL (Not Recommended for Production!):**
    If you encounter issues with SSL certificates locally or prefer not to set up a self-signed certificate for development, you can **temporarily disable CSRF protection** in the backend's Security Configuration. **Be aware that this significantly reduces security and should NEVER be done in a production environment.**

    To disable CSRF, open the file `FiTrackerMain/src/main/java/aj/FiTracker/FiTracker/Security/SecurityConfig.java`. Locate the CSRF configuration section and comment out or remove the lines configuring the `CsrfCookieTokenRepository` and `CsrfTokenRequestAttributeHandler`. Then, add `csrf.disable()` within the `csrf()` lambda.

    Your `csrf` configuration block should look like this after modification:

    ```java
                .csrf(csrf -> {
    //                 logger.info("Configuring CSRF protection");
    //                 CsrfCookieTokenRepository csrfTokenRepository = new CsrfCookieTokenRepository();
    //                 csrf.csrfTokenRepository(csrfTokenRepository);
    //                 logger.debug("Using CookieCsrfTokenRepository for CSRF token storage");
    //                 CsrfTokenRequestAttributeHandler csrfTokenRequestHandler = new CsrfTokenRequestAttributeHandler();
    //                 csrf.csrfTokenRequestHandler(csrfTokenRequestHandler);
    //                 logger.debug("Using CsrfTokenRequestAttributeHandler for CSRF token handling");
                        csrf.disable(); // WARNING: Disables CSRF protection - USE ONLY FOR LOCAL DEVELOPMENT WITHOUT SSL
                });
    ```
    Remember to revert these changes if you decide to implement SSL or before deploying to any environment accessible outside your local machine.


## Production
For deploying the application to a production environment, there is a dedicated docker-compose.prod.yml file.
* **Custom Dockerfiles:** For production deployment, you may need to create your own `Dockerfile.prod` for each service/container, tailored to your specific production needs (e.g., building optimized JARs, using non-root users).
* **Production Properties:** You must create `application.prod.properties` files for each Spring microservice containing production-specific configurations (database URLs, credentials, external service endpoints, logging levels, etc.).
* **Database Initialization:** Additionally, you might need to create production initialization scripts, such as `init.prod.js` for MongoDB or `init.prod.sql` for SQL databases, to set up the production database schema or initial data securely.
* **Grafana Configuration:** Grafana also requires specific configuration adjustments for production use, including securing access and configuring persistent storage for its data.
* 
You can base these production configuration files (`.prod.properties`, `.prod.js`, .`prod.sql`) on the provided examples, but IMPORTANT: Never use the example credentials or default passwords in a production environment! Use secure, unique passwords and secrets managed via Vault or other secure means.  
Remember to adjust the `docker-compose.prod.yml` file for your specific infrastructure requirements, including setting resource limits (e.g., CPU, RAM), configuring persistent storage for databases, logs, and Grafana data, setting up proper networking, and managing user/container privileges for security.

## Administration
Several tools are available for monitoring and administering the application components, especially in a local development setup:
1. **Spring Boot Actuator:** All Spring Microservices utilize Spring Boot Actuator, which provides various operational endpoints (metrics, health checks, configuration properties, environment details) accessible from your browser. Default access ports on your local machine are:
```text
 http://localhost:9000/actuator/* # FiTracker Main
 http://localhost:9001/actuator/* # FiTracker Zone
 http://localhost:9002/actuator/* # FiTracker Expenses
```
The asterisk (*) represents different Actuator endpoints like /health, /info, /metrics, /configprops, /env, etc. Accessing these endpoints might require authentication depending on your security configuration. Default authentication is disabled.
2. **Grafana:** Grafana is accessible on the default port 3000:
```text
http://localhost:3000
```
You can log in with the default credentials (which should be changed immediately in any non-local environment):
```text
login: admin
password: examplePassword
```
From Grafana, you can view application logs (collected by Promtail and stored in Loki) by selecting the Loki data source and exploring logs for different services using labels. The main log sources you can explore using the job label are:
``` 
    main # Main Service
    zone # Zone Service
    expenses # Expenses Service
    fit-api # For nginx as gateway
    fit-broker #Kafka logs
```
3. **Portainer:** Portainer provides a web-based interface for managing your Docker environment. In the provided setup, it is typically configured in the docker-compose.prod.yml file (and not included in the default development setup, but you can add it if needed). The default Portainer port in the production setup is configured to 8000 (e.g., accessible via http://your-server-ip:8000).


## Test coverage


| Service             | Test Coverage |
|---------------------|---------------|
| **MainService**     | 76%           | 
| **ZoneService**     | 67%           | 
| **ExpensesService** | 78%           | 

- [MainService Coverage Report](https://ajambor91.github.io/FiTracker/reports/MainService/jacoco/)
- [ZoneService Coverage Report](https://ajambor91.github.io/FiTracker/reports/ZoneService/jacoco/)
- [ExpensesService Coverage Report](https://ajambor91.github.io/FiTracker/reports/ExpensesService/jacoco)

Reports are available in `reports/SERVICE_NAME/jacoco`
All microservices are comprehensively covered by both unit and integration tests. Tests are clearly categorized for flexible execution using Spring Profiles and JUnit Tags, with the "unit" profile/tag designated for unit tests and "integration" for integration tests. A consistent naming convention is followed for test files, such as SomeServiceUnitTest.java and SomeServiceIntegrationTest.java, aiding in easy identification of test types. Integration tests are robustly implemented using Testcontainers, providing isolated, lightweight instances of external dependencies including PostgreSQL, MongoDB, Apache Kafka, and HashiCorp Vault. For services utilizing PostgreSQL, database schema structures required for integration tests are automatically managed by Flyway, which applies migration scripts located in the src/test/resources/db/migration directory within the respective service modules.
To run test you should go into ServiceDirectory, like FiTrackeMain etc. and run
```bash
    ./gradlew unitTest # For unit tests
    ./gradlew integrationTest #For integration test
    ./gradlew test # For both 
    ./gradlew jacocoTestReport # For generate Jacoco Reports. Reports will be store in Service/build/reports/jacoco/index.html
```

## Future Enhancements
Here are the planned improvements and features for FiT:
1. **User account managing**
2. **Improve expense dashboards, such as adding a summary per user.**
3. **Implement granular privileges for Zone administrators and members.**
4. **Introduce a default currency option for each Zone.**
5. **Allow personalization of Zone themes, including background colors.**
6. **Enable personalization of the dashboard page with user-selected charts.**


## License

This project is licensed under the [MIT License](LICENSE).  
Feel free to use, modify, and distribute it as long as the terms of the license are followed.