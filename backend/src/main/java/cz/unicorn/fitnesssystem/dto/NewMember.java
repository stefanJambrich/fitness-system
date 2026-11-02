package cz.unicorn.fitnesssystem.dto;

public record NewMember(String nickname, String publicKey, String algorithm, String deviceLabel) {
}