package top.creativelab.role.applications;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.creativelab.role.applications.exceptions.ResourceNotFoundException;
import top.creativelab.role.domain.Resource;
import top.creativelab.role.domain.vo.ResourceId;
import top.creativelab.role.ports.inbound.ResourceUseCase;
import top.creativelab.role.ports.outbound.ResourceRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResourcesService implements ResourceUseCase {

    private final ResourceRepository repository;

    @Override
    public Resource create(String name) {
        return repository.save(new Resource(ResourceId.newId(), name));
    }

    @Override
    public List<Resource> findAll() {
        return repository.findAll();
    }

    @Override
    public boolean isExistsById(ResourceId resourceId) {
        return repository.isExistsById(resourceId);
    }

    @Override
    public Resource findById(UUID id) {
        ResourceId resourceId = new ResourceId(id);
        return repository.findById(resourceId).orElseThrow(() -> new ResourceNotFoundException(resourceId.getValue()));
    }

    public Resource findByName(String name){
        return repository.findByName(name).orElseThrow(()-> new ResourceNotFoundException(name));
    }

    @Override
    public Resource update(UUID id, String name) {
        ResourceId resourceId = new ResourceId(id);
        if(!isExistsById(resourceId)){
            throw new ResourceNotFoundException(resourceId.getValue());
        }
        else if(name.isBlank()){
            throw new IllegalArgumentException("Resource name can't be empty");
        }

        Resource modifiedResource = repository.findById(resourceId).orElseThrow(() -> new ResourceNotFoundException(resourceId.getValue()));
        modifiedResource.updateName(name);

        return repository.save(modifiedResource);
    }

    public void delete(UUID id){
        ResourceId resourceId = new ResourceId(id);
        repository.delete(resourceId);
    }


}
