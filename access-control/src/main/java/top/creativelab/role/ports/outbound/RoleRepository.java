package top.creativelab.role.ports.outbound;

import top.creativelab.role.domain.Role;
import top.creativelab.role.domain.vo.RoleId;

import java.util.List;
import java.util.Optional;

public interface RoleRepository {
    Role save(Role role);
    Optional<Role> findById(RoleId roleId);
    boolean isExistById(RoleId roleId);
    void delete(RoleId roleId);
    List<Role> findByName(String name);
}
