package com.ada.holiday_party_planning.service;


import com.ada.holiday_party_planning.dto.CreatePartyOwnerDTO;
import com.ada.holiday_party_planning.dto.PartyOwnerDTO;
import com.ada.holiday_party_planning.exceptions.EmailAlreadyExistsException;
import com.ada.holiday_party_planning.mappers.PartyOwnerMapper;
import com.ada.holiday_party_planning.model.PartyOwner;
import com.ada.holiday_party_planning.repository.PartyOwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.method.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


public class PartyOwnerServiceTest {

    @Mock
    private PartyOwnerRepository partyOwnerRepository;


    @InjectMocks
    private PartyOwnerService partyOwnerService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void dadoValidoCreatePartyOwnerDTO_quandoCreatePartyOwner_entaoRetornaPartyOwnerDTO() {
        //dado
        CreatePartyOwnerDTO createPartyOwnerDTO = new CreatePartyOwnerDTO("Monica", "monica@teste","password");

        PartyOwner partyOwner = new PartyOwner();
        partyOwner.setName("Monica");
        partyOwner.setEmail("monica@teste");
        partyOwner.setPassword("encodedPassword");

        PartyOwnerDTO expectedDTO = new PartyOwnerDTO(UUID.randomUUID(), "Monica","monica@teste");

        when(partyOwnerRepository.findByEmail("monica@teste")).thenReturn(Optional.empty());
        when(partyOwnerRepository.save(partyOwner)).thenReturn(partyOwner);

        //quando
        PartyOwnerDTO result = partyOwnerService.createPartyOwner(createPartyOwnerDTO);

        //entao
        assertNotNull(result);
        assertEquals("monica@teste", result.getEmail());
        verify(partyOwnerRepository).save(partyOwner);

    }
    @Test
    void dadoInvalidoCreatePartyOwnerDTO_QuandoCreatePartyOwnerDTO_entaoLancaEmailAlreadyExist(){
        //dado
        CreatePartyOwnerDTO createPartyOwnerDTO = new CreatePartyOwnerDTO("Monica", "jaExisteEsteEmail@teste", "123456");

        PartyOwner existingPartyOwner = new PartyOwner();
        existingPartyOwner.setEmail("jaExisteEsteEmail@teste");

        when(partyOwnerRepository.findByEmail("jaExisteEsteEmail@teste"))
                .thenReturn(Optional.of(existingPartyOwner));

        //quando e entao
        assertThrows(EmailAlreadyExistsException.class, () -> partyOwnerService.createPartyOwner(createPartyOwnerDTO));
        verify(partyOwnerRepository, never()).save(any(PartyOwner.class));
    }


    @Test
    void login() {
    }

    @Test
    void getAllPartyOwners() {
    }

    @Test
    void updatePartyOwner() {
    }

    @Test
    void loadUserByUsername() {
    }

}
