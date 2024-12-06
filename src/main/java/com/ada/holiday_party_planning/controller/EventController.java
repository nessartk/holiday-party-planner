package com.ada.holiday_party_planning.controller;

import com.ada.holiday_party_planning.dto.*;
import com.ada.holiday_party_planning.model.Event;
import com.ada.holiday_party_planning.service.EmailService;
import com.ada.holiday_party_planning.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para gerenciar operações relacionadas a eventos.
 * Define endpoints para criar, atualizar, listar e excluir eventos.
 */

@RestController
@RequestMapping("/event")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private EmailService emailService;

    /**
     * Construtor que injeta o serviço de eventos.
     *
     * @param eventService Serviço para manipulação de dados de eventos.
     */

    /**
     * Obtém todos os eventos cadastrados.
     *
     * @return Lista de todos os eventos no sistema.
     */

    @GetMapping("/all")
    public ResponseEntity<List<Event>> findAllEvent() {
        List<Event> events = eventService.listAllEvent();
        return ResponseEntity.ok(events);
    }

    /**
     * Lista os eventos de um proprietário específico.
     *
     * @param ownerId Identificador do proprietário dos eventos.
     * @return Lista de eventos do proprietário especificado.
     */

    @GetMapping("/{ownerId}/list")
    public ResponseEntity<List<EventWithPartyOwnerDTO>> findByEventOwner(@PathVariable UUID ownerId) {
        List<EventWithPartyOwnerDTO> events = eventService.eventsByPartyOwner(ownerId);
        return ResponseEntity.ok(events);
    }

    /**
     * Cria um novo evento associado a um proprietário específico.
     *
     * @param ownerId Identificador do proprietário do evento.
     * @param eventDto   Dados do evento a ser criado.
     * @return Resposta HTTP com status 201 Created em caso de sucesso.
     */

    @PostMapping("/{ownerId}/create")
    public ResponseEntity<UUID> createEvent(@PathVariable UUID ownerId, @RequestBody CreateEventDTO eventDto) {
       Event event =  eventService.createEvent(ownerId, eventDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(event.getEventId());
    }

    /**
     * Atualiza um evento existente.
     *
     * @param eventId        Identificador do evento a ser atualizado.
     * @param updateEventDTO Dados para atualização do evento.
     * @return Resposta HTTP com status 200 OK em caso de sucesso.
     */

    @PutMapping("/{eventId}/update")
    public ResponseEntity<Void> updateEvent(@PathVariable UUID eventId, @Valid @RequestBody UpdateEventDTO updateEventDTO) {
        eventService.updateEvent(eventId, updateEventDTO);
        return ResponseEntity.ok().build();
    }

    /**
     * Exclui um evento com base no ID fornecido.
     *
     * @param id Identificador do evento a ser excluído.
     * @return Resposta HTTP com status 204 No Content em caso de sucesso.
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{eventId}/send-invites")
    public ResponseEntity<Void> sendInvites(@PathVariable UUID eventId) {
        eventService.sendInvites(eventId);
        return ResponseEntity.ok().build();
    }

}
