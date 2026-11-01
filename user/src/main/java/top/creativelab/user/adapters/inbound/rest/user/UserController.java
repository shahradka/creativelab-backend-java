package top.creativelab.user.adapters.inbound.rest.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import top.creativelab.user.domain.user.User;
import top.creativelab.user.domain.user.UserStatus;
import top.creativelab.user.domain.user.vo.Email;
import top.creativelab.common.ports.PageResult;
import top.creativelab.common.rest.PageResponse;
import top.creativelab.user.ports.UserFilter;
import top.creativelab.user.ports.inbound.user.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserManagementUseCase userManagementUseCase;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid CreateUserRequest request) {
        User user = userManagementUseCase.create(request.username(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.fromDomain(user));
    }

    @PutMapping("/update/{username}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable String username,
                                                   @RequestBody @Valid UpdateUserRequest request) {
        User updatedUser = userManagementUseCase.update(username, request.toCommand());
        return ResponseEntity.ok(UserResponse.fromDomain(updatedUser));
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeUserPassword(@RequestHeader(name = "X-User-Id", required = true) String userName,
                                 @RequestBody @Valid ChangePasswordRequest request) {
        userManagementUseCase.changePassword(userName, request.oldPassword(), request.newPassword());
    }

    @PutMapping("/activate/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activateUser(@PathVariable String username) {
        userManagementUseCase.activateUser(username);
    }

    @PutMapping("/suspend/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspendUser(@PathVariable String username) {
        userManagementUseCase.suspend(username);
    }

    @PutMapping("/reactivate/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reActiveUser(@PathVariable String username) {
        userManagementUseCase.reActive(username);
    }

    @PutMapping("/delete/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String username) {
        userManagementUseCase.delete(username);
    }

    @GetMapping("/active")
    public ResponseEntity<List<UserResponse>> findActiveUsers() {
        List<User> users = userManagementUseCase.findActiveUsers();
        List<UserResponse> responses = users.stream()
                .map(UserResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<UserResponse>> findPendingUsers() {
        List<User> users = userManagementUseCase.findPendingUsers();
        List<UserResponse> responses = users.stream()
                .map(UserResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/suspended")
    public ResponseEntity<List<UserResponse>> findSuspendedUsers() {
        List<User> users = userManagementUseCase.findSuspendedUsers();
        List<UserResponse> responses = users.stream()
                .map(UserResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<UserResponse>> findDeletedUsers() {
        List<User> users = userManagementUseCase.findDeletedUsers();
        List<UserResponse> responses = users.stream()
                .map(UserResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> findAllUsers() {
        List<User> users = userManagementUseCase.findAllUsers();
        List<UserResponse> responses = users.stream()
                .map(UserResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponse<UserResponse>> findUsersPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String firstname,
            @RequestParam(required = false) String lastname,
            @RequestParam(required = false) UserStatus status) {
        UserFilter filter = new UserFilter(username, email, firstname, lastname, status);
        PageResult<User> result = userManagementUseCase.findUsers(page, size, filter);
        return ResponseEntity.ok(PageResponse.from(result, UserResponse::fromDomain));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countTotalUsers() {
        Long count = userManagementUseCase.countTotalUsers();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/active")
    public ResponseEntity<Long> countActiveUsers() {
        Long count = userManagementUseCase.countActiveUsers();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/pending")
    public ResponseEntity<Long> countPendingUsers() {
        Long count = userManagementUseCase.countPendingUsers();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/suspended")
    public ResponseEntity<Long> countSuspendedUsers() {
        Long count = userManagementUseCase.countSuspendedUsers();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/deleted")
    public ResponseEntity<Long> countDeletedUsers() {
        Long count = userManagementUseCase.countDeletedUsers();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/search/username")
    public ResponseEntity<List<UserResponse>> findByUsernamePattern(@RequestParam String pattern) {
        List<User> users = userManagementUseCase.findByUsernamePattern(pattern);
        List<UserResponse> responses = users.stream()
                .map(UserResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search/email")
    public ResponseEntity<List<UserResponse>> findByEmailPattern(@RequestParam String pattern) {
        List<User> users = userManagementUseCase.findByEmailPattern(pattern);
        List<UserResponse> responses = users.stream()
                .map(UserResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search/firstname")
    public ResponseEntity<List<UserResponse>> findByFirstnamePattern(@RequestParam String pattern) {
        List<User> users = userManagementUseCase.findByFirstnamePattern(pattern);
        List<UserResponse> responses = users.stream()
                .map(UserResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search/lastname")
    public ResponseEntity<List<UserResponse>> findByLastnamePattern(@RequestParam String pattern) {
        List<User> users = userManagementUseCase.findByLastnamePattern(pattern);
        List<UserResponse> responses = users.stream()
                .map(UserResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/validate/password")
    public ResponseEntity<Void> validatePassword(@RequestParam String password) {
        try {
            userManagementUseCase.validatePassword(password);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/validate/username")
    public ResponseEntity<Void> validateUsername(@RequestParam String username) {
        try {
            userManagementUseCase.validateUsername(username);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/validate/email")
    public ResponseEntity<Void> validateEmail(@RequestParam String email) {
        try {
            userManagementUseCase.validateEmail(new Email(email));
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
