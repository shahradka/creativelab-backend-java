package top.creativelab.user.adapters.inbound.rest.user;

import jakarta.validation.constraints.NotBlank;
import top.creativelab.user.domain.user.vo.Password;

public record CreateUserRequest (@NotBlank String username, @NotBlank String password){
}


