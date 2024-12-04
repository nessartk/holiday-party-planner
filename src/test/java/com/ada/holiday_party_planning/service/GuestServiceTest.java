package com.ada.holiday_party_planning.service;

import com.ada.holiday_party_planning.dto.GuestDTO;
import com.ada.holiday_party_planning.model.Event;
import com.ada.holiday_party_planning.model.Guest;
import com.ada.holiday_party_planning.repository.EventRepository;
import com.ada.holiday_party_planning.repository.GuestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;
import static com.ada.holiday_party_planning.enums.GuestStatusEnum.CONFIRMED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class GuestServiceTest {

    @Mock
    private  GuestRepository guestRepository;
    @Mock
    private  EventRepository eventRepository;
    @Autowired
    private GuestService guestService;


    @BeforeEach
    void setUp() {
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
        Mockito.verify(guestRepository, Mockito.times(1)).findAll();
    }

}
