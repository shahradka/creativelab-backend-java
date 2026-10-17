package top.creativelab.role.ports.inbound;

import top.creativelab.role.domain.Resource;
import top.creativelab.role.domain.vo.ResourceId;

import java.util.List;
import java.util.UUID;

public interface ResourceUseCase {
    Resource create(String name);
    List<Resource> findAll();
    boolean isExistsById(ResourceId resourceId);
    Resource findById(UUID id);
    Resource findByName(String name);
    Resource update(UUID id, String name);
    void delete(UUID id);
}
