package top.creativelab.user.ports.inbound.user;

import top.creativelab.user.domain.user.User;
import top.creativelab.user.domain.user.vo.Email;
import top.creativelab.common.ports.PageResult;
import top.creativelab.user.ports.UserFilter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserManagementUseCase {

    /**
     * Changes a user's password.
     *
     * @param userId        the user ID
     * @param oldPassword   the current password
     * @param newPassword  the new password
     * @throws IllegalArgumentException if passwords don't match or validation fails
     */
    void changePassword(String username, String oldPassword, String newPassword);

    /**
     * Creates a new user with the given username and password.
     *
     * @param userName      the username of the new user
     * @param rawPassword   the raw password to encode and store
     * @return the created user
     */
    User create(String userName, String rawPassword);

    /**
     * Updates an existing user's information.
     *
     * @param username the username of the user to update
     * @param command  the fields to update
     * @return the updated user
     */
    User update(String username, UpdateUserCommand command);

    /**
     * Activates a user by setting their status to ACTIVE.
     *
     * @param userName the username of the user to activate
     */
    void activateUser(String userName);

    /**
     * Suspends a user by setting their status to SUSPENDED.
     *
     * @param userName the username of the user to suspend
     */
    void suspend(String userName);

    /**
     * Reactivates a suspended user by setting their status back to ACTIVE.
     *
     * @param userName the username of the user to reactivate
     */
    void reActive(String userName);

    /**
     * Soft deletes a user by setting their status to DELETED.
     *
     * @param userName the username of the user to delete
     */
    void delete(String userName);

    /**
     * Finds all users who are currently active.
     *
     * @return list of active users
     */
    List<User> findActiveUsers();

    /**
     * Finds all pending users.
     *
     * @return list of pending users
     */
    List<User> findPendingUsers();

    /**
     * Finds all suspended users.
     *
     * @return list of suspended users
     */
    List<User> findSuspendedUsers();

    /**
     * Finds all deleted users.
     *
     * @return list of deleted users
     */
    List<User> findDeletedUsers();

    /**
     * Finds all users.
     *
     * @return list of all users
     */
    List<User> findAllUsers();

    /**
     * Finds a page of users matching the supplied filter.
     *
     * @param page   zero-based page index
     * @param size   page size
     * @param filter optional filters; {@code null} means no filtering
     * @return the matching page including total element/page counts
     */
    PageResult<User> findUsers(int page, int size, UserFilter filter);

    /**
     * Counts total number of users.
     *
     * @return total count of users
     */
    long countTotalUsers();

    /**
     * Counts active users.
     *
     * @return count of active users
     */
    long countActiveUsers();

    /**
     * Counts pending users.
     *
     * @return count of pending users
     */
    long countPendingUsers();

    /**
     * Counts suspended users.
     *
     * @return count of suspended users
     */
    long countSuspendedUsers();

    /**
     * Counts deleted users.
     *
     * @return count of deleted users
     */
    long countDeletedUsers();

    /**
     * Searches users by username pattern.
     *
     * @param pattern the username pattern to search
     * @return list of matching users
     */
    List<User> findByUsernamePattern(String pattern);

    /**
     * Searches users by email pattern.
     *
     * @param pattern the email pattern to search
     * @return list of matching users
     */
    List<User> findByEmailPattern(String pattern);

    /**
     * Searches users by first name pattern.
     *
     * @param pattern the first name pattern to search
     * @return list of matching users
     */
    List<User> findByFirstnamePattern(String pattern);

    /**
     * Searches users by last name pattern.
     *
     * @param pattern the last name pattern to search
     * @return list of matching users
     */
    List<User> findByLastnamePattern(String pattern);

    /**
     * Checks if a user exists by username.
     *
     * @param userName the username to check
     * @return true if user exists, false otherwise
     */
    boolean existsByUsername(String userName);

    /**
     * Gets a user by ID.
     *
     * @param id the user ID
     * @return optional containing the user if found
     */
    Optional<User> findById(UUID id);

    /**
     * Gets a user by username.
     *
     * @param userName the username
     * @return optional containing the user if found
     */
    Optional<User> findUserByUsername(String userName);

    /**
     * Checks if a user exists by ID.
     *
     * @param id the user ID to check
     * @return true if user exists, false otherwise
     */
    boolean existsById(UUID id);

    /**
     * Validates that a password meets minimum requirements.
     *
     * @param rawPassword the password to validate
     * @throws IllegalArgumentException if password doesn't meet requirements
     */
    void validatePassword(String rawPassword);

    /**
     * Validates that a username meets requirements.
     *
     * @param userName the username to validate
     * @throws IllegalArgumentException if username doesn't meet requirements
     */
    void validateUsername(String userName);

    /**
     * Validates that an email meets requirements.
     *
     * @param email the email to validate
     * @throws IllegalArgumentException if email doesn't meet requirements
     */
    void validateEmail(Email email);

    /**
     * Gets a user by username with validation.
     *
     * @param userName the username
     * @return the user if found and valid, throws exception otherwise
     */
    User getUserByUsername(String userName);

    /**
     * Gets a user by ID with validation.
     *
     * @param id the user ID
     * @return the user if found and valid, throws exception otherwise
     */
    User getUserById(UUID id);
}
