package persistance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public abstract class BasePersistenceAdapter <D, E, ID>{
    protected final JpaRepository<E, ID> repository;

    protected BasePersistenceAdapter(JpaRepository<E, ID> repository) {
        this.repository = repository;
    }

    protected abstract E mapToJpaEntity(D domain);
    protected abstract D mapToDomain(E entity);

    public D save(D domain){
        E entity = mapToJpaEntity(domain);
        return mapToDomain(repository.save(entity));
    }

    public List<D> findAll(){
        return repository.findAll().stream().map(this::mapToDomain).toList();
    }

    public Optional<D> findById(ID id){
        return repository.findById(id).map(this::mapToDomain);
    }

    public boolean isExistById(ID id){
        return repository.existsById(id);
    }

    public void deleteById(ID id){
        repository.deleteById(id);
    }

}
