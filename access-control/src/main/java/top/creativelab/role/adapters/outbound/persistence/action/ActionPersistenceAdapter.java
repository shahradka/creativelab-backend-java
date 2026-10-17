package top.creativelab.role.adapters.outbound.persistence.action;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import persistance.BasePersistenceAdapter;
import top.creativelab.role.adapters.outbound.persistence.permission.PermissionJpaRepository;
import top.creativelab.role.domain.Action;
import top.creativelab.role.domain.vo.ActionId;
import top.creativelab.role.ports.outbound.ActionRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ActionPersistenceAdapter extends BasePersistenceAdapter<Action, ActionJpaEntity, UUID> implements ActionRepository {

    public ActionPersistenceAdapter(ActionJpaRepository repository) {
        super(repository);
    }

    protected ActionJpaEntity mapToJpaEntity(Action action) {
        return new ActionJpaEntity(
                action.getId().getValue(),
                action.getName()
        );
    }

    protected Action mapToDomain(ActionJpaEntity actionJpaEntity){
        return new Action(new ActionId(actionJpaEntity.getId()), actionJpaEntity.getName());
    }

    @Override
    public Optional<Action> findById(ActionId id) {
        return super.findById(id.getValue());
    }

    @Override
    public Optional<Action> findByName(String name) {
        return ((ActionJpaRepository) repository).findByName(name).map(this::mapToDomain);
    }

    @Override
    public void deleteById(ActionId id) {
        super.deleteById(id.getValue());
    }

    @Override
    public boolean isExistById(ActionId id) {
        return super.isExistById(id.getValue());
    }

    @Override
    public Integer count() {
        return repository.findAll().size();
    }
}
