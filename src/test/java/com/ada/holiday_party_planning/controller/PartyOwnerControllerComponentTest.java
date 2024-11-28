package com.ada.holiday_party_planning.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class PartyOwnerControllerComponentTest {


    @Autowired
    private PartyOwnerController controller;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    void shouldReturnDefaultMessage() throws Exception {
        this.mockMvc.perform(post("/party-owners/register")
                        .content("{\n" +
                        "    \"name\" : \"Cascao\",\n" +
                        "    \"email\": \"cascao@teste.com\",\n" +
                        "    \"password\": \"123456\"\n" +
                        "}")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Cascao")))
                .andExpect(content().string(containsString("cascao@teste.com")))
                .andExpect(content().string(containsString("ownerId")))
                .andExpect(content().string(containsString("casc"))); // notcontent password
    }



}
