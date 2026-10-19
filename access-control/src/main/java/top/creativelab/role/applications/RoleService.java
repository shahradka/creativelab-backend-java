package top.creativelab.role.applications;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.creativelab.role.applications.exceptions.PermissionNotFoundException;
import top.creativelab.role.applications.exceptions.RoleNotFoundException;
import top.creativelab.role.domain.Role;
import top.creativelab.role.domain.vo.PermissionId;
import top.creativelab.role.domain.vo.RoleId;
import top.creativelab.role.ports.inbound.RoleUseCase;
import top.creativelab.role.ports.outbound.PermissionRepository;
import top.creativelab.role.ports.outbound.RoleRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService implements RoleUseCase {

    private final PermissionRepository permissionRepository;
    private final RoleRepository repository;

    public Role create(String name, Set<PermissionId> permissionIds){
        Role newRole = new Role(RoleId.newId(), name, permissionIds);

        repository.save(newRole);
        return newRole;
    }

    @Override
    @Transactional
    public Role update(RoleId roleId, String name, Set<PermissionId> permissionIds) {
        Role modifiedRole = repository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException(roleId.getValue()));

        Set<PermissionId> validPermissionIds = permissionIds.stream()
                .filter(permissionRepository::isExistById)
                .collect(Collectors.toSet());

        modifiedRole.updatePermissions(validPermissionIds);
        modifiedRole.updateName(name);

        return repository.update(modifiedRole);
    }

    @Override
    public void Delete(RoleId roleId) {
        if(!repository.isExistById(roleId)){
            throw new RoleNotFoundException(roleId.getValue());
        }

        repository.delete(roleId);
    }

    @Override
    public List<Role> findByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public Role findById(RoleId roleId) {
        return repository.findById(roleId).orElseThrow(()-> new RoleNotFoundException(roleId.getValue()));
    }

    @Override
    public boolean isRoleExistById(RoleId id) {
        return repository.isExistById(id);
    }
}
