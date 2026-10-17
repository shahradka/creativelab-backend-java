package top.creativelab.role.adapters.inbound.rest.action;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UpdateActionRequest(@NotBlank String actionName) {
}
