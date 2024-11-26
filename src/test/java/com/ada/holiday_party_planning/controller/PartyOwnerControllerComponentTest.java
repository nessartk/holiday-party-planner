package com.ada.holiday_party_planning.controller;


import com.ada.holiday_party_planning.service.PartyOwnerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class PartyOwnerControllerComponentTest {

    @MockBean
    private PartyOwnerService partyOwnerService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void createPartyOwner() {
    }

    // mockMvc.perform();

}
