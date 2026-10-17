package top.creativelab.user.adapters.inbound.rest.user;

import jakarta.validation.constraints.NotNull;

public record ChangePasswordRequest(@NotNull String password) {
}
