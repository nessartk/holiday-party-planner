package com.ada.holiday_party_planning.service;

import com.ada.holiday_party_planning.dto.*;
import com.ada.holiday_party_planning.exceptions.EmailAlreadyExistsException;
import com.ada.holiday_party_planning.exceptions.InvalidCredentialsException;
import com.ada.holiday_party_planning.exceptions.PartyOwnerNotFoundException;
import com.ada.holiday_party_planning.mappers.PartyOwnerMapper;
import com.ada.holiday_party_planning.model.PartyOwner;
import com.ada.holiday_party_planning.repository.PartyOwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.*;
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
        CreatePartyOwnerDTO createPartyOwnerDTO = new CreatePartyOwnerDTO("Monica", "monica@teste", "password");

        PartyOwner partyOwner = new PartyOwner();
        partyOwner.setName("Monica");
        partyOwner.setEmail("monica@teste");
        partyOwner.setPassword("encodedPassword");

        PartyOwnerDTO expectedDTO = new PartyOwnerDTO(UUID.randomUUID(), "Monica", "monica@teste");

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
    void dadoInvalidoCreatePartyOwnerDTO_QuandoCreatePartyOwnerDTO_entaoLancaEmailAlreadyExist() {
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
    void dadoCredenciaisValidas_quandoFizerLogin_entaoRetornaPartyOwnerResponseLoginDTO() {
        //dado
        PartyOwnerLoginDTO userLoginInfo = new PartyOwnerLoginDTO("monica@teste", "password");
        PartyOwner partyOwner = new PartyOwner();
        partyOwner.setEmail("monica@teste");
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        partyOwner.setPassword(passwordEncoder.encode("password"));

        when(partyOwnerRepository.findByEmail("monica@teste"))
                .thenReturn(Optional.of(partyOwner));

        // quando
        PartyOwnerLoginResponseDTO result = partyOwnerService.login(userLoginInfo);

        // entao
        assertNotNull(result);
        assertEquals(result.getEmail(), "monica@teste");
        verify(partyOwnerRepository).findByEmail("monica@teste");

    }

    @Test
    void dadoEmailNaoExistente_quandoFizerLogin_entaoLancaPartyOwnerNotFoundException() {
        //dado
        PartyOwnerLoginDTO userLoginInfo = new PartyOwnerLoginDTO("emailInexistente@teste", "password");

        when(partyOwnerRepository.findByEmail("emailInexistente@teste"))
                .thenReturn(Optional.empty());

        //quando e entao
        assertThrows(PartyOwnerNotFoundException.class, () -> partyOwnerService.login(userLoginInfo));
        verify(partyOwnerRepository).findByEmail("emailInexistente@teste");

    }

    @Test
    void dadoSenhaInvalida_quandoFizerLogin_entaoLancaInvalidCredentialsException() {
        // dado
        PartyOwnerLoginDTO userLoginInfo = new PartyOwnerLoginDTO("monica@teste", "senhaInvalida");

        PartyOwner partyOwner = new PartyOwner();
        partyOwner.setEmail("monica@teste");
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        partyOwner.setPassword(passwordEncoder.encode("senhaValida"));
        when(partyOwnerRepository.findByEmail("monica@teste"))
                .thenReturn(Optional.of(partyOwner));

        // quando e entao
        assertThrows(InvalidCredentialsException.class, () -> partyOwnerService.login(userLoginInfo));
        verify(partyOwnerRepository).findByEmail("monica@teste");


    }

    @Test
    void dadoListaPartyOwners_quandoChamaMetodoGetAllPartyOwners_entaoRetornaListaTodosPartyOwners() {
        //dado
        List<PartyOwner> partyOwners = Arrays.asList(
                new PartyOwner("Owner1", "owner1@teste", "password1"),
                new PartyOwner("Owner2", "owner2@teste", "password2")
        );
        List<PartyOwnerDTO> partyOwnerDTOS = Arrays.asList(
                new PartyOwnerDTO(UUID.randomUUID(), "Owner1", "owner1@teste"),
                new PartyOwnerDTO(UUID.randomUUID(), "Owner2", "owner2@teste")
        );

        when(partyOwnerRepository.findAll()).thenReturn(partyOwners);

        // quando
        List<PartyOwnerDTO> result = partyOwnerService.getAllPartyOwners();

        //entao
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Owner1", result.get(0).getName());
        assertEquals("Owner2", result.get(1).getName());

        verify(partyOwnerRepository, times(1)).findAll();
    }

    @Test
    void dadoListaPartyOwnersVazia_quandoChamaMetodoGettAllPartyOwners_entaoRetornaPartyOwnerNotFoundException() {
        //dado
        when(partyOwnerRepository.findAll()).thenReturn(Collections.emptyList());

        //quando e entao
        PartyOwnerNotFoundException exception = assertThrows(
                PartyOwnerNotFoundException.class,
                () -> partyOwnerService.getAllPartyOwners()
        );
        assertNotNull(exception);
        verify(partyOwnerRepository, times(1)).findAll();
    }

    @Test
    void dadoPartyOwner_quandoAtualizarCadastro_entaoSalvaAtualizacao() {
        //dado
        UUID ownerId = UUID.randomUUID();
        PartyOwner existingPartyOwner = new PartyOwner();
        existingPartyOwner.setOwnerId(ownerId);
        existingPartyOwner.setName("old Name");

        UpdatePartyOwnerDTO newPartyOwner = new UpdatePartyOwnerDTO("new Name","owner@teste");
        PartyOwnerDTO updatedPartyOwnerDTO = new PartyOwnerDTO(ownerId,"new Name","owner@teste");

        when(partyOwnerRepository.findById(ownerId)).thenReturn(Optional.of(existingPartyOwner));
        when(partyOwnerRepository.save(existingPartyOwner)).thenReturn(existingPartyOwner);

        //quando
        Optional<PartyOwnerDTO> result = partyOwnerService.updatePartyOwner(ownerId, newPartyOwner);

        //entao
        assertTrue(result.isPresent());
        assertEquals("new Name", result.get().getName());
        verify(partyOwnerRepository, times(1)).findById(ownerId);
        verify(partyOwnerRepository, times(1)).save(existingPartyOwner);
    }

    @Test
    void dadoPartyOwner_quandoPartyOwnerNaoEncontrado_entaoRetornaPartyOwnerNotFound(){
        //dado
        UUID ownerId = UUID.randomUUID();
        UpdatePartyOwnerDTO newPartyOwner = new UpdatePartyOwnerDTO("new name", "owner@teste");
        when(partyOwnerRepository.findById(ownerId)).thenReturn(Optional.empty());

        //quando
        Optional<PartyOwnerDTO> result = partyOwnerService.updatePartyOwner(ownerId, newPartyOwner);

        //entao
        assertFalse(result.isPresent());
        verify(partyOwnerRepository, times(1)).findById(ownerId);
        verify(partyOwnerRepository, never()).save(any());
    }

    @Test
    void loadUserByUsername() {
    }

}
