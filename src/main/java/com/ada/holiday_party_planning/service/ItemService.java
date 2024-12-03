package com.ada.holiday_party_planning.service;

import com.ada.holiday_party_planning.exceptions.EventNotFoundException;
import com.ada.holiday_party_planning.exceptions.ItemNotFoundException;
import com.ada.holiday_party_planning.exceptions.GuestNotFoundException;
import com.ada.holiday_party_planning.model.Event;
import com.ada.holiday_party_planning.model.Guest;
import com.ada.holiday_party_planning.model.Item;
import com.ada.holiday_party_planning.repository.EventRepository;
import com.ada.holiday_party_planning.repository.GuestRepository;
import com.ada.holiday_party_planning.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final EventRepository eventRepository;
    private final GuestRepository guestRepository;


    public ItemService(ItemRepository itemRepository, EventRepository eventRepository, GuestRepository guestRepository) {
        this.itemRepository = itemRepository;
        this.eventRepository = eventRepository;
        this.guestRepository = guestRepository;
    }


    public Item createItem(Item item, UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);
        item.setEvent(event);
        return itemRepository.save(item);
    }

    public Item updateItem(Item item, UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);
        item.setEvent(event);
        return itemRepository.save(item);
    }


    public List<Item> itemsByEventId(UUID eventId) {
        List<Item> allItems = itemRepository.findAll();
        allItems.removeIf(item -> !eventId.equals(item.getEvent().getEventId()));
        if (allItems.isEmpty()) {
            throw new ItemNotFoundException("No items found for the given eventId: " + eventId);
        }
        return allItems;
    }


    public void deleteItem(UUID itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
        itemRepository.delete(item);
    }


    //metodo q verifica se um item está associado a um convidado específico
    public boolean isItemWithGuest(UUID itemId, UUID guestId) {
        Item item = itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
        return item.getGuest().getGuestId().equals(guestId);
    }


    public List<Item> itemsByGuestId(UUID guestId) {
        return itemRepository.findAll().stream().filter(item -> item.getGuest() != null && item.getGuest().getGuestId().equals(guestId)).toList();
    }

    public Item addItemToGuest(UUID guestId, UUID itemId) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(GuestNotFoundException::new);

        Item item = itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
        item.setGuest(guest);

        return itemRepository.save(item);

    }

    public Item removeGuestFromItem(UUID guestId, UUID itemId) {

        if (isItemWithGuest(itemId, guestId)) {
            Item item = itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);

            item.setGuest(null);

            return itemRepository.save(item);
        } else {
            throw new ItemNotFoundException();
        }

    }
}
