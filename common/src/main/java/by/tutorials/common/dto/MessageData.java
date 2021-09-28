package by.tutorials.common.dto;

import by.tutorials.common.enums.MessageType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record MessageData(@NotBlank String content,
                          @NotNull MessageType type) {
}
