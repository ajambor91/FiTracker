package aj.FiTracker.FiTracker.Controllers;

import aj.FiTracker.FiTracker.Abstract.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Tag("integrationWithCSRF")
@ActiveProfiles("integrationWithCSRF")
public class CoreControllerIntegrationTest extends AbstractIntegrationTest {
    private final CoreController coreContoller;
    private MockMvc mockMvc;

    @Autowired
    public CoreControllerIntegrationTest(MockMvc mockMvc, CoreController coreContoller) {
        this.coreContoller = coreContoller;
        this.mockMvc = mockMvc;
    }


    @Test
    @DisplayName("Should register user")
    public void testRegisterUser() throws Exception {
        this.mockMvc.perform(get("/core/csrf-token"))
                .andExpect(status().isOk());
    }
}
