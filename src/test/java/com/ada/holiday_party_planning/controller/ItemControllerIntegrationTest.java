package com.ada.holiday_party_planning.controller;

import com.ada.holiday_party_planning.dto.GuestDTO;
import com.ada.holiday_party_planning.dto.PartyOwnerDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void eventFlow() throws Exception{
        // criar PartyOwner
        MvcResult result = this.mockMvc.perform(post("/party-owners/register")
                        .content("{\n" +
                                "    \"name\" : \"Owner2\",\n" +
                                "    \"email\": \"owner5@teste.com\",\n" +
                                "    \"password\": \"senha\"\n" +
                                "}")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String json = result.getResponse().getContentAsString();
        PartyOwnerDTO ownerDTO = objectMapper.readValue(json, PartyOwnerDTO.class);

        //rever createDTO e ID
        MvcResult result1 = this.mockMvc.perform(post("/event/{ownerId}/create", ownerDTO.getOwnerId())
                        .content("{" +
                                "    \"theme\" : \"Natalino\"," +
                                "    \"title\": \"Ceia de Natal\"," +
                                "    \"LocalDateTime\": \"24-12-2024\"," +
                                "    \"place\": \"Rua do Limoeiro\"," +
                                "    \"description\": \"Ceia de Natal da Turma da Monica\"," +
                                "    \"funActivate\": \"true\"," +
                                "    \"categoryFun\": \"true\"" +
                                "}")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("owner5@teste.com").password("senha")))
                .andDo(print())
                .andReturn();
        String eventId = result1.getResponse().getContentAsString().replaceAll("\"","");


        // criar item
        this.mockMvc.perform(post("/item/{eventId}/create",eventId)
                        .content("{\n" +
                                "    \"name\": \"Chester\"," +
                                "    \"quantity\": 1," +
                                "    \"value\": 50.0" +
                                "}")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("owner5@teste.com").password("senha")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Chester")))
                .andExpect(content().string(containsString("1")))
                .andExpect(content().string(containsString("50.0")));

        //TODO testar demais endpoints



    }

}
