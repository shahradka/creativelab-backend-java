package top.creativelab.user.adapters.outbound.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import top.creativelab.user.domain.user.UserStatus;

import java.util.Optional;
import java.util.UUID;
import java.util.List;


public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID>, JpaSpecificationExecutor<UserJpaEntity> {
    Optional<UserJpaEntity> findByUsername(String username);
    List<UserJpaEntity> findByFirstname(String firstname);
    List<UserJpaEntity> findByLastname(String lastname);
    boolean existsByUsername(String username);
    List<UserJpaEntity> findByUserStatus(UserStatus userStatus);
    long countByUserStatus(UserStatus userStatus);
    List<UserJpaEntity> findByUsernameContaining(String pattern);
    List<UserJpaEntity> findByEmailContaining(String pattern);
    List<UserJpaEntity> findByFirstnameContaining(String pattern);
    List<UserJpaEntity> findByLastnameContaining(String pattern);
}
