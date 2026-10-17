package top.creativelab.role.ports.outbound;

import top.creativelab.role.domain.Action;
import top.creativelab.role.domain.vo.ActionId;

import java.util.List;
import java.util.Optional;

public interface ActionRepository {
    Action save(Action action);
    List<Action> findAll();
    Optional<Action> findById(ActionId id);
    Optional<Action> findByName(String name);
    void deleteById(ActionId id);
    boolean isExistById(ActionId id);
    Integer count();
}
