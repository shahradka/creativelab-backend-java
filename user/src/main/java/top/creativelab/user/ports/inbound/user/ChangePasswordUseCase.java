package top.creativelab.user.ports.inbound.user;

public interface ChangePasswordUseCase {
    void changePassword(String userName, String rawPassword);
}
