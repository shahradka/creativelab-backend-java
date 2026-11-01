package top.creativelab.user.adapters.inbound.rest.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import top.creativelab.user.domain.user.User;
import top.creativelab.user.domain.user.UserStatus;
import top.creativelab.user.domain.user.vo.BirthDate;
import top.creativelab.user.domain.user.vo.Email;
import top.creativelab.user.domain.user.vo.Password;
import top.creativelab.user.domain.user.vo.UserId;
import top.creativelab.common.ports.PageResult;
import top.creativelab.user.ports.inbound.user.UserManagementUseCase;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserManagementUseCase useCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(useCase))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    private User user() {
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

    @Test
    void createUserReturnsResponseWithoutPassword() throws Exception {
        when(useCase.create("john", "Str0ng!Pass")).thenReturn(user());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"john\",\"password\":\"Str0ng!Pass\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void updateUserReturnsUpdatedUser() throws Exception {
        when(useCase.update(eq("john"), any())).thenReturn(user());

        mockMvc.perform(put("/users/update/john")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstname\":\"Jane\",\"lastname\":\"Smith\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("John"));
    }

    @Test
    void changePasswordReturnsNoContent() throws Exception {
        mockMvc.perform(put("/users/password")
                        .header("X-User-Id", "john")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\":\"Old!Pass1\",\"newPassword\":\"New!Pass1\"}"))
                .andExpect(status().isNoContent());

        verify(useCase).changePassword("john", "Old!Pass1", "New!Pass1");
    }

    @Test
    void activateUserReturnsNoContent() throws Exception {
        mockMvc.perform(put("/users/activate/john")).andExpect(status().isNoContent());

        verify(useCase).activateUser("john");
    }

    @Test
    void suspendUserReturnsNoContent() throws Exception {
        mockMvc.perform(put("/users/suspend/john")).andExpect(status().isNoContent());

        verify(useCase).suspend("john");
    }

    @Test
    void reActiveUserReturnsNoContent() throws Exception {
        mockMvc.perform(put("/users/reactivate/john")).andExpect(status().isNoContent());

        verify(useCase).reActive("john");
    }

    @Test
    void deleteUserReturnsNoContent() throws Exception {
        mockMvc.perform(put("/users/delete/john")).andExpect(status().isNoContent());

        verify(useCase).delete("john");
    }

    @Test
    void findActiveUsersReturnsList() throws Exception {
        when(useCase.findActiveUsers()).thenReturn(List.of(user()));

        mockMvc.perform(get("/users/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("john"));
    }

    @Test
    void findPendingUsersReturnsList() throws Exception {
        when(useCase.findPendingUsers()).thenReturn(List.of());

        mockMvc.perform(get("/users/pending")).andExpect(status().isOk());
    }

    @Test
    void findSuspendedUsersReturnsList() throws Exception {
        when(useCase.findSuspendedUsers()).thenReturn(List.of());

        mockMvc.perform(get("/users/suspended")).andExpect(status().isOk());
    }

    @Test
    void findDeletedUsersReturnsList() throws Exception {
        when(useCase.findDeletedUsers()).thenReturn(List.of());

        mockMvc.perform(get("/users/deleted")).andExpect(status().isOk());
    }

    @Test
    void findAllUsersReturnsList() throws Exception {
        when(useCase.findAllUsers()).thenReturn(List.of(user()));

        mockMvc.perform(get("/users/all")).andExpect(status().isOk());
    }

    @Test
    void findUsersPageReturnsPageMetadata() throws Exception {
        PageResult<User> page = PageResult.of(List.of(user()), 0, 10, 1);
        when(useCase.findUsers(eq(0), eq(10), any())).thenReturn(page);

        mockMvc.perform(get("/users/page")
                        .param("page", "0")
                        .param("size", "10")
                        .param("username", "jo")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("john"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void countTotalUsersReturnsCount() throws Exception {
        when(useCase.countTotalUsers()).thenReturn(5L);

        mockMvc.perform(get("/users/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    void countActiveUsersReturnsCount() throws Exception {
        when(useCase.countActiveUsers()).thenReturn(2L);

        mockMvc.perform(get("/users/count/active")).andExpect(status().isOk());
    }

    @Test
    void countPendingUsersReturnsCount() throws Exception {
        when(useCase.countPendingUsers()).thenReturn(3L);

        mockMvc.perform(get("/users/count/pending")).andExpect(status().isOk());
    }

    @Test
    void countSuspendedUsersReturnsCount() throws Exception {
        when(useCase.countSuspendedUsers()).thenReturn(1L);

        mockMvc.perform(get("/users/count/suspended")).andExpect(status().isOk());
    }

    @Test
    void countDeletedUsersReturnsCount() throws Exception {
        when(useCase.countDeletedUsers()).thenReturn(4L);

        mockMvc.perform(get("/users/count/deleted")).andExpect(status().isOk());
    }

    @Test
    void findByUsernamePatternReturnsList() throws Exception {
        when(useCase.findByUsernamePattern("jo")).thenReturn(List.of(user()));

        mockMvc.perform(get("/users/search/username").param("pattern", "jo"))
                .andExpect(status().isOk());
    }

    @Test
    void findByEmailPatternReturnsList() throws Exception {
        when(useCase.findByEmailPattern("example")).thenReturn(List.of(user()));

        mockMvc.perform(get("/users/search/email").param("pattern", "example"))
                .andExpect(status().isOk());
    }

    @Test
    void findByFirstnamePatternReturnsList() throws Exception {
        when(useCase.findByFirstnamePattern("Jo")).thenReturn(List.of(user()));

        mockMvc.perform(get("/users/search/firstname").param("pattern", "Jo"))
                .andExpect(status().isOk());
    }

    @Test
    void findByLastnamePatternReturnsList() throws Exception {
        when(useCase.findByLastnamePattern("Do")).thenReturn(List.of(user()));

        mockMvc.perform(get("/users/search/lastname").param("pattern", "Do"))
                .andExpect(status().isOk());
    }

    @Test
    void validatePasswordReturnsOk() throws Exception {
        mockMvc.perform(get("/users/validate/password").param("password", "Str0ng!Pass"))
                .andExpect(status().isOk());
    }

    @Test
    void validatePasswordReturnsBadRequestOnFailure() throws Exception {
        doThrow(new IllegalArgumentException("weak")).when(useCase).validatePassword("weak");

        mockMvc.perform(get("/users/validate/password").param("password", "weak"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validateUsernameReturnsOk() throws Exception {
        mockMvc.perform(get("/users/validate/username").param("username", "john"))
                .andExpect(status().isOk());
    }

    @Test
    void validateEmailReturnsOk() throws Exception {
        mockMvc.perform(get("/users/validate/email").param("email", "john@example.com"))
                .andExpect(status().isOk());
    }
}
