package com.ada.holiday_party_planning.service;

import com.ada.holiday_party_planning.model.PartyOwner;
import com.ada.holiday_party_planning.repository.PartyOwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private PartyOwnerRepository partyOwnerRepository;

    @InjectMocks
    private PartyOwnerService partyOwnerService;

    private PartyOwner mockPartyOwner;

    @BeforeEach
    void setUp() {
        // Dado que existe um PartyOwner válido
        mockPartyOwner = new PartyOwner();
        mockPartyOwner.setName("Monica");
        mockPartyOwner.setEmail("monica@teste");
        mockPartyOwner.setPassword("123456");

    }

    @Test
    void dadoEmailValido_quandoCarregarUsuario_entaoRetornaUserDetails() {
        //dado
        when(partyOwnerRepository.findByEmail("monica@teste")).thenReturn(Optional.of(mockPartyOwner));

        //quando
        UserDetails userDetails = partyOwnerService.loadUserByUsername("monica@teste");

        //entao
        assertNotNull(userDetails);
        assertEquals("monica@teste", userDetails.getUsername());
        assertEquals("123456", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());
        verify(partyOwnerRepository, times(1)).findByEmail("monica@teste");
    }

    @Test
    void dadoEmailInexistente_quandoCarregarUsuario_entaoLancaUserNameNotFoundException() {
        //dado
        String email = "notfoundemail@teste";
        when(partyOwnerRepository.findByEmail(email)).thenReturn(Optional.empty());

        //quando e entao

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> partyOwnerService.loadUserByUsername(email)
        );

        assertEquals("Usuário não encontrado com email: " + email, exception.getMessage());
        verify(partyOwnerRepository, times(1)).findByEmail(email);

    }


}
