package com.ada.holiday_party_planning.service;

import com.ada.holiday_party_planning.dto.CreateEventDTO;
import com.ada.holiday_party_planning.dto.UpdateEventDTO;
import com.ada.holiday_party_planning.enums.CategoryFun;
import com.ada.holiday_party_planning.exceptions.EventDeleteConflictException;
import com.ada.holiday_party_planning.exceptions.EventNotFoundException;
import com.ada.holiday_party_planning.exceptions.PartyOwnerNotFoundException;
import com.ada.holiday_party_planning.model.Event;
import com.ada.holiday_party_planning.model.Guest;
import com.ada.holiday_party_planning.model.PartyOwner;
import com.ada.holiday_party_planning.repository.EventRepository;
import com.ada.holiday_party_planning.repository.GuestRepository;
import com.ada.holiday_party_planning.repository.ItemRepository;
import com.ada.holiday_party_planning.repository.PartyOwnerRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private PartyOwnerRepository partyOwnerRepository;

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private EventService eventService;

    EventServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    // Testes para o método createEvent
    @Test
    void dadoOwnerIdValido_quandoCreateEvent_entaoEventoEhCriado() {
        // Dado
        UUID ownerId = UUID.randomUUID();
        CreateEventDTO createEventDTO = new CreateEventDTO(
                "Festa de Natal",                // theme
                "Festa com tema natalino",       // title
                LocalDateTime.now(),             // date
                "Salão de Festas",               // place
                "Celebração com amigos",         // description
                true,                            // funActivate
                "Música ao Vivo",                // categoryFun
                null                             // partyOwnerDTO (pode ser null se não utilizado)
        );

        PartyOwner mockPartyOwner = new PartyOwner();
        mockPartyOwner.setOwnerId(ownerId);

        when(partyOwnerRepository.findById(ownerId)).thenReturn(Optional.of(mockPartyOwner));

        // Quando
        eventService.createEvent(ownerId, createEventDTO);

        // Então
        verify(partyOwnerRepository).findById(ownerId);
    }



    @Test
    void dadoOwnerIdInvalido_quandoCreateEvent_entaoLancaExcecao() {
        // Dado
        UUID ownerId = UUID.randomUUID();
        CreateEventDTO createEventDTO = new CreateEventDTO();

        when(partyOwnerRepository.findById(ownerId)).thenReturn(Optional.empty());

        // Quando/Então
        assertThrows(PartyOwnerNotFoundException.class, () -> eventService.createEvent(ownerId, createEventDTO));
        verify(eventRepository, never()).save(any(Event.class));
    }

    // Testes para o método updateEvent
    @Test
    void dadoEventIdValido_quandoUpdateEvent_entaoEventoEhAtualizado() {
        // Dado
        UUID eventId = UUID.randomUUID();
        UpdateEventDTO updateEventDTO = new UpdateEventDTO();
        updateEventDTO.setTitle("Festa de Ano Novo");
        updateEventDTO.setDescription("Ano novo, nova festa!");

        Event mockEvent = new Event();
        mockEvent.setEventId(eventId);
        mockEvent.setTitle("Festa Antiga");

        // Mock do repositório para retornar o mockEvent
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(mockEvent));
        // Mock do save para simular a persistência do evento
        when(eventRepository.save(mockEvent)).thenReturn(mockEvent);

        // Quando
        eventService.updateEvent(eventId, updateEventDTO);

        // Então
        verify(eventRepository, times(1)).save(mockEvent); // Verifica se o save foi chamado
        assertEquals("Festa de Ano Novo", mockEvent.getTitle()); // Verifica se o título foi atualizado
        assertEquals("Ano novo, nova festa!", mockEvent.getDescription()); // Verifica se a descrição foi atualizada
    }


    @Test
    void dadoEventIdInvalido_quandoUpdateEvent_entaoLancaExcecao() {
        // Dado
        UUID eventId = UUID.randomUUID();
        UpdateEventDTO updateEventDTO = new UpdateEventDTO();

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        // Quando/Então
        assertThrows(EventNotFoundException.class, () -> eventService.updateEvent(eventId, updateEventDTO));
        verify(eventRepository, never()).save(any(Event.class));
    }

    // Testes para o método deleteEvent
    @Test
    void dadoEventIdValidoESemConvidadosConfirmados_quandoDeleteEvent_entaoEventoEhExcluido() {
        // Dado
        UUID eventId = UUID.randomUUID();
        Guest guest = new Guest();
        guest.setEvent(new Event());
        guest.getEvent().setEventId(eventId);
        guest.setConfirmed(false);

        when(guestRepository.findAll()).thenReturn(List.of(guest));
        doNothing().when(eventRepository).deleteById(eventId);

        // Quando
        eventService.deleteEvent(eventId);

        // Então
        verify(eventRepository, times(1)).deleteById(eventId);
        verify(guestRepository, times(1)).deleteById(guest.getGuestId());
    }

    @Test
    void dadoEventIdComConvidadosConfirmados_quandoDeleteEvent_entaoLancaExcecao() {
        // Dado
        UUID eventId = UUID.randomUUID();
        Guest guest = new Guest();
        guest.setEvent(new Event());
        guest.getEvent().setEventId(eventId);
        guest.setConfirmed(true);

        when(guestRepository.findAll()).thenReturn(List.of(guest));

        // Quando/Então
        assertThrows(EventDeleteConflictException.class, () -> eventService.deleteEvent(eventId));
        verify(eventRepository, never()).deleteById(eventId);
    }

    // Testes para o método listAllEvent
    @Test
    void quandoListAllEvent_entaoRetornaTodosOsEventos() {
        // Dado
        Event event1 = new Event();
        Event event2 = new Event();
        when(eventRepository.findAll()).thenReturn(List.of(event1, event2));

        // Quando
        List<Event> events = eventService.listAllEvent();

        // Então
        assertEquals(2, events.size());
        verify(eventRepository, times(1)).findAll();
    }
}
