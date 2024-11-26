package com.ada.holiday_party_planning.service;

import com.ada.holiday_party_planning.dto.CreateEventDTO;
import com.ada.holiday_party_planning.dto.EventWithPartyOwnerDTO;
import com.ada.holiday_party_planning.dto.UpdateEventDTO;
import com.ada.holiday_party_planning.enums.CategoryFun;
import com.ada.holiday_party_planning.exceptions.EventDeleteConflictException;
import com.ada.holiday_party_planning.exceptions.EventNotFoundException;
import com.ada.holiday_party_planning.exceptions.PartyOwnerNotFoundException;
import com.ada.holiday_party_planning.mappers.EventMapper;
import com.ada.holiday_party_planning.model.Event;
import com.ada.holiday_party_planning.model.Guest;
import com.ada.holiday_party_planning.model.PartyOwner;
import com.ada.holiday_party_planning.repository.EventRepository;
import com.ada.holiday_party_planning.repository.GuestRepository;
import com.ada.holiday_party_planning.repository.ItemRepository;
import com.ada.holiday_party_planning.repository.PartyOwnerRepository;
import com.ada.holiday_party_planning.util.APIFunTranlation;
import com.ada.holiday_party_planning.util.APIGoogleTranslate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Serviço para gerenciar eventos e a lógica de negócio relacionada aos eventos e seus donos.
 *
 * Esta classe oferece operações para criar, atualizar, listar, excluir eventos e realizar traduções assíncronas para os eventos.
 */

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private PartyOwnerRepository partyOwnerRepository;
    @Autowired
    private GuestRepository guestRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private EmailService emailService;


    /**
     * Construtor do serviço que recebe os repositórios necessários.
     *
     * @param eventRepository      Repositório para manipulação de eventos.
     * @param partyOwnerRepository Repositório para manipulação dos donos de festa.
     * @param guestRepository      Repositório para manipulação de convidados.
     * @param itemRepository       Repositório para manipulação de itens.
     */


    /**
     * Cria um novo evento e o associa a um dono de festa.
     *
     * @param ownerID        O ID do dono da festa.
     * @param createEventDTO O DTO contendo os dados para criação do evento.
     * @throws ResponseStatusException Se o dono da festa não for encontrado.
     */

    public void createEvent(UUID ownerID, CreateEventDTO createEventDTO) {
        EventMapper eventMapper = new EventMapper(partyOwnerRepository, eventRepository);
        PartyOwner partyOwner = partyOwnerRepository.findById(ownerID)
                .orElseThrow(PartyOwnerNotFoundException::new);
        Event event =  eventMapper.createDTOToModel(createEventDTO,partyOwner);
        if (event.getFunActivate()) {
            try {
                CategoryFun category = CategoryFun.valueOf(event.getCategoryFun().toLowerCase());
                String mensagemTraduzida = translateFun(event.getDescription(), category);
                event.setDescriptionTranslateFun(mensagemTraduzida);
            } catch (IllegalArgumentException e) {
                System.out.println("Erro: Categoria não encontrada para a descrição do evento.");
            }
        }
        eventRepository.save(event);
    }

    /**
     * Atualiza as informações de um evento existente.
     *
     * @param eventId        O ID do evento a ser atualizado.
     * @param updateEventDTO O DTO contendo os dados para atualização do evento.
     * @throws ResponseStatusException Se o evento não for encontrado.
     */

    public void updateEvent(UUID eventId, UpdateEventDTO updateEventDTO) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);
        event.setTheme(updateEventDTO.getTheme());
        event.setTitle(updateEventDTO.getTitle());
        event.setDate(updateEventDTO.getDate());
        event.setPlace(updateEventDTO.getPlace());
        event.setDescription(updateEventDTO.getDescription());
        event.setFunActivate(updateEventDTO.getFunActivate());
        if (event.getFunActivate()) {
            try {
                CategoryFun category = CategoryFun.valueOf(event.getCategoryFun().toLowerCase());
                String mensagemTraduzida = translateFun(event.getDescription(), category);
                event.setDescriptionTranslateFun(mensagemTraduzida);
            } catch (IllegalArgumentException e) {
                System.out.println("Erro: Categoria não encontrada para a descrição do evento.");
            }
        }
        eventRepository.save(event);
    }

    /**
     * Lista todos os eventos cadastrados.
     *
     * @return Uma lista com todos os eventos.
     */

    public List<Event> listAllEvent() {
        return eventRepository.findAll();
    }

    /**
     * Lista todos os eventos de um dono específico, retornando os dados no formato DTO.
     *
     * @param ownerID O ID do dono da festa.
     * @return Uma lista de eventos com os dados do dono da festa.
     */

    public List<EventWithPartyOwnerDTO> eventsByPartyOwner(UUID ownerID) {
        EventMapper eventMapper = new EventMapper(partyOwnerRepository, eventRepository);
        List<EventWithPartyOwnerDTO> allEvents = eventMapper.eventWithPartyOwnerDTO(ownerID);
        return allEvents;
    }

    /**
     * Exclui um evento e seus dados relacionados, caso não haja convidados confirmados para o evento.
     * <p>
     * O método verifica se há convidados confirmados para o evento com o ID fornecido.
     * Caso algum convidado esteja confirmado, uma exceção é lançada e a exclusão não é permitida.
     * Se não houver convidados confirmados, o método realiza uma exclusão em cascata: remove
     * todos os convidados e itens relacionados ao evento antes de excluir o próprio evento.
     *
     * @param eventID O ID do evento a ser excluído.
     * @throws ResponseStatusException com status CONFLICT se houver convidados confirmados para o evento.
     */

    public void deleteEvent(UUID eventID) throws ResponseStatusException {
        List<Guest> guests = guestRepository.findAll();

        boolean hasConfirmedGuest = guests.stream()
                .filter(guest -> eventID.equals(guest.getEvent().getEventId()))
                .anyMatch(Guest::isConfirmed);

        if (hasConfirmedGuest) {
            throw new EventDeleteConflictException();
        }

        guestRepository.findAll().stream()
                .filter(guest -> eventID.equals(guest.getEvent().getEventId()))
                .forEach(guest -> guestRepository.deleteById(guest.getGuestId()));

        itemRepository.findAll().stream()
                .filter(item -> eventID.equals(item.getEvent().getEventId()))
                .forEach(item -> itemRepository.deleteById(item.getItemId()));

        eventRepository.deleteById(eventID);
    }

    /**
     //     * Realiza a tradução assíncrona da descrição do evento, se ativado.
     //     *
     //     * @param message define a mensagem a ser traduzida
     //     * @param category define em qual lingua vai traduzir
     //     */

    @Async
    public String translateFun(String message, CategoryFun category) {
        if (message != null) {
            String textTranslate = APIGoogleTranslate.translateMensage(message, "pt-br", "en");
            String translatedText = APIFunTranlation.tranlateFun(textTranslate, category.name().toLowerCase());

            if (category.isRepeat()) {
                translatedText = APIGoogleTranslate.translateMensage(translatedText, "en", "pt-br");
            }

            return translatedText;
        }
        return "";
    }

    public void sendInvites(UUID eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found."));
        List<Guest> guests = guestRepository.findByEvent(event);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm");

        for (Guest guest : guests) {
            Map<String, String> variables = new HashMap<>();
            variables.put("eventTitle", event.getTitle());
            variables.put("hostName", event.getOwner().getName());
            variables.put("eventDate", event.getDate().format(formatter));
            variables.put("eventLocation", event.getPlace());
            variables.put("eventLink", "http://localhost:8080/events/" + event.getEventId());

            try {
                emailService.sendEmail(guest.getEmail(), "You're Invited!", variables);
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send email.");
            }
        }
    }
}