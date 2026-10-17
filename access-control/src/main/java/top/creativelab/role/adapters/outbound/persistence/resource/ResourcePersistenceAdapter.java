package top.creativelab.role.adapters.outbound.persistence.resource;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import persistance.BasePersistenceAdapter;
import top.creativelab.role.adapters.outbound.persistence.action.ActionJpaRepository;
import top.creativelab.role.domain.Resource;
import top.creativelab.role.domain.vo.ResourceId;
import top.creativelab.role.ports.outbound.ResourceRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ResourcePersistenceAdapter extends BasePersistenceAdapter<Resource, ResourceJpaEntity, UUID> implements ResourceRepository {

    public ResourcePersistenceAdapter(ResourceJpaRepository repository) {
        super(repository);
    }

    // =====================
    // Mapping
    // =====================

    protected ResourceJpaEntity mapToJpaEntity(Resource resource) {
        return new ResourceJpaEntity(
                resource.getId().getValue(),
                resource.getName()
        );
    }

    protected Resource mapToDomain(ResourceJpaEntity entity) {
        return new Resource(
                new ResourceId(entity.getId()),
                entity.getName()
        );
    }

    @Override
    public Optional<Resource> findById(ResourceId id) {
        return super.findById(id.getValue());
    }

    @Override
    public boolean isExistsById(ResourceId id) {
        return super.isExistById(id.getValue());
    }

    @Override
    public void delete(ResourceId id) {
        repository.deleteById(id.getValue());
    }

    public Optional<Resource> findByName(String name) {
        return ((ResourceJpaRepository) repository).findByName(name).map(this::mapToDomain);
    }
}
