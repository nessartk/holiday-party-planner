package com.ada.holiday_party_planning.service;

import com.ada.holiday_party_planning.repository.PartyOwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

public class CustomUserDetailsServiceTest {

        private PartyOwnerRepository partyOwnerRepository;

        @BeforeEach
        void setUp() {
            partyOwnerRepository = mock(PartyOwnerRepository.class);
        }

        @Test
        void loadUserByUsername(){}

}
