package top.creativelab.user.ports.inbound.user;

import top.creativelab.user.domain.user.vo.Password;

public interface ChangePasswordUseCase {
    void changePassword(String userId, String rawPassword);
}
