package com.ada.holiday_party_planning.service;

import com.ada.holiday_party_planning.exceptions.EventNotFoundException;
import com.ada.holiday_party_planning.exceptions.GuestNotFoundException;
import com.ada.holiday_party_planning.exceptions.ItemNotFoundException;
import com.ada.holiday_party_planning.model.Event;
import com.ada.holiday_party_planning.model.Item;
import com.ada.holiday_party_planning.model.Guest;
import com.ada.holiday_party_planning.repository.EventRepository;
import com.ada.holiday_party_planning.repository.GuestRepository;
import com.ada.holiday_party_planning.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceTest {

    private ItemService itemService;// classe q contem os metodos do Item
    private EventRepository eventRepository;
    private ItemRepository itemRepository;
    private GuestRepository guestRepository;

    @BeforeEach
    void setUp() {
        eventRepository = mock(EventRepository.class);
        guestRepository = mock(GuestRepository.class);
        itemRepository = Mockito.mock(ItemRepository.class);
        itemService = new ItemService(itemRepository, eventRepository, guestRepository);


    }

    @Test
    void dadoUmItemEUmEventIdValidos_quandoCreateItem_entaoRetornarItemSalvo() {
        // Dado
        UUID eventId = UUID.randomUUID();
        Item item = new Item();
        item.setName("chester");
        item.setQuantity(1);
        item.setValue(60.0);
        Event event = new Event();
        event.setEventId(eventId);
        event.setDate(LocalDateTime.now());
        event.setPlace("Casa da Monica");
        event.setTheme("Ceia de Natal");
        event.setDescription("Rua do Limoeiro");

        when(eventRepository.findById(eventId))
                .thenReturn(Optional.of(event));

        when(itemRepository.save(item))
                .thenAnswer(invocation -> {
                    Item savedItem = invocation.getArgument(0);
                    savedItem.setItemId(UUID.randomUUID()); // simula geracao Id
                    return savedItem;
                });

        // Quando
        Item savedItem = itemService.createItem(item, eventId);

        // Entao
        assertNotNull(savedItem);
        assertEquals(savedItem.getEvent(), event);
        verify(eventRepository, times(1)).findById(eventId);
        verify(itemRepository, times(1)).save(item);


    }

    @Test
    void dadoUmItemEUmEventIdInexistente_quandoCreateItem_entaoLancaEventNotFoundException() {
        // Dado
        UUID eventId =UUID.randomUUID();
        Item item = new Item();
        item.setName("chester");
        item.setQuantity(1);
        item.setValue(60.0);

        when(eventRepository.findById(eventId))
                .thenReturn(Optional.empty());

        // Quando e Entao
        assertThrows(EventNotFoundException.class, () -> itemService.createItem(item, eventId));
        verifyNoMoreInteractions(itemRepository);

    }


    @Test
    void dadoUmItemEUmEventIdValidos_quandoUpdateItem_entaoRetornarItemSalvo() {
        //Dado
       UUID eventId = UUID.randomUUID();
       Event mockEvent = new Event();
       mockEvent.setEventId(eventId);

       Item mockItem = new Item();
       Item updatedItem = new Item();
       updatedItem.setEvent(mockEvent);

        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.of(mockEvent));
        Mockito.when(itemRepository.save(mockItem)).thenReturn(updatedItem);

        // Quando
        Item result = itemService.updateItem(mockItem,eventId);

        //Entao
        Mockito.verify(eventRepository).findById(eventId);
        Mockito.verify(itemRepository).save(mockItem);
        assertEquals(mockEvent, result.getEvent());

    }

    @Test
    void dadoUmItemEUmEventIdInexistente_quandoUpdateItem_entaoLancaEventNotFoundException(){
        //Dado
        UUID invalidEventId = UUID.randomUUID();
        Item mockItem = new Item();

        Mockito.when(eventRepository.findById(invalidEventId)).thenReturn(Optional.empty());

        //Quando e Entao
        assertThrows(EventNotFoundException.class, () ->
            itemService.updateItem(mockItem, invalidEventId));


        Mockito.verify(eventRepository).findById(invalidEventId);
        Mockito.verifyNoMoreInteractions(itemRepository);

    }

    @Test
    void dadoItemsAUmEventIdEspecifico_quandoListaItensByEventId_entaoRetornaItensByEventIdEspecifico() {
        //dado

        UUID eventId = UUID.randomUUID();

        Item item1 = new Item(UUID.randomUUID(), new Event(eventId));
        Item item2 = new Item(UUID.randomUUID(), new Event(eventId));
        Item item3 = new Item(UUID.randomUUID(), new Event(UUID.randomUUID()));

        List<Item> mockItems = new ArrayList<>(List.of(item1,item2,item3));
        when(itemRepository.findAll()).thenReturn(mockItems);

        //quando
        List<Item> result= itemService.itemsByEventId(eventId);

        //entao
        assertEquals(2,result.size());
        assertTrue(result.contains(item1));
        assertTrue(result.contains(item2));
        assertFalse(result.contains(item3));

        verify(itemRepository,times(1)).findAll();

    }

    @Test
    void dadoItemsByEventId_quandoLancaNoItemsFoundException_entaoFiltraListaVazia(){
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        UUID eventId = UUID.randomUUID();

        when(itemRepository.findAll()).thenReturn(new ArrayList<>());

        ItemService itemService = new ItemService(itemRepository,eventRepository,guestRepository);

        assertThrows(ItemNotFoundException.class, ()-> itemService.itemsByEventId(eventId));
        verify(itemRepository,times(1)).findAll();
    }

    //Item Service / Delete item - Teste Caminho Feliz
    @Test
    void dadoUmItemIdExistente_quandoDeleteItem_entaoDeletaItem() {
        //Dado
        UUID itemId = UUID.randomUUID();
        Item item = new Item();
        item.setItemId(itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        doNothing().when(itemRepository).delete(item);

        //Quando
        itemService.deleteItem(itemId);

        //Então
        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, times(1)).delete(item);
    }

    @Test
    void dadoUmItemIdInexistente_quandoDeleteItem_entaoLancaItemNotFoundException() {
        //Dado
        UUID itemId = UUID.randomUUID();

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        //Quando e Então
        assertThrows(ItemNotFoundException.class, () -> itemService.deleteItem(itemId));
        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, never()).delete(any());
    }


    @Test
    void isItemWithGuest() {
    }

    @Test
    void itemsByGuest() {
    }
    //teste itemsByGuest
    //caminho feliz:



    //teste addItemToGuest
    //caminho feliz
    @Test
    void dadoUmGuestIdEItemIdValidos_quandoAddItemToGuest_entaoItemEAssociadoAoGuest() {
        // Dado
        UUID guestId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        Guest mockGuest = new Guest();
        mockGuest.setGuestId(guestId);
        mockGuest.setName("Maria");

        Item mockItem = new Item();
        mockItem.setItemId(itemId);
        mockItem.setName("Sobremesa");

        when(guestRepository.findById(guestId)).thenReturn(Optional.of(mockGuest));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(mockItem));
        when(itemRepository.save(mockItem)).thenAnswer(invocation -> invocation.getArgument(0));

        // Quando
        Item updatedItem = itemService.addItemToGuest(guestId, itemId);

        // Então
        assertNotNull(updatedItem, "O item atualizado não deve ser nulo");
        assertNotNull(updatedItem.getGuest(), "O convidado deve estar associado ao item");
        assertEquals(mockGuest, updatedItem.getGuest(), "O convidado associado deve ser o mockGuest");
        verify(guestRepository, times(1)).findById(guestId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, times(1)).save(mockItem);
    }


    //Caminho erro:
    //Guest Inexistente
    @Test
    void dadoUmGuestIdInvalidoEItemIdValido_quandoAddItemToGuest_entaoLancaGuestNotFoundException() {
        // Dado
        UUID guestId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        Item item = new Item();
        item.setItemId(itemId);
        item.setName("Bebida");

        when(guestRepository.findById(guestId)).thenReturn(Optional.empty());
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        // Quando e Então
        assertThrows(
                GuestNotFoundException.class,
                () -> itemService.addItemToGuest(guestId, itemId),
                "Deve lançar GuestNotFoundException para um guestId inválido"
        );

        verify(guestRepository, times(1)).findById(guestId);
        verify(itemRepository, never()).save(any());
    }

    //Item Inexistente
    @Test
    void dadoUmGuestIdValidoEItemIdInvalido_quandoAddItemToGuest_entaoLancaItemNotFoundException() {
        // Dado
        UUID guestId = UUID.randomUUID();
        UUID invalidItemId = UUID.randomUUID();

        Guest mockGuest = new Guest();
        mockGuest.setGuestId(guestId);
        mockGuest.setName("Maria");

        when(guestRepository.findById(guestId)).thenReturn(Optional.of(mockGuest));
        when(itemRepository.findById(invalidItemId)).thenReturn(Optional.empty());

        // Quando e Então
        assertThrows(
                ItemNotFoundException.class,
                () -> itemService.addItemToGuest(guestId, invalidItemId),
                "Deve lançar ItemNotFoundException para um itemId inválido"
        );

        verify(guestRepository, times(1)).findById(guestId);
        verify(itemRepository, times(1)).findById(invalidItemId);
        verify(itemRepository, never()).save(any());
    }




}