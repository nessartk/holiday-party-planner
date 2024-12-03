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
import com.ada.holiday_party_planning.util.APIFunTranlation;
import com.ada.holiday_party_planning.util.APIGoogleTranslate;
import jakarta.mail.MessagingException;
import org.apache.catalina.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.ada.holiday_party_planning.enums.GuestStatusEnum.CONFIRMED;
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

    @Mock
    private final EmailService emailService = mock(EmailService.class);

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

    @Test
    void translateFun(){}
    //TODO pesquisar mock static method


    @Test
    void dadoEventoEConvidados_quandoEnviarConvites_entaoEmailsEnviadosComSucesso() throws MessagingException {
        // Dado
        UUID eventId = UUID.randomUUID();

        Event event = new Event();
        event.setEventId(eventId);
        event.setTitle("Aniversário");
        event.setDate(LocalDateTime.of(2023, 12, 25, 18, 0));
        event.setPlace("Rua das Flores, 123");
        event.setOwner(new PartyOwner("Owner","owner@teste","password"));


        Guest guest1 = new Guest(UUID.randomUUID(),CONFIRMED,"guest1@teste","Guest1", event,true);
        Guest guest2 = new Guest(UUID.randomUUID(),CONFIRMED,"guest2@teste","Guest2", event,true);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(guestRepository.findByEvent(event)).thenReturn(Arrays.asList(guest1, guest2));

        // Quando
        eventService.sendInvites(eventId);

        // Então
        verify(emailService, times(1)).sendEmail(
                eq("guest1@teste"),
                eq("You're Invited!"),
                argThat(variables -> variables.get("eventTitle").equals("Aniversário") &&
                        variables.get("hostName").equals("Owner") &&
                        variables.get("eventLocation").equals("Rua das Flores, 123"))
        );
        verify(emailService, times(1)).sendEmail(
                eq("guest2@teste"),
                eq("You're Invited!"),
                argThat(variables -> variables.get("eventTitle").equals("Aniversário") &&
                        variables.get("hostName").equals("Owner"))
        );
    }


    @Test
    void dadoEventoInexistenteQuandoEnviarConvitesEntaoLancarExcecao() {
        // Dado
        UUID eventId = UUID.randomUUID();
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        // Quando & Então
        assertThrows(ResponseStatusException.class, () -> eventService.sendInvites(eventId));

        verifyNoInteractions(guestRepository, emailService);
    }

}
