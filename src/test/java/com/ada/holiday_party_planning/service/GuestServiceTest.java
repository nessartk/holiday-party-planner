package com.ada.holiday_party_planning.service;

import com.ada.holiday_party_planning.dto.CreateGuestDTO;
import com.ada.holiday_party_planning.dto.GuestDTO;
import com.ada.holiday_party_planning.enums.GuestStatusEnum;
import com.ada.holiday_party_planning.exceptions.GuestNotFoundException;
import com.ada.holiday_party_planning.mappers.GuestMapper;
import com.ada.holiday_party_planning.model.Event;
import com.ada.holiday_party_planning.model.Guest;
import com.ada.holiday_party_planning.repository.EventRepository;
import com.ada.holiday_party_planning.repository.GuestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.*;

import static com.ada.holiday_party_planning.enums.GuestStatusEnum.CONFIRMED;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doNothing;

public class GuestServiceTest {

    @Mock
    private  GuestRepository guestRepository;
    @Mock
    private  EventRepository eventRepository;
    @InjectMocks
    private GuestService guestService;

    private Event event;

    @BeforeEach
    void setUp() {
        event = new Event();
        eventRepository = mock(EventRepository.class);
        guestRepository = mock(GuestRepository.class);
        guestService = new GuestService(guestRepository,eventRepository);
        }



    @Test
    public void dadoAllGuests_quandoMetodoListaGuests_entaoRetornarListaDeGuestDTOs() {
        // Dado
        Guest guest1 = new Guest(UUID.randomUUID(),CONFIRMED,"guest1@teste", "Guest1", new Event(),true);
        Guest guest2 = new Guest(UUID.randomUUID(),CONFIRMED,"guest2@teste","Guest2", new Event(),true);
        List<Guest> guestList = List.of(guest1, guest2);

        GuestDTO guestDTO1 = new GuestDTO(guest1.getGuestId(),guest1.getName(),guest1.getEmail(),CONFIRMED);
        GuestDTO guestDTO2 = new GuestDTO(guest2.getGuestId(),guest2.getName(),guest2.getEmail(),CONFIRMED);
        List<GuestDTO> expectedDTOList = List.of(guestDTO1, guestDTO2);


        when(guestRepository.findAll()).thenReturn(guestList);

        // Quando

        List<GuestDTO> result = guestService.getAllGuests();

        // Então
        assertNotNull(result);
        assertEquals(2,result.size());
        assertTrue(result.contains(guestDTO1));
        assertTrue(result.contains(guestDTO2));
        assertEquals(expectedDTOList.size(), result.size());
        assertEquals(expectedDTOList, result);
        verify(guestRepository, Mockito.times(1)).findAll();
    }

    @Test
    void dadoListaDeGuests_quandoForVazia_entaoRetornarQueListaEstaVazia() {

        // dado
        when(guestRepository.findAll()).thenReturn(Collections.emptyList());

        // quando
        List<GuestDTO> result = guestService.getAllGuests();

        // entao
        assertTrue(result.isEmpty(), "A lista deve estar vazia");
        verify(guestRepository, times(1)).findAll();
    }

    @Test
    public void dadoGuestId_quandoMetodoGetGuestById_entaoRetornarGuestDTO() {

        // dado
        UUID guestId = UUID.randomUUID();
        Guest guest = new Guest(guestId,CONFIRMED,"guest1@teste", "Guest1", new Event(),true);
        GuestDTO expectedDTO = new GuestDTO(guest.getGuestId(),guest.getName(),guest.getEmail(),CONFIRMED);

        when(guestRepository.findById(guestId)).thenReturn(java.util.Optional.of(guest));

        // quando
        Optional<GuestDTO> result = guestService.getGuestById(guestId);

        // entao
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(expectedDTO, result.get());
        verify(guestRepository, Mockito.times(1)).findById(guestId);
    }

    @Test
    public void dadoCreateGuestDTO_quandoMetodoCreateGuest_entaoRetornarGuestDTO() {

        // dado
        CreateGuestDTO createGuestDTO = new CreateGuestDTO("Ada Tech", "ada@test.com", GuestStatusEnum.CONFIRMED, event, true);
        Guest guest = new Guest(UUID.randomUUID(), GuestStatusEnum.CONFIRMED, "ada@test.com", "Ada Tech", event, true);
        when(guestRepository.save(any(Guest.class))).thenReturn(guest);

        // quando
        Guest newGuest = guestService.createGuest(createGuestDTO);
        GuestDTO guestDTO = GuestMapper.toDTO(newGuest);

        // entao
        assertNotNull(guestDTO);
        assertEquals("Ada Tech", guestDTO.getName());
        assertEquals("ada@test.com", guestDTO.getEmail());
        assertEquals(GuestStatusEnum.CONFIRMED, guestDTO.getStatus());
        verify(guestRepository, Mockito.times(1)).save(any(Guest.class));
    }


    @Test
    void dadoGuestDTO_quandoAtualizarGuest_entaoRetornarGuestAtualizado() {

        // dado
        UUID guestId = UUID.randomUUID();
        Guest existingGuest = new Guest(guestId, GuestStatusEnum.PENDING, "old@test.com", "Old test", null, false);
        GuestDTO updatedGuestDTO = new GuestDTO(guestId, "Updated test", "updated@test.com", GuestStatusEnum.CONFIRMED);

        when(guestRepository.findById(guestId)).thenReturn(Optional.of(existingGuest));
        when(guestRepository.save(any(Guest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // quando
        Optional<GuestDTO> result = guestService.updateGuest(guestId, updatedGuestDTO);

        // entao
        assertTrue(result.isPresent());
        assertEquals("Updated test", result.get().getName());
        assertEquals("updated@test.com", result.get().getEmail());
        assertEquals(GuestStatusEnum.CONFIRMED, result.get().getStatus());
    }

    @Test
    void dadoGuestId_quandoExcluirGuest_entaoGuestDeveSerRemovido() {

        // dado
        UUID guestId = UUID.randomUUID();
        Guest guest = new Guest(guestId, GuestStatusEnum.CONFIRMED, "ada@test.com", "Ada Tech", null, true);

        when(guestRepository.findById(guestId)).thenReturn(Optional.of(guest));
        doNothing().when(guestRepository).delete(guest);

        // quando e entao
        assertDoesNotThrow(() -> guestService.deleteGuest(guestId));
        verify(guestRepository, times(1)).findById(guestId);
        verify(guestRepository, times(1)).delete(guest);
    }

    @Test
    void dadoGuestIdInexistente_quandoExcluirGuest_entaoLancaExcecao() {

        // dado
        UUID guestId = UUID.randomUUID();

        when(guestRepository.findById(guestId)).thenReturn(Optional.empty());

        // quando e entao
        assertThrows(GuestNotFoundException.class, () -> guestService.deleteGuest(guestId));
        verify(guestRepository, times(1)).findById(guestId);
        verify(guestRepository, never()).delete(any(Guest.class));
    }

}
