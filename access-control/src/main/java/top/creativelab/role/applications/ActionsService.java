package top.creativelab.role.applications;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.creativelab.role.applications.exceptions.ActionNotFoundException;
import top.creativelab.role.domain.Action;
import top.creativelab.role.domain.vo.ActionId;
import top.creativelab.role.ports.inbound.ActionUseCase;
import top.creativelab.role.ports.outbound.ActionRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActionsService implements ActionUseCase {
    private final ActionRepository repository;
    @Override
    public Action create(String name) {
        return repository.save(new Action(ActionId.newId(), name));
    }

    @Override
    public List<Action> findAll() {
        return repository.findAll();
    }


    @Override
    public Action findById(UUID id) {
        ActionId actionId = new ActionId(id);
        return repository.findById(actionId).orElseThrow(() -> new ActionNotFoundException(id));
    }

    @Override
    public Action update(UUID id, String actionName) {
        if(id == null)
            throw new IllegalArgumentException("Action id is not valid");

        ActionId actionId = new ActionId(id);
        Action modifiedAction = repository.findById(actionId).orElseThrow(() -> new ActionNotFoundException(actionId.getValue()));
        modifiedAction.updateName(actionName);
        return repository.save(modifiedAction);
    }

    @Override
    public void delete(UUID id) {
        ActionId actionId = new ActionId(id);
        repository.deleteById(actionId);
    }

    @Override
    public Action findByName(String name) {
        return repository.findByName(name).orElseThrow(() -> new ActionNotFoundException(name));
    }
}
