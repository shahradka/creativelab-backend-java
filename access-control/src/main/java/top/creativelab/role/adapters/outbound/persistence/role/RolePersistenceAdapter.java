package top.creativelab.role.adapters.outbound.persistence.role;

import org.springframework.stereotype.Repository;
import persistance.BasePersistenceAdapter;
import top.creativelab.role.domain.Role;
import top.creativelab.role.domain.vo.PermissionId;
import top.creativelab.role.domain.vo.RoleId;
import top.creativelab.role.ports.outbound.RoleRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class RolePersistenceAdapter extends BasePersistenceAdapter<Role, RoleJpaEntity, UUID> implements RoleRepository {

    public RolePersistenceAdapter(RoleJpaRepository roleJpaRepository){
        super(roleJpaRepository);
    }

    @Override
    protected RoleJpaEntity mapToJpaEntity(Role domain) {
        return new RoleJpaEntity(domain.getId().getValue(), domain.getName(), domain.getPermissionSet().stream().map(PermissionId::getValue).collect(Collectors.toSet()));
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
}
