package top.creativelab.user.ports.inbound.user;

import top.creativelab.user.domain.user.vo.Password;

public interface CreateUserUseCase {
    void create(String userName, String rawPassword);
}
