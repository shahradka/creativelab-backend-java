package top.creativelab.user.adapters.inbound.rest.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import top.creativelab.user.ports.inbound.user.ChangePasswordUseCase;
import top.creativelab.user.ports.inbound.user.CreateUserUseCase;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@RequestBody @Valid CreateUserRequest request){
        createUserUseCase.create(request.username(), request.password());
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeUserPassword(@RequestHeader("X-User-Id") String userId, @RequestBody @Valid ChangePasswordRequest request){
        changePasswordUseCase.changePassword(userId, request.password());
    }
}
