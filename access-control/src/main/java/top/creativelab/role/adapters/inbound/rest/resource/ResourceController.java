package top.creativelab.role.adapters.inbound.rest.resource;

import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import top.creativelab.role.domain.Resource;
import top.creativelab.role.domain.vo.ResourceId;
import top.creativelab.role.ports.inbound.ResourceUseCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("acc-ctl-resource")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceUseCase resourceUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createResource(@RequestBody @Valid CreateUpdateResourceRequest request){
        resourceUseCase.create(request.name());
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateResource(@PathVariable UUID id, @RequestBody @Valid CreateUpdateResourceRequest request){
        resourceUseCase.update(id, request.name());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ResourceResponse> getAll(){
        return resourceUseCase.findAll().stream().map(ResourceResponse::from).toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResourceResponse getById(@PathVariable @Valid UUID id){
        return ResourceResponse.from(resourceUseCase.findById(id));
    }


    @GetMapping("/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public ResourceResponse getByName(@PathVariable @Valid String name){
        return ResourceResponse.from(resourceUseCase.findByName(name));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable @Valid UUID id){
        resourceUseCase.delete(id);
    }

}
