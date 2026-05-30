package net.unicorn.fitnesssystem.helper;

import net.unicorn.fitnesssystem.api.model.MessageResponseDto;

public class MessageBuilder {

    public static MessageResponseDto success(String message) {
        var response = new MessageResponseDto();
        response.setMessage(message);
        response.setLevel(MessageResponseDto.LevelEnum.INFO);
        return response;
    }

    public static MessageResponseDto error(String message) {
        var response = new MessageResponseDto();
        response.setMessage(message);
        response.setLevel(MessageResponseDto.LevelEnum.ERROR);
        return response;
    }

    public static MessageResponseDto warning(String message) {
        var response = new MessageResponseDto();
        response.setMessage(message);
        response.setLevel(MessageResponseDto.LevelEnum.WARNING);
        return response;
    }
}
