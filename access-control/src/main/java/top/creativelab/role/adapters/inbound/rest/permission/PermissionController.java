package top.creativelab.role.adapters.inbound.rest.permission;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import top.creativelab.role.domain.Permission;
import top.creativelab.role.ports.inbound.PermissionUseCase;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("acc-ctl-permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionUseCase permissionUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PermissionResponse createPermission(@RequestBody @Valid CreatePermissionRequest createPermissionRequest) {
        return PermissionResponse.from(permissionUseCase.create(createPermissionRequest.name(), createPermissionRequest.resourceId(), createPermissionRequest.actionId()));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PermissionResponse> getAll() {
        return permissionUseCase.findAll().stream().map(PermissionResponse::from).toList();
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PermissionResponse getById(@PathVariable @Valid UUID id) {
        return PermissionResponse.from(permissionUseCase.findById(id));
    }

    @GetMapping("/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public List<PermissionResponse> getById(@PathVariable @Valid String name) {
        return permissionUseCase.findByName(name).stream().map(PermissionResponse::from).toList();
    }


}
