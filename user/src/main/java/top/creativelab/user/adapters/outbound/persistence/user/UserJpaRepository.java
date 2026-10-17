package top.creativelab.user.adapters.outbound.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, String> {
    @Override
    Optional<UserJpaEntity> findById(String id);
    Optional<UserJpaEntity> findByUsername(String username);
    boolean existsByUsername(String username);
}
