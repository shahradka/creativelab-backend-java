package top.creativelab.role.ports.inbound;

import top.creativelab.role.domain.Action;
import top.creativelab.role.domain.vo.ActionId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActionUseCase {
    Action create(String name);
    List<Action> findAll();
    Action findById(UUID id);
    Action update(UUID id, String actionName);
    void delete(UUID id);
    Action findByName(String name);
}
