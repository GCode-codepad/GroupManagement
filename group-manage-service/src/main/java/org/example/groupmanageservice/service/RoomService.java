package org.example.groupmanageservice.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.groupmanageservice.config.RabbitConfig;
import org.example.groupmanageservice.modules.*;
import org.example.groupmanageservice.modules.domain.ParticipantId;
import org.springframework.cache.annotation.Cacheable;
import org.example.groupmanageservice.dao.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.amqp.core.AmqpTemplate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private static final Logger logger = LoggerFactory.getLogger(RoomService.class);

    /**
     * Creates a new room by generating a unique roomId and a random join password.
     * The host is added as a participant with HOSTER permission.
     */
    public Room createRoom(String hosterUserId) {
        // Generate unique roomId and join password
        String roomId = UUID.randomUUID().toString();
        String joinPassword = generateRandomPassword();

        // Create a new Room instance
        Room room = new Room();
        room.setRoomId(roomId);
        room.setHosterUserId(hosterUserId);
        room.setJoinPassword(joinPassword);
        room.setStatus(Room.Status.ACTIVE);
        room.setCreatedAt(LocalDateTime.now());

        // Create host Participant and add to room's participant list
        Participant host = new Participant();
        host.setId(new ParticipantId(hosterUserId, roomId));
        host.setPermission(Participant.Permission.HOSTER);
        host.setRoom(room);
        ArrayList<Participant> participants = new ArrayList<>();
        participants.add(host);
        room.setParticipants(participants);

        // Save to MySQL and return
        return roomRepository.save(room);
    }

    // Utility method to generate a random 6-digit numeric password
    private String generateRandomPassword() {
        Random random = new Random();
        int number = 100000 + random.nextInt(900000);
        return String.valueOf(number);
    }

    /**
     * Closes the room by marking its status as CLOSED and clearing its participants.
     * This method is transactional so that the lazy-loaded collection is initialized.
     */
    @Transactional
    public Room closeRoom(String roomId, String hoster) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        if (!room.getHosterUserId().equals(hoster)) {
            throw new IllegalArgumentException("Only the host can close the room");
        }
        // Force initialization of participants
        room.getParticipants().size();
        room.setStatus(Room.Status.CLOSED);
        room.getParticipants().clear();
        return roomRepository.save(room);
    }

    // Cache a Room without initializing the participants collection.
    @Cacheable(value = "rooms", key = "#roomId")
    public Room getRoom(String roomId) {
        Optional<Room> room = roomRepository.findById(roomId);
        return room.orElse(null);
    }

    // Use this method when you need to work with participants and ensure they are initialized.
    @Transactional
    public Room getRoomWithParticipants(String roomId) {
        Room room = roomRepository.findByIdWithParticipants(roomId).orElse(null);
        if (room != null) {
            // Force initialization of participants
            room.getParticipants().size();
        }
        return room;
    }

    // Update room details and update the cache.
    @CachePut(value = "rooms", key = "#room.roomId")
    public Room updateRoom(Room room) {
        // Reload the existing Room from the database
        Room existingRoom = roomRepository.findById(room.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException("Room not found: " + room.getRoomId()));

        // Update only the fields that need to change
        existingRoom.setUpdatedAt(LocalDateTime.now());
        // (Update other fields if needed, but be careful with associations)

        return roomRepository.save(existingRoom);
    }

    // Delete room from DB and evict it from cache.
    @CacheEvict(value = "rooms", key = "#roomId")
    public void deleteRoom(String roomId) {
        roomRepository.deleteById(roomId);
    }

    /**
     * Helper method to publish and broadcast an event.
     * This method publishes the event payload to RabbitMQ and broadcasts a RoomEvent
     * via Spring's ApplicationEventPublisher.
     */
    public void publishEvent(EventType eventType, String roomId, String userId) {
        RoomEventPayload payload = new RoomEventPayload(eventType, roomId, userId);
        try {
            amqpTemplate.convertAndSend(RabbitConfig.ROOM_EXCHANGE, RabbitConfig.ROUTING_KEY, payload);
            logger.info("Published event to RabbitMQ: {}", payload);
        } catch (Exception ex) {
            logger.error("Failed to publish event to RabbitMQ", ex);
        }
        // Broadcast the event via Spring's ApplicationEventPublisher
        RoomEvent roomEvent = new RoomEvent(this, eventType, roomId, userId);
        applicationEventPublisher.publishEvent(roomEvent);
        logger.info("Broadcasted event: {}", roomEvent);
    }

}
