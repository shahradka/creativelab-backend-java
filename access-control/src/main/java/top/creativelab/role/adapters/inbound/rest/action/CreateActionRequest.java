package top.creativelab.role.adapters.inbound.rest.action;

import jakarta.validation.constraints.NotBlank;

public record CreateActionRequest(@NotBlank String name) {
}
