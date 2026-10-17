package top.creativelab.role.ports.outbound;

import top.creativelab.role.domain.Resource;
import top.creativelab.role.domain.vo.ResourceId;

import java.util.List;
import java.util.Optional;

public interface ResourceRepository {
    Resource save(Resource resource);
    List<Resource> findAll();
    Optional<Resource> findById(ResourceId id);
    Optional<Resource> findByName(String name);
    boolean isExistsById(ResourceId id);
    void delete(ResourceId id);
}
