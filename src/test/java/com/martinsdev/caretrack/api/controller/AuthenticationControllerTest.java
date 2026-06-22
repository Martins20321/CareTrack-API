package com.martinsdev.caretrack.api.controller;

import com.jayway.jsonpath.JsonPath;
import com.martinsdev.caretrack.api.dto.LoginRequestDTO;
import com.martinsdev.caretrack.api.dto.RegisterRequestDTO;
import com.martinsdev.caretrack.api.model.User;
import com.martinsdev.caretrack.api.model.enums.RoleUser;
import com.martinsdev.caretrack.api.repository.SolicitationSearchRepository;
import com.martinsdev.caretrack.api.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private SolicitationSearchRepository solicitationSearchRepository;

    @MockitoBean
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private JacksonTester<RegisterRequestDTO> requestDTOJacksonTester;

    @Autowired
    private JacksonTester<LoginRequestDTO> loginRequestDTOJacksonTester;

    @BeforeEach
    void initialization() {
        userRepository.deleteAll();

        User user = User.builder()
                .name("Pedro Roberto")
                .email("pedro@email.com")
                .passwordHash(passwordEncoder.encode("pedro12345"))
                .role(RoleUser.CLIENT)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(user);
    }

    @Test
    @DisplayName("Should register a new client and return token")
    void register_shouldReturnToken_whenValidData() throws Exception {
        //ARRANGE
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO("Ana Souza", "ana@email.com", "ana1234");

        //ACT
        var response = mockMvc.perform(
                post("/auth/register")
                        .content(requestDTOJacksonTester.write(registerRequestDTO).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print()).andReturn().getResponse();

        //ASSERT
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("Should return 409 when email already exists")
    void register_shouldReturnConflict_whenEmailAlreadyExists() throws Exception {
        //ARRANGE
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO("Pedro Roberto", "pedro@email.com", "pedro1234");

        //ACT
        var response = mockMvc.perform(
                post("/auth/register")
                        .content(requestDTOJacksonTester.write(registerRequestDTO).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print()).andReturn().getResponse();

        //ASSERT
        Assertions.assertEquals(409, response.getStatus());

        var responseContent = response.getContentAsString(StandardCharsets.UTF_8);
        String exceptionActual = JsonPath.read(responseContent, "$.error");

        Assertions.assertEquals("Email Already Exists", exceptionActual);
    }

    @Test
    @DisplayName("Should return 400 when data is invalid")
    void register_shouldReturnBadRequest_whenInvalidData() throws Exception {
        //ARRANGE
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO("Carlos Ricardo", "carlosemail.com", "carlos1234");

        //ACT
        var response = mockMvc.perform(
                post("/auth/register")
                        .content(requestDTOJacksonTester.write(registerRequestDTO).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print()).andReturn().getResponse();

        //ASSERT
        Assertions.assertEquals(400, response.getStatus());
    }

    @Test
    @DisplayName("Should return 200 when data is valid")
    void login_shouldReturnToken_whenValidCredentials() throws Exception {
        //ARRANGE
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("pedro@email.com", "pedro12345");

        //ACT
        var response = mockMvc.perform(
                post("/auth/login")
                        .content(loginRequestDTOJacksonTester.write(loginRequestDTO).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print()).andReturn().getResponse();

        //ASSERT
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("Should return 403 when credentials are invalid")
    void login_shouldReturnForbidden_whenInvalidCredentials() throws Exception {
        //ARRANGE
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("pedro@email.com", "pedro123459090");

        //ACT
        var response = mockMvc.perform(
                post("/auth/login")
                        .content(loginRequestDTOJacksonTester.write(loginRequestDTO).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print()).andReturn().getResponse();

        //ASSERT
        Assertions.assertEquals(403, response.getStatus());
    }
}