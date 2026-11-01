package top.creativelab.user.adapters.outbound.persistence.user;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import persistance.BasePersistenceAdapter;
import top.creativelab.user.domain.user.User;
import top.creativelab.user.domain.user.UserStatus;
import top.creativelab.user.domain.user.vo.BirthDate;
import top.creativelab.user.domain.user.vo.Email;
import top.creativelab.user.domain.user.vo.Password;
import top.creativelab.user.domain.user.vo.UserId;
import top.creativelab.common.ports.PageResult;
import top.creativelab.user.ports.UserFilter;
import top.creativelab.user.ports.outbound.user.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserPersistenceAdapter extends BasePersistenceAdapter<User, UserJpaEntity, UUID> implements UserRepository {


    public UserPersistenceAdapter(UserJpaRepository userJpaRepository){
        super(userJpaRepository);
    }

    private UserJpaRepository jpaRepository() {
        return (UserJpaRepository) repository;
    }

    // =====================
    // Mapping
    // =====================

    @Override
    protected UserJpaEntity mapToJpaEntity(User user) {
        UserJpaEntity entity = new UserJpaEntity(
                user.getId().getValue(),
                user.getUsername(),
                user.getUserStatus(),
                user.getPassword().getHash()
        );
        entity.updateFrom(
                user.getUsername(),
                user.getUserStatus(),
                user.getPassword().getHash(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail() != null ? user.getEmail().getValue() : null,
                user.getBirthday() != null ? user.getBirthday().getValue() : null
        );
        return entity;
    }

    @Override
    protected User mapToDomain(UserJpaEntity entity) {
        return User.rehydrate(
                new UserId(entity.getId()),
                entity.getUsername(),
                Password.fromHash(entity.getPassword()),
                entity.getFirstname(),
                entity.getLastname(),
                entity.getEmail() != null ? new Email(entity.getEmail()) : null,
                entity.getBirthday() != null ? new BirthDate(entity.getBirthday()) : null,
                entity.getUserStatus()
        );
    }

    // =====================
    // Persistence
    // =====================

    @Override
    public User save(User user) {
        UUID id = user.getId().getValue();
        UserJpaEntity entity = jpaRepository().findById(id)
                .orElseGet(() -> new UserJpaEntity(
                        id,
                        user.getUsername(),
                        user.getUserStatus(),
                        user.getPassword().getHash()
                ));

        entity.updateFrom(
                user.getUsername(),
                user.getUserStatus(),
                user.getPassword().getHash(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail() != null ? user.getEmail().getValue() : null,
                user.getBirthday() != null ? user.getBirthday().getValue() : null
        );

        return mapToDomain(jpaRepository().save(entity));
    }

    @Override
    public List<User> findAll() {
        return super.findAll();
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public PageResult<User> findPage(int page, int size, UserFilter filter) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "username"));
        Page<UserJpaEntity> result = jpaRepository().findAll(buildSpecification(filter), pageable);
        return PageResult.of(
                result.getContent().stream().map(this::mapToDomain).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements()
        );
    }

    private Specification<UserJpaEntity> buildSpecification(UserFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter != null) {
                if (hasText(filter.username())) {
                    predicates.add(cb.like(cb.lower(root.<String>get("username")), contains(filter.username())));
                }
                if (hasText(filter.email())) {
                    predicates.add(cb.like(cb.lower(root.<String>get("email")), contains(filter.email())));
                }
                if (hasText(filter.firstname())) {
                    predicates.add(cb.like(cb.lower(root.<String>get("firstname")), contains(filter.firstname())));
                }
                if (hasText(filter.lastname())) {
                    predicates.add(cb.like(cb.lower(root.<String>get("lastname")), contains(filter.lastname())));
                }
                if (filter.status() != null) {
                    predicates.add(cb.equal(root.get("userStatus"), filter.status()));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String contains(String value) {
        return "%" + value.toLowerCase() + "%";
    }

    // =====================
    // Queries
    // =====================

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository().existsByUsername(username);
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return jpaRepository().findByUsername(username).map(this::mapToDomain);
    }

    @Override
    public List<User> findByFirstname(String firstname) {
        return jpaRepository().findByFirstname(firstname).stream().map(this::mapToDomain).toList();
    }

    @Override
    public List<User> findByLastname(String lastname) {
        return jpaRepository().findByLastname(lastname).stream().map(this::mapToDomain).toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return super.isExistById(id);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return super.findById(id);
    }

    @Override
    public List<User> findByStatus(UserStatus status) {
        return jpaRepository().findByUserStatus(status).stream().map(this::mapToDomain).toList();
    }

    @Override
    public long countByStatus(UserStatus status) {
        return jpaRepository().countByUserStatus(status);
    }

    @Override
    public List<User> findByUsernameContaining(String pattern) {
        return jpaRepository().findByUsernameContaining(pattern).stream().map(this::mapToDomain).toList();
    }

    @Override
    public List<User> findByEmailContaining(String pattern) {
        return jpaRepository().findByEmailContaining(pattern).stream().map(this::mapToDomain).toList();
    }

    @Override
    public List<User> findByFirstnameContaining(String pattern) {
        return jpaRepository().findByFirstnameContaining(pattern).stream().map(this::mapToDomain).toList();
    }

    @Override
    public List<User> findByLastnameContaining(String pattern) {
        return jpaRepository().findByLastnameContaining(pattern).stream().map(this::mapToDomain).toList();
    }
}
