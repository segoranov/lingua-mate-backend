package mate.lingua;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest(properties = {
        "spring.liquibase.change-log=classpath:config/liquibase/master.xml"
})
@AutoConfigureMockMvc
public class LinguaMateIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:15.4");

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public static void init() {
        String jdbcUrl = postgresqlContainer.getJdbcUrl();
        try (Connection connection = DriverManager.getConnection(jdbcUrl, postgresqlContainer.getUsername(),
                postgresqlContainer.getPassword());
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE DATABASE linguamatedb");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create the database", e);
        }
    }

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> postgresqlContainer.getJdbcUrl() + "linguamatedb");
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
    }

    @Test
    public void testCreateLearningDataset() throws Exception {
        mockMvc.perform(post("/api/v1/learning-datasets")
                        .contentType("application/json")
                        .content("{\"name\": \"Dataset 1\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern("http://.+/api/v1/learning-datasets/\\d+")))
                .andExpect(jsonPath("$.id").value(4)) // because of test data generated in liquibase changelog 1.1
                .andExpect(jsonPath("$.name").value("Dataset 1"));
    }

//    @Test
//    public void testEditLearningDataset() throws Exception {
//        mockMvc.perform(patch("/api/v1/learning-datasets/1")
//                        .contentType("application/json")
//                        .content("{\"name\": \"Updated Dataset\"}"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void testCreateTranslationUnit() throws Exception {
//        mockMvc.perform(post("/api/v1/learning-datasets/1/translation-units")
//                        .contentType("application/json")
//                        .content("{\"text\": \"Hello\", \"translation\": \"Hola\"}"))
//                .andExpect(status().isCreated());
//    }
//
//    @Test
//    public void testEditTranslationUnit() throws Exception {
//        mockMvc.perform(patch("/api/v1/learning-datasets/1/translation-units/1")
//                        .contentType("application/json")
//                        .content("{\"text\": \"Updated Text\", \"translation\": \"Updated Translation\"}"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void testDeleteTranslationUnit() throws Exception {
//        mockMvc.perform(delete("/api/v1/learning-datasets/1/translation-units/1"))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    public void testDeleteLearningDataset() throws Exception {
//        mockMvc.perform(delete("/api/v1/learning-datasets/1"))
//                .andExpect(status().isNoContent());
//    }
}
