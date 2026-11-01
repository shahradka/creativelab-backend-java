package top.creativelab.user.adapters.outbound.persistence.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import top.creativelab.user.domain.user.User;
import top.creativelab.user.domain.user.UserStatus;
import top.creativelab.user.domain.user.vo.BirthDate;
import top.creativelab.user.domain.user.vo.Email;
import top.creativelab.user.domain.user.vo.Password;
import top.creativelab.user.domain.user.vo.UserId;
import top.creativelab.common.ports.PageResult;
import top.creativelab.user.ports.UserFilter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserPersistenceAdapterTest {

    @Mock
    private UserJpaRepository jpaRepository;

    private UserPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UserPersistenceAdapter(jpaRepository);
    }

    private User domainUser() {
        return User.rehydrate(
                new UserId(UUID.randomUUID()),
                "john",
                Password.fromHash("hash"),
                "John",
                "Doe",
                new Email("john@example.com"),
                new BirthDate(LocalDate.of(1990, 1, 1)),
                UserStatus.ACTIVE
        );
    }

    private UserJpaEntity entityFor(User user) {
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
                user.getEmail().getValue(),
                user.getBirthday().getValue()
        );
        return entity;
    }

    @Test
    void findByIdMapsAllFields() {
        User user = domainUser();
        when(jpaRepository.findById(user.getId().getValue())).thenReturn(Optional.of(entityFor(user)));

        Optional<User> found = adapter.findById(user.getId().getValue());

        assertThat(found).isPresent();
        User mapped = found.get();
        assertThat(mapped.getId()).isEqualTo(user.getId());
        assertThat(mapped.getUsername()).isEqualTo("john");
        assertThat(mapped.getFirstname()).isEqualTo("John");
        assertThat(mapped.getLastname()).isEqualTo("Doe");
        assertThat(mapped.getEmail()).isEqualTo(user.getEmail());
        assertThat(mapped.getBirthday()).isEqualTo(user.getBirthday());
        assertThat(mapped.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(mapped.getPassword().getHash()).isEqualTo("hash");
    }

    @Test
    void saveCreatesNewEntityWhenAbsent() {
        User user = domainUser();
        when(jpaRepository.findById(user.getId().getValue())).thenReturn(Optional.empty());
        when(jpaRepository.save(any(UserJpaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = adapter.save(user);

        assertThat(saved.getUsername()).isEqualTo("john");
        assertThat(saved.getEmail()).isEqualTo(user.getEmail());
        verify(jpaRepository).save(any(UserJpaEntity.class));
    }

    @Test
    void saveMutatesExistingEntity() {
        User user = domainUser();
        UserJpaEntity existing = entityFor(user);
        when(jpaRepository.findById(user.getId().getValue())).thenReturn(Optional.of(existing));
        when(jpaRepository.save(existing)).thenReturn(existing);

        user.updateFirstname("Jane");
        adapter.save(user);

        assertThat(existing.getFirstname()).isEqualTo("Jane");
        verify(jpaRepository).save(existing);
    }

    @Test
    void findAllMapsEntities() {
        User user = domainUser();
        when(jpaRepository.findAll()).thenReturn(List.of(entityFor(user)));

        assertThat(adapter.findAll()).hasSize(1);
    }

    @Test
    void countDelegates() {
        when(jpaRepository.count()).thenReturn(7L);

        assertThat(adapter.count()).isEqualTo(7L);
    }

    @Test
    void findPageMapsContentAndCounts() {
        User user = domainUser();
        Page<UserJpaEntity> page = new PageImpl<>(
                List.of(entityFor(user)), PageRequest.of(0, 10), 1);
        when(jpaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        PageResult<User> result = adapter.findPage(0, 10, UserFilter.empty());

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().getFirst().getUsername()).isEqualTo("john");
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.totalPages()).isEqualTo(1);
    }

    @Test
    void existsByUsernameDelegates() {
        when(jpaRepository.existsByUsername("john")).thenReturn(true);

        assertThat(adapter.existsByUsername("john")).isTrue();
    }

    @Test
    void findUserByUsernameMaps() {
        User user = domainUser();
        when(jpaRepository.findByUsername("john")).thenReturn(Optional.of(entityFor(user)));

        assertThat(adapter.findUserByUsername("john")).isPresent();
    }

    @Test
    void findByFirstnameMaps() {
        User user = domainUser();
        when(jpaRepository.findByFirstname("John")).thenReturn(List.of(entityFor(user)));

        assertThat(adapter.findByFirstname("John")).hasSize(1);
    }

    @Test
    void findByLastnameMaps() {
        User user = domainUser();
        when(jpaRepository.findByLastname("Doe")).thenReturn(List.of(entityFor(user)));

        assertThat(adapter.findByLastname("Doe")).hasSize(1);
    }

    @Test
    void existsByIdDelegates() {
        UUID id = UUID.randomUUID();
        when(jpaRepository.existsById(id)).thenReturn(true);

        assertThat(adapter.existsById(id)).isTrue();
    }

    @Test
    void findByStatusMaps() {
        User user = domainUser();
        when(jpaRepository.findByUserStatus(UserStatus.ACTIVE)).thenReturn(List.of(entityFor(user)));

        assertThat(adapter.findByStatus(UserStatus.ACTIVE)).hasSize(1);
    }

    @Test
    void countByStatusDelegates() {
        when(jpaRepository.countByUserStatus(UserStatus.ACTIVE)).thenReturn(2L);

        assertThat(adapter.countByStatus(UserStatus.ACTIVE)).isEqualTo(2L);
    }

    @Test
    void findByUsernameContainingMaps() {
        User user = domainUser();
        when(jpaRepository.findByUsernameContaining("jo")).thenReturn(List.of(entityFor(user)));

        assertThat(adapter.findByUsernameContaining("jo")).hasSize(1);
    }

    @Test
    void findByEmailContainingMaps() {
        User user = domainUser();
        when(jpaRepository.findByEmailContaining("example")).thenReturn(List.of(entityFor(user)));

        assertThat(adapter.findByEmailContaining("example")).hasSize(1);
    }

    @Test
    void findByFirstnameContainingMaps() {
        User user = domainUser();
        when(jpaRepository.findByFirstnameContaining("Jo")).thenReturn(List.of(entityFor(user)));

        assertThat(adapter.findByFirstnameContaining("Jo")).hasSize(1);
    }

    @Test
    void findByLastnameContainingMaps() {
        User user = domainUser();
        when(jpaRepository.findByLastnameContaining("Do")).thenReturn(List.of(entityFor(user)));

        assertThat(adapter.findByLastnameContaining("Do")).hasSize(1);
    }
}
