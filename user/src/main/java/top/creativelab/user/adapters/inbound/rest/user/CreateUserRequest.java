package top.creativelab.user.adapters.inbound.rest.user;

import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest (@NotBlank String username, @NotBlank String password){
}


