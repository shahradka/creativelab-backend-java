package top.creativelab.role.adapters.inbound.rest.action;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.creativelab.role.domain.Action;
import top.creativelab.role.domain.vo.ActionId;
import top.creativelab.role.ports.inbound.ActionUseCase;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("acc-ctl-action")
@RequiredArgsConstructor
public class ActionController {
    private final ActionUseCase actionUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ActionResponse createAction(@RequestBody @Valid CreateActionRequest createActionRequest) {
        return ActionResponse.from(actionUseCase.create(createActionRequest.name()));
    }

    @PutMapping("/{id}")
    public ActionResponse updateAction(@PathVariable UUID id,@RequestBody @Valid UpdateActionRequest updateActionRequest ){
        return ActionResponse.from(actionUseCase.update(id, updateActionRequest.actionName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAction(@PathVariable @Valid UUID id){
        actionUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ActionResponse> getAll(){
        return actionUseCase.findAll().stream().map(ActionResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ActionResponse getById(@PathVariable @Valid UUID id){
        return ActionResponse.from(actionUseCase.findById(id));
    }

    @GetMapping("/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public ActionResponse getByName(@PathVariable String name){
        return ActionResponse.from(actionUseCase.findByName(name));
    }
}
