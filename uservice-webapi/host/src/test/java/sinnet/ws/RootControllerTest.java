package sinnet.ws;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import sinnet.app.Program;

/**
 * Unit tests for RootController.
 */
@SpringBootTest(classes = Program.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RootControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rootEndpoint_shouldReturnNoContent() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isNoContent());
    }

    @Test
    void rootEndpoint_shouldReturnHttp204() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is(HttpStatus.NO_CONTENT.value()));
    }
}