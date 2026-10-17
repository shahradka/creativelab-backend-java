package top.creativelab.role.adapters.inbound.rest.role;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import top.creativelab.role.domain.vo.PermissionId;
import top.creativelab.role.domain.vo.RoleId;
import top.creativelab.role.ports.inbound.RoleUseCase;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("acc-ctl-role")
public class RoleController {

    private final RoleUseCase roleUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoleResponse create(@RequestBody @Valid CreateUpdateRoleRequest roleRequest){
        Set<PermissionId> permissionIds = roleRequest.permissionIdSet() !=null? roleRequest.permissionIdSet().stream().map(PermissionId::new).collect(Collectors.toSet()) :
                Collections.emptySet();
        return RoleResponse.from(roleUseCase.create(roleRequest.name(), permissionIds));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RoleResponse update(@PathVariable UUID id, @RequestBody @Valid CreateUpdateRoleRequest createUpdateRoleRequest){
        RoleId roleId = new RoleId(id);
        Set<PermissionId> permissionIdSet = createUpdateRoleRequest.permissionIdSet() != null? createUpdateRoleRequest.permissionIdSet().stream().map(PermissionId::new).collect(Collectors.toSet()) :
                Collections.emptySet();
        return RoleResponse.from(roleUseCase.update(roleId, createUpdateRoleRequest.name(), permissionIdSet));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RoleResponse getRoleById(@PathVariable @Valid UUID roleId){
        return RoleResponse.from(roleUseCase.findById(new RoleId(roleId)));
    }

    @GetMapping("/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public List<RoleResponse> getRoleByName(@PathVariable @Valid String name){
        return roleUseCase.findByName(name).stream().map(RoleResponse::from).toList();
    }
}
