package com.ada.holiday_party_planning.controller;


import com.ada.holiday_party_planning.dto.PartyOwnerLoginResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class PartyOwnerControllerIntegrationTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void partyOwnerFlow() throws Exception {
        this.mockMvc.perform(post("/party-owners/register")
                        .content("{\n" +
                                "    \"name\" : \"Owner1\",\n" +
                                "    \"email\": \"owner1@teste.com\",\n" +
                                "    \"password\": \"password\"\n" +
                                "}")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Owner1")))
                .andExpect(content().string(containsString("owner1@teste.com")))
                .andExpect(content().string(containsString("ownerId")));

        this.mockMvc.perform(post("/party-owners/register")
                        .content("{\n" +
                                "    \"name\" : \"Owner2\",\n" +
                                "    \"email\": \"owner2@teste.com\",\n" +
                                "    \"password\": \"senha\"\n" +
                                "}")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Owner2")))
                .andExpect(content().string(containsString("owner2@teste.com")))
                .andExpect(content().string(containsString("ownerId")));


        MvcResult result = this.mockMvc.perform(
                        post("/party-owners/login")
                                .content("{\n" +
                                        "    \"email\": \"owner1@teste.com\",\n" +
                                        "    \"password\": \"password\"\n" +
                                        "}")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(user("owner1@teste.com").password("password")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Owner1")))
                .andExpect(content().string(containsString("owner1@teste.com")))
                .andExpect(content().string(containsString("ownerId")))
                .andReturn();
        String json = result.getResponse().getContentAsString();
        PartyOwnerLoginResponseDTO loginDTO = objectMapper.readValue(json, PartyOwnerLoginResponseDTO.class);


        this.mockMvc.perform(
                        post("/party-owners/login")
                                .content("{\n" +
                                        "    \"email\": \"owner2@teste.com\",\n" +
                                        "    \"password\": \"senha\"\n" +
                                        "}")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(user("owner2@teste.com").password("senha")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Owner2")))
                .andExpect(content().string(containsString("owner2@teste.com")));

        this.mockMvc.perform(get("/party-owners/all")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("owner@teste.com").password("password")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Owner1")))
                .andExpect(content().string(containsString("owner1@teste.com")))
                .andExpect(content().string(containsString("Owner2")))
                .andExpect(content().string(containsString("owner2@teste.com")));

        this.mockMvc.perform(put("/party-owners/update/{ownerId}",loginDTO.getOwnerId().toString())
                .content("{\n" +
                        "    \"name\" : \"Owner3\",\n" +
                        "    \"email\": \"owner3@teste.com\"" +
                        "}")
                .param("ownerId",loginDTO.getOwnerId().toString())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("owner1@teste.com").password("password")))
                .andDo(print());

        this.mockMvc.perform(
                        post("/party-owners/login")
                                .content("{\n" +
                                        "    \"email\": \"owner1@teste.com\",\n" +
                                        "    \"password\": \"password\"\n" +
                                        "}")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(user("owner1@teste.com").password("password")))
                .andDo(print())
                .andExpect(status().isNotFound());

        this.mockMvc.perform(
                        post("/party-owners/login")
                                .content("{\n" +
                                        "    \"email\": \"owner3@teste.com\",\n" +
                                        "    \"password\": \"password\"\n" +
                                        "}")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(user("owner3@teste.com").password("password")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Owner3")))
                .andExpect(content().string(containsString("owner3@teste.com")))
                .andExpect(content().string(containsString("ownerId")))
                .andReturn();







    }


}
