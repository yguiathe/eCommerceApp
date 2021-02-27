package com.example.demo;

import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class UserEndpointsIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JacksonTester<CreateUserRequest> json1;

    @Autowired
    private JacksonTester<ModifyCartRequest> json;

    @Test
    @Transactional
    public void shouldSuccessfullyLogin() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("jdoe");
        createUserRequest.setPassword("password");
        createUserRequest.setConfirmPassword("password");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/user/create")
                .content(json1.write(createUserRequest).getJson())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders
                .post("/login")
                .content("{ \"username\": \"" + createUserRequest.getUsername() + "\" , \"password\": \"" + createUserRequest.getPassword() + "\" }")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @Transactional
    public void shouldSuccessfullyLoginAndItemToCart() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("jiglesias");
        createUserRequest.setPassword("password1");
        createUserRequest.setConfirmPassword("password1");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/user/create")
                .content(json1.write(createUserRequest).getJson())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post("/login")
                .content("{ \"username\": \"" + createUserRequest.getUsername() + "\" , \"password\": \"" + createUserRequest.getPassword() + "\" }")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1l);
        modifyCartRequest.setUsername(createUserRequest.getUsername());
        modifyCartRequest.setQuantity(2);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/cart/addToCart")
                .content(json.write(modifyCartRequest).getJson())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + result.getResponse().getHeader("Authorization"))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

    }
}
