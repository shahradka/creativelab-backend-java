package top.creativelab.user.application.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import top.creativelab.user.domain.user.User;
import top.creativelab.user.domain.user.vo.Password;
import top.creativelab.user.domain.user.vo.UserId;
import top.creativelab.user.ports.inbound.user.CreateUserUseCase;
import top.creativelab.user.ports.outbound.user.UserRepository;

@Service
@RequiredArgsConstructor
public class CreateUserService implements CreateUserUseCase {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void create(String userName, String rawPassword) {
        if (repository.existsByUsername(userName)) {
            throw new IllegalStateException("Username already taken");
        }

        User user = User.create(UserId.newId(), userName, Password.fromRaw(rawPassword, passwordEncoder));
        repository.save(user);
    }
}
