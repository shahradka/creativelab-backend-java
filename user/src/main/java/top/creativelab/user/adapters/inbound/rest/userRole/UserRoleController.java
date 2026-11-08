package top.creativelab.user.adapters.inbound.rest.userRole;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import top.creativelab.user.domain.userRole.UserRole;
import top.creativelab.user.ports.inbound.userRole.UserRoleUseCase;

import java.util.List;
import java.util.UUID;

/**
 * REST adapter exposing the {@code user_roles} join table.
 *
 * <p>Assignments are addressed from the user side ({@code /user-roles/user/{userId}}) and can be
 * looked up from the role side ({@code /user-roles/role/{roleId}}).</p>
 */
@RestController
@RequestMapping("/user-roles")
@RequiredArgsConstructor
@Tag(name = "User Roles", description = "Assign, replace, query and remove roles attached to users.")
public class UserRoleController {

    private final UserRoleUseCase userRoleUseCase;

    @PostMapping("/user/{userId}")
    @Operation(summary = "Assign a role to a user", description = "Creates a single user-role assignment.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Role assigned",
                    content = @Content(schema = @Schema(implementation = UserRoleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @ApiResponse(responseCode = "404", description = "User or role not found", content = @Content)
    })
    public ResponseEntity<UserRoleResponse> assignRole(
            @Parameter(description = "Identifier of the user", required = true) @PathVariable UUID userId,
            @RequestBody @Valid AssignRoleRequest request) {
        UserRole userRole = userRoleUseCase.assignRole(userId, request.roleId());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserRoleResponse.fromDomain(userRole));
    }

    @PutMapping("/user/{userId}")
    @Operation(summary = "Replace all roles of a user",
            description = "Replaces the complete set of roles assigned to the user with the supplied set.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Roles replaced",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserRoleResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @ApiResponse(responseCode = "404", description = "User or role not found", content = @Content)
    })
    public ResponseEntity<List<UserRoleResponse>> replaceRoles(
            @Parameter(description = "Identifier of the user", required = true) @PathVariable UUID userId,
            @RequestBody @Valid ReplaceUserRolesRequest request) {
        List<UserRoleResponse> responses = userRoleUseCase.replaceRoles(userId, request.roleIds())
                .stream()
                .map(UserRoleResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "List roles of a user", description = "Returns every role assignment belonging to the user.")
    @ApiResponse(responseCode = "200", description = "Role assignments of the user",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserRoleResponse.class))))
    public ResponseEntity<List<UserRoleResponse>> findByUserId(
            @Parameter(description = "Identifier of the user", required = true) @PathVariable UUID userId) {
        List<UserRoleResponse> responses = userRoleUseCase.findByUserId(userId)
                .stream()
                .map(UserRoleResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}/count")
    @Operation(summary = "Count roles of a user", description = "Returns the number of roles assigned to the user.")
    @ApiResponse(responseCode = "200", description = "Number of assigned roles",
            content = @Content(schema = @Schema(implementation = Long.class)))
    public ResponseEntity<Long> countRoles(
            @Parameter(description = "Identifier of the user", required = true) @PathVariable UUID userId) {
        return ResponseEntity.ok(userRoleUseCase.countRoles(userId));
    }

    @GetMapping("/user/{userId}/{roleId}/exists")
    @Operation(summary = "Check a role assignment", description = "Indicates whether the user currently has the given role.")
    @ApiResponse(responseCode = "200", description = "Whether the assignment exists",
            content = @Content(schema = @Schema(implementation = Boolean.class)))
    public ResponseEntity<Boolean> hasRole(
            @Parameter(description = "Identifier of the user", required = true) @PathVariable UUID userId,
            @Parameter(description = "Identifier of the role", required = true) @PathVariable UUID roleId) {
        return ResponseEntity.ok(userRoleUseCase.hasRole(userId, roleId));
    }

    @DeleteMapping("/user/{userId}/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Unassign a role from a user", description = "Removes the given role assignment from the user.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Role unassigned"),
            @ApiResponse(responseCode = "404", description = "Assignment not found", content = @Content)
    })
    public void unassignRole(
            @Parameter(description = "Identifier of the user", required = true) @PathVariable UUID userId,
            @Parameter(description = "Identifier of the role", required = true) @PathVariable UUID roleId) {
        userRoleUseCase.unassignRole(userId, roleId);
    }

    @GetMapping("/role/{roleId}")
    @Operation(summary = "List users holding a role", description = "Returns every user-role assignment for the given role.")
    @ApiResponse(responseCode = "200", description = "Assignments for the role",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserRoleResponse.class))))
    public ResponseEntity<List<UserRoleResponse>> findByRoleId(
            @Parameter(description = "Identifier of the role", required = true) @PathVariable UUID roleId) {
        List<UserRoleResponse> responses = userRoleUseCase.findByRoleId(roleId)
                .stream()
                .map(UserRoleResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
