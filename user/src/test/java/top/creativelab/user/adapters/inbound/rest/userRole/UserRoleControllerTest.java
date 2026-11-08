package top.creativelab.user.adapters.inbound.rest.userRole;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import top.creativelab.user.domain.user.vo.UserId;
import top.creativelab.user.domain.userRole.UserRole;
import top.creativelab.user.domain.userRole.vo.RoleId;
import top.creativelab.user.domain.userRole.vo.UserRoleId;
import top.creativelab.user.ports.inbound.userRole.UserRoleUseCase;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserRoleControllerTest {

    @Mock
    private UserRoleUseCase userRoleUseCase;

    private MockMvc mockMvc;

    private UUID userId;
    private UUID roleId;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
        mockMvc = MockMvcBuilders.standaloneSetup(new UserRoleController(userRoleUseCase))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
        userId = UUID.randomUUID();
        roleId = UUID.randomUUID();
    }

    private UserRole assignment() {
        return UserRole.rehydrate(new UserRoleId(UUID.randomUUID()), new UserId(userId), new RoleId(roleId));
    }

    @Test
    void assignRoleReturnsCreated() throws Exception {
        when(userRoleUseCase.assignRole(userId, roleId)).thenReturn(assignment());

        mockMvc.perform(post("/user-roles/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roleId\":\"%s\"}".formatted(roleId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.roleId").value(roleId.toString()));
    }

    @Test
    void replaceRolesReturnsUpdatedAssignments() throws Exception {
        UUID secondRole = UUID.randomUUID();
        when(userRoleUseCase.replaceRoles(eq(userId), any()))
                .thenReturn(List.of(assignment(), assignment()));

        mockMvc.perform(put("/user-roles/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roleIds\":[\"%s\",\"%s\"]}".formatted(roleId, secondRole)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void findByUserIdReturnsAssignments() throws Exception {
        when(userRoleUseCase.findByUserId(userId)).thenReturn(List.of(assignment()));

        mockMvc.perform(get("/user-roles/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roleId").value(roleId.toString()));
    }

    @Test
    void findByRoleIdReturnsAssignments() throws Exception {
        when(userRoleUseCase.findByRoleId(roleId)).thenReturn(List.of(assignment()));

        mockMvc.perform(get("/user-roles/role/{roleId}", roleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(userId.toString()));
    }

    @Test
    void hasRoleReturnsBoolean() throws Exception {
        when(userRoleUseCase.hasRole(userId, roleId)).thenReturn(true);

        mockMvc.perform(get("/user-roles/user/{userId}/{roleId}/exists", userId, roleId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void countRolesReturnsCount() throws Exception {
        when(userRoleUseCase.countRoles(userId)).thenReturn(2L);

        mockMvc.perform(get("/user-roles/user/{userId}/count", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    void unassignRoleReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/user-roles/user/{userId}/{roleId}", userId, roleId))
                .andExpect(status().isNoContent());

        verify(userRoleUseCase).unassignRole(userId, roleId);
    }

    @Test
    void replaceRolesAcceptsNullRoleIds() throws Exception {
        when(userRoleUseCase.replaceRoles(eq(userId), eq(Set.of()))).thenReturn(List.of());

        mockMvc.perform(put("/user-roles/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roleIds\":[]}"))
                .andExpect(status().isOk());
    }
}
