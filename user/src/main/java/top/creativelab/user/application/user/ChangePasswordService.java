package top.creativelab.user.application.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import top.creativelab.user.domain.user.User;
import top.creativelab.user.domain.user.vo.Password;
import top.creativelab.user.ports.inbound.user.ChangePasswordUseCase;
import top.creativelab.user.ports.outbound.user.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChangePasswordService implements ChangePasswordUseCase {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void changePassword(String userName, String rawPassword) {
        User user = repository.findUserByUsername(userName).orElseThrow(() -> new IllegalArgumentException("User Not Found"));
        Password newPassword = Password.fromRaw(rawPassword, passwordEncoder);

        user.changePassword(newPassword);
        repository.save(user);
    }
}
