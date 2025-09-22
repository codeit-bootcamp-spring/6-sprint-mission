package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;


    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public Message createMessage(@RequestBody CreateMessageRequest request) {
        return messageService.create(request);
    }

    @GetMapping("/{authorId}")
    public Message findMessageByAuthorId(@PathVariable UUID authorId) {
        return messageService.find(authorId);
    }

    @GetMapping("/channel/{channelId}")
    public List<Message> findAllByChannelId(@PathVariable UUID channelId) {
        return messageService.findAllByChannelId(channelId);
    }

    @GetMapping
    public List<Message> findAllMessages() {
        return messageService.findAll();
    }

    @PutMapping("/{messageId}")
    public Message updateMessage(@PathVariable UUID messageId, @RequestBody UpdateMessageRequest request) {
        return messageService.update(request);
    }

    @DeleteMapping("/{messageId}")
    public void deleteMessage(@PathVariable UUID messageId) {
        messageService.delete(messageId);
    }
}