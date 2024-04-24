package net.therap.authService.repository;

import net.therap.authService.domain.Status;
import net.therap.authService.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author erfan
 * @since 7/6/23
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    Page<User> findAllByStatus(Status status, Pageable pageable);
}
