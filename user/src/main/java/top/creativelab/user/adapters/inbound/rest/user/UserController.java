package top.creativelab.user.adapters.inbound.rest.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Users", description = "Create, update, query and manage the lifecycle of users.")
public class UserController {

    private final UserManagementUseCase userManagementUseCase;

    @PostMapping
    @Operation(summary = "Create a user", description = "Registers a new user with the supplied username and password.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content)
    })
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid CreateUserRequest request) {
        User user = userManagementUseCase.create(request.username(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.fromDomain(user));
    }

    @PutMapping("/update/{username}")
    @Operation(summary = "Update a user", description = "Updates the profile information of an existing user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content)
    })
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "Username of the user to update", required = true) @PathVariable String username,
            @RequestBody @Valid UpdateUserRequest request) {
        User updatedUser = userManagementUseCase.update(username, request.toCommand());
        return ResponseEntity.ok(UserResponse.fromDomain(updatedUser));
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Change a user's password",
            description = "Changes the password of the user identified by the X-User-Id header.",
            security = @SecurityRequirement(name = "X-User-Id"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password changed"),
            @ApiResponse(responseCode = "400", description = "Old password is wrong or payload is invalid", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public void changeUserPassword(
            @Parameter(description = "Username of the authenticated user", required = true)
            @RequestHeader(name = "X-User-Id", required = true) String userName,
            @RequestBody @Valid ChangePasswordRequest request) {
        userManagementUseCase.changePassword(userName, request.oldPassword(), request.newPassword());
    }

    @PutMapping("/activate/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Activate a user", description = "Moves a pending user to the ACTIVE status.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User activated"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public void activateUser(
            @Parameter(description = "Username of the user to activate", required = true) @PathVariable String username) {
        userManagementUseCase.activateUser(username);
    }

    @PutMapping("/suspend/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Suspend a user", description = "Moves an active user to the SUSPENDED status.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User suspended"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public void suspendUser(
            @Parameter(description = "Username of the user to suspend", required = true) @PathVariable String username) {
        userManagementUseCase.suspend(username);
    }

    @PutMapping("/reactivate/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Reactivate a user", description = "Moves a suspended user back to the ACTIVE status.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User reactivated"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public void reActiveUser(
            @Parameter(description = "Username of the user to reactivate", required = true) @PathVariable String username) {
        userManagementUseCase.reActive(username);
    }

    @PutMapping("/delete/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a user", description = "Soft-deletes a user by moving it to the DELETED status.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public void deleteUser(
            @Parameter(description = "Username of the user to delete", required = true) @PathVariable String username) {
        userManagementUseCase.delete(username);
    }

    @GetMapping("/active")
    @Operation(summary = "List active users", description = "Returns every user whose status is ACTIVE.")
    @ApiResponse(responseCode = "200", description = "List of active users",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponse.class))))
    public ResponseEntity<List<UserResponse>> findActiveUsers() {
        List<User> users = userManagementUseCase.findActiveUsers();
        List<UserResponse> responses = users.stream()
                .map(UserResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/pending")
    @Operation(summary = "List pending users", description = "Returns every user whose status is PENDING.")
    @ApiResponse(responseCode = "200", description = "List of pending users",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponse.class))))
    public ResponseEntity<List<UserResponse>> findPendingUsers() {
        List<User> users = userManagementUseCase.findPendingUsers();
        List<UserResponse> responses = users.stream()
                .map(UserResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/suspended")
    @Operation(summary = "List suspended users", description = "Returns every user whose status is SUSPENDED.")
    @ApiResponse(responseCode = "200", description = "List of suspended users",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponse.class))))
    public ResponseEntity<List<UserResponse>> findSuspendedUsers() {
        List<User> users = userManagementUseCase.findSuspendedUsers();
        List<UserResponse> responses = users.stream()
                .map(UserResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/deleted")
    @Operation(summary = "List deleted users", description = "Returns every user whose status is DELETED.")
    @ApiResponse(responseCode = "200", description = "List of deleted users",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponse.class))))
    public ResponseEntity<List<UserResponse>> findDeletedUsers() {
        List<User> users = userManagementUseCase.findDeletedUsers();
        List<UserResponse> responses = users.stream()
                .map(UserResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/all")
    @Operation(summary = "List all users", description = "Returns every user regardless of status.")
    @ApiResponse(responseCode = "200", description = "List of all users",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponse.class))))
    public ResponseEntity<List<UserResponse>> findAllUsers() {
        List<User> users = userManagementUseCase.findAllUsers();
        List<UserResponse> responses = users.stream()
                .map(UserResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/page")
    @Operation(summary = "Page users", description = "Returns a filtered, paginated list of users.")
    @ApiResponse(responseCode = "200", description = "A page of users",
            content = @Content(schema = @Schema(implementation = PageResponse.class)))
    public ResponseEntity<PageResponse<UserResponse>> findUsersPage(
            @Parameter(description = "Zero-based page index") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of elements per page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Filter by username") @RequestParam(required = false) String username,
            @Parameter(description = "Filter by email") @RequestParam(required = false) String email,
            @Parameter(description = "Filter by first name") @RequestParam(required = false) String firstname,
            @Parameter(description = "Filter by last name") @RequestParam(required = false) String lastname,
            @Parameter(description = "Filter by user status") @RequestParam(required = false) UserStatus status) {
        UserFilter filter = new UserFilter(username, email, firstname, lastname, status);
        PageResult<User> result = userManagementUseCase.findUsers(page, size, filter);
        return ResponseEntity.ok(PageResponse.from(result, UserResponse::fromDomain));
    }

    @GetMapping("/count")
    @Operation(summary = "Count all users", description = "Returns the total number of users.")
    @ApiResponse(responseCode = "200", description = "Total user count",
            content = @Content(schema = @Schema(implementation = Long.class)))
    public ResponseEntity<Long> countTotalUsers() {
        Long count = userManagementUseCase.countTotalUsers();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/active")
    @Operation(summary = "Count active users", description = "Returns the number of ACTIVE users.")
    @ApiResponse(responseCode = "200", description = "Active user count",
            content = @Content(schema = @Schema(implementation = Long.class)))
    public ResponseEntity<Long> countActiveUsers() {
        Long count = userManagementUseCase.countActiveUsers();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/pending")
    @Operation(summary = "Count pending users", description = "Returns the number of PENDING users.")
    @ApiResponse(responseCode = "200", description = "Pending user count",
            content = @Content(schema = @Schema(implementation = Long.class)))
    public ResponseEntity<Long> countPendingUsers() {
        Long count = userManagementUseCase.countPendingUsers();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/suspended")
    @Operation(summary = "Count suspended users", description = "Returns the number of SUSPENDED users.")
    @ApiResponse(responseCode = "200", description = "Suspended user count",
            content = @Content(schema = @Schema(implementation = Long.class)))
    public ResponseEntity<Long> countSuspendedUsers() {
        Long count = userManagementUseCase.countSuspendedUsers();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/deleted")
    @Operation(summary = "Count deleted users", description = "Returns the number of DELETED users.")
    @ApiResponse(responseCode = "200", description = "Deleted user count",
            content = @Content(schema = @Schema(implementation = Long.class)))
    public ResponseEntity<Long> countDeletedUsers() {
        Long count = userManagementUseCase.countDeletedUsers();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/search/username")
    @Operation(summary = "Search users by username", description = "Returns users whose username matches the given pattern.")
    @ApiResponse(responseCode = "200", description = "Matching users",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponse.class))))
    public ResponseEntity<List<UserResponse>> findByUsernamePattern(
            @Parameter(description = "Username pattern to match", required = true) @RequestParam String pattern) {
        List<User> users = userManagementUseCase.findByUsernamePattern(pattern);
        List<UserResponse> responses = users.stream()
                .map(UserResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search/email")
    @Operation(summary = "Search users by email", description = "Returns users whose email matches the given pattern.")
    @ApiResponse(responseCode = "200", description = "Matching users",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponse.class))))
    public ResponseEntity<List<UserResponse>> findByEmailPattern(
            @Parameter(description = "Email pattern to match", required = true) @RequestParam String pattern) {
        List<User> users = userManagementUseCase.findByEmailPattern(pattern);
        List<UserResponse> responses = users.stream()
                .map(UserResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search/firstname")
    @Operation(summary = "Search users by first name", description = "Returns users whose first name matches the given pattern.")
    @ApiResponse(responseCode = "200", description = "Matching users",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponse.class))))
    public ResponseEntity<List<UserResponse>> findByFirstnamePattern(
            @Parameter(description = "First name pattern to match", required = true) @RequestParam String pattern) {
        List<User> users = userManagementUseCase.findByFirstnamePattern(pattern);
        List<UserResponse> responses = users.stream()
                .map(UserResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search/lastname")
    @Operation(summary = "Search users by last name", description = "Returns users whose last name matches the given pattern.")
    @ApiResponse(responseCode = "200", description = "Matching users",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponse.class))))
    public ResponseEntity<List<UserResponse>> findByLastnamePattern(
            @Parameter(description = "Last name pattern to match", required = true) @RequestParam String pattern) {
        List<User> users = userManagementUseCase.findByLastnamePattern(pattern);
        List<UserResponse> responses = users.stream()
                .map(UserResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/validate/password")
    @Operation(summary = "Validate a password", description = "Checks whether the supplied password satisfies the password policy.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password is valid"),
            @ApiResponse(responseCode = "400", description = "Password is invalid", content = @Content)
    })
    public ResponseEntity<Void> validatePassword(
            @Parameter(description = "Password to validate", required = true) @RequestParam String password) {
        try {
            userManagementUseCase.validatePassword(password);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/validate/username")
    @Operation(summary = "Validate a username", description = "Checks whether the supplied username satisfies the username policy.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Username is valid"),
            @ApiResponse(responseCode = "400", description = "Username is invalid", content = @Content)
    })
    public ResponseEntity<Void> validateUsername(
            @Parameter(description = "Username to validate", required = true) @RequestParam String username) {
        try {
            userManagementUseCase.validateUsername(username);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/validate/email")
    @Operation(summary = "Validate an email", description = "Checks whether the supplied email address is well formed.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email is valid"),
            @ApiResponse(responseCode = "400", description = "Email is invalid", content = @Content)
    })
    public ResponseEntity<Void> validateEmail(
            @Parameter(description = "Email address to validate", required = true) @RequestParam String email) {
        try {
            userManagementUseCase.validateEmail(new Email(email));
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
