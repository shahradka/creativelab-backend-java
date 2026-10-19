package top.creativelab.role.adapters.outbound.persistence.role;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import persistance.BasePersistenceAdapter;
import top.creativelab.role.adapters.outbound.persistence.permission.PermissionJpaEntity;
import top.creativelab.role.adapters.outbound.persistence.rolePermission.RolePermissionJpaEntity;
import top.creativelab.role.applications.exceptions.RoleNotFoundException;
import top.creativelab.role.domain.Role;
import top.creativelab.role.domain.vo.PermissionId;
import top.creativelab.role.domain.vo.RoleId;
import top.creativelab.role.ports.outbound.PermissionRepository;
import top.creativelab.role.ports.outbound.RoleRepository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class RolePersistenceAdapter extends BasePersistenceAdapter<Role, RoleJpaEntity, UUID> implements RoleRepository {

    public RolePersistenceAdapter(RoleJpaRepository roleJpaRepository){
        super(roleJpaRepository);
    }

    @Override
    protected RoleJpaEntity mapToJpaEntity(Role domain) {

        Set<UUID> newPermissionIds = domain.getPermissionSet() != null ?domain.getPermissionSet().stream().map(PermissionId::getValue).collect(Collectors.toSet()) : Collections.emptySet();
        return new RoleJpaEntity(domain.getId().getValue(), domain.getName(), newPermissionIds);

    }

    @Override
    protected Role mapToDomain(RoleJpaEntity entity) {
        return new Role(new RoleId(entity.getId()), entity.getName(), entity.getPermissionIds().stream().map(PermissionId::new).collect(Collectors.toSet()));
    }

    @Override
    public Optional<Role> findById(RoleId roleId) {
        return repository.findById(roleId.getValue()).map(this::mapToDomain);
    }

    public List<Role> findByName(String name){
        return ((RoleJpaRepository) repository).findByNameContainingIgnoreCase(name).stream().map(this::mapToDomain).toList();
    }

    @Override
    public boolean isExistById(RoleId roleId) {
        return repository.existsById(roleId.getValue());
    }

    @Override
    public void delete(RoleId roleId) {
        repository.deleteById(roleId.getValue());
    }

    @Transactional
    public Role update(Role role){

        RoleJpaEntity modifiedJpaRole = repository.findById(role.getId().getValue()).orElseThrow(() -> new RoleNotFoundException(role.getId().getValue()));
        modifiedJpaRole.updateName(role.getName());
        modifiedJpaRole.syncPermissions(role.getPermissionSet().stream().map(PermissionId::getValue).collect(Collectors.toSet()));

        repository.save(modifiedJpaRole);

        return mapToDomain(modifiedJpaRole);
    }


}
