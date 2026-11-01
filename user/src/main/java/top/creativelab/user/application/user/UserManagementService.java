package top.creativelab.user.application.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import top.creativelab.user.domain.user.User;
import top.creativelab.user.domain.user.UserStatus;
import top.creativelab.user.domain.user.vo.Email;
import top.creativelab.user.domain.user.vo.Password;
import top.creativelab.user.domain.user.vo.UserId;
import top.creativelab.common.ports.PageResult;
import top.creativelab.user.ports.UserFilter;
import top.creativelab.user.ports.inbound.user.*;
import top.creativelab.user.ports.outbound.user.UserRepository;
import top.creativelab.user.application.exception.PasswordWrongExeption;
import top.creativelab.user.application.exception.UserNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserManagementService implements UserManagementUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new user with the given username and password.
     *
     * @param userName      the username of the new user
     * @param rawPassword   the raw password to encode and store
     */
    @Override
    public User create(String userName, String rawPassword) {
        validateUsername(userName);
        if (userRepository.existsByUsername(userName)) {
            throw new IllegalStateException("Username already taken");
        }

        User user = User.create(UserId.newId(), userName, Password.fromRaw(rawPassword, passwordEncoder));
        userRepository.save(user);
        return user;
    }

    /**
     * Updates an existing user's information. Only non-null fields are applied.
     *
     * @param username the username of the user to update
     * @param command  the fields to update
     * @return the updated user
     */
    @Override
    public User update(String username, UpdateUserCommand command) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username must be provided for update");
        }
        if (command == null) {
            throw new IllegalArgumentException("Update payload must be provided");
        }

        User existingUser = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        if (command.firstname() != null) {
            existingUser.updateFirstname(command.firstname());
        }
        if (command.lastname() != null) {
            existingUser.updateLastname(command.lastname());
        }
        if (command.email() != null) {
            existingUser.updateEmail(command.email());
        }
        if (command.birthdate() != null) {
            existingUser.updateBirthdate(command.birthdate());
        }

        userRepository.save(existingUser);

        return existingUser;
    }

    /**
     * Activates a user by setting their status to ACTIVE.
     *
     * @param userName the username of the user to activate
     */
    @Override
    public void activateUser(String userName) {
        User user = userRepository.findUserByUsername(userName)
                .orElseThrow(() -> new UserNotFoundException(userName));

        user.activate();
        userRepository.save(user);
    }

    /**
     * Suspends a user by setting their status to SUSPENDED.
     *
     * @param userName the username of the user to suspend
     */
    @Override
    public void suspend(String userName) {
        User user = userRepository.findUserByUsername(userName)
                .orElseThrow(() -> new UserNotFoundException(userName));

        user.suspend();
        userRepository.save(user);
    }

    /**
     * Reactivates a suspended user by setting their status back to ACTIVE.
     *
     * @param userName the username of the user to reactivate
     */
    @Override
    public void reActive(String userName) {
        User user = userRepository.findUserByUsername(userName)
                .orElseThrow(() -> new UserNotFoundException(userName));

        user.reActive();
        userRepository.save(user);
    }

    /**
     * Soft deletes a user by setting their status to DELETED.
     *
     * @param userName the username of the user to delete
     */
    @Override
    public void delete(String userName) {
        User user = userRepository.findUserByUsername(userName)
                .orElseThrow(() -> new UserNotFoundException(userName));

        user.delete();
        userRepository.save(user);
    }

    /**
     * Finds all users who are currently active.
     *
     * @return list of active users
     */
    public List<User> findActiveUsers() {
        return userRepository.findByStatus(UserStatus.ACTIVE);
    }

    /**
     * Finds all pending users.
     *
     * @return list of pending users
     */
    public List<User> findPendingUsers() {
        return userRepository.findByStatus(UserStatus.PENDING);
    }

    /**
     * Finds all suspended users.
     *
     * @return list of suspended users
     */
    public List<User> findSuspendedUsers() {
        return userRepository.findByStatus(UserStatus.SUSPENDED);
    }

    /**
     * Finds all deleted users.
     *
     * @return list of deleted users
     */
    public List<User> findDeletedUsers() {
        return userRepository.findByStatus(UserStatus.DELETED);
    }

    /**
     * Finds all users.
     *
     * @return list of all users
     */
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Finds a page of users matching the supplied filter.
     *
     * @param page   zero-based page index
     * @param size   page size (1-100)
     * @param filter optional filters; {@code null} means no filtering
     * @return the matching page including total element/page counts
     */
    @Override
    public PageResult<User> findUsers(int page, int size, UserFilter filter) {
        if (page < 0) {
            throw new IllegalArgumentException("Page index must be zero or greater");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }
        return userRepository.findPage(page, size, filter == null ? UserFilter.empty() : filter);
    }

    /**
     * Counts total number of users.
     *
     * @return total count of users
     */
    public long countTotalUsers() {
        return userRepository.count();
    }

    /**
     * Counts active users.
     *
     * @return count of active users
     */
    public long countActiveUsers() {
        return userRepository.countByStatus(UserStatus.ACTIVE);
    }

    /**
     * Counts pending users.
     *
     * @return count of pending users
     */
    public long countPendingUsers() {
        return userRepository.countByStatus(UserStatus.PENDING);
    }

    /**
     * Counts suspended users.
     *
     * @return count of suspended users
     */
    public long countSuspendedUsers() {
        return userRepository.countByStatus(UserStatus.SUSPENDED);
    }

    /**
     * Counts deleted users.
     *
     * @return count of deleted users
     */
    public long countDeletedUsers() {
        return userRepository.countByStatus(UserStatus.DELETED);
    }

    /**
     * Searches users by username pattern.
     *
     * @param pattern the username pattern to search
     * @return list of matching users
     */
    public List<User> findByUsernamePattern(String pattern) {
        return userRepository.findByUsernameContaining(pattern);
    }

    /**
     * Searches users by email pattern.
     *
     * @param pattern the email pattern to search
     * @return list of matching users
     */
    public List<User> findByEmailPattern(String pattern) {
        return userRepository.findByEmailContaining(pattern);
    }

    /**
     * Searches users by first name pattern.
     *
     * @param pattern the first name pattern to search
     * @return list of matching users
     */
    public List<User> findByFirstnamePattern(String pattern) {
        return userRepository.findByFirstnameContaining(pattern);
    }

    /**
     * Searches users by last name pattern.
     *
     * @param pattern the last name pattern to search
     * @return list of matching users
     */
    public List<User> findByLastnamePattern(String pattern) {
        return userRepository.findByLastnameContaining(pattern);
    }

    /**
     * Checks if a user exists by username.
     *
     * @param userName the username to check
     * @return true if user exists, false otherwise
     */
    public boolean existsByUsername(String userName) {
        return userRepository.existsByUsername(userName);
    }

    /**
     * Gets a user by ID.
     *
     * @param id the user ID
     * @return optional containing the user if found
     */
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    /**
     * Gets a user by username.
     *
     * @param userName the username
     * @return optional containing the user if found
     */
    public Optional<User> findUserByUsername(String userName) {
        return userRepository.findUserByUsername(userName);
    }

    /**
     * Checks if a user exists by ID.
     *
     * @param id the user ID to check
     * @return true if user exists, false otherwise
     */
    public boolean existsById(UUID id) {
        return userRepository.existsById(id);
    }

    /**
     * Changes the password for a user.
     *
     * @param userName      the username of the user whose password to change
     * @param rawPassword   the new raw password to set
     */
    @Override
    public void changePassword(String username, String oldRawPassword, String newRawPassword) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        if (oldRawPassword == null
                || !passwordEncoder.matches(oldRawPassword, user.getPassword().getHash())) {
            throw new PasswordWrongExeption();
        }

        Password newPassword = Password.fromRaw(newRawPassword, passwordEncoder);
        user.changePassword(newPassword);
        userRepository.save(user);
    }

    /**
     * Validates that a password meets minimum requirements.
     *
     * @param rawPassword the password to validate
     * @throws IllegalArgumentException if password doesn't meet requirements
     */
    public void validatePassword(String rawPassword) {
        Password.validate(rawPassword);
    }

    /**
     * Validates that a username meets requirements.
     *
     * @param userName the username to validate
     * @throws IllegalArgumentException if username doesn't meet requirements
     */
    public void validateUsername(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (userName.length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters");
        }
        if (!userName.matches("^[a-zA-Z][a-zA-Z0-9_-]*$")) {
            throw new IllegalArgumentException("Username must start with letter and can only contain letters, numbers, underscores or hyphens");
        }
    }

    /**
     * Validates that an email meets requirements.
     *
     * @param email the email to validate
     * @throws IllegalArgumentException if email doesn't meet requirements
     */
    public void validateEmail(Email email) {
        if (email == null || email.getValue() == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
    }

    /**
     * Gets a user by username with validation.
     *
     * @param userName the username
     * @return the user if found and valid, throws exception otherwise
     */
    public User getUserByUsername(String userName) {
        validateUsername(userName);
        return userRepository.findUserByUsername(userName)
                .orElseThrow(() -> new UserNotFoundException(userName));
    }

    /**
     * Gets a user by ID with validation.
     *
     * @param id the user ID
     * @return the user if found and valid, throws exception otherwise
     */
    public User getUserById(UUID id) {
        validateId(id);
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));
    }

    private void validateId(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
    }
}
