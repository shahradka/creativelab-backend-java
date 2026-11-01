package top.creativelab.user.adapters.inbound.rest.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

record ChangePasswordRequest(@Valid @NotBlank String newPassword, @Valid String oldPassword) {
}
