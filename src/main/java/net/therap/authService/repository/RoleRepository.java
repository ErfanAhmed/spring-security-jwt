package net.therap.authService.repository;

import net.therap.authService.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

/**
 * @author erfan
 * @since 9/14/23
 */
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Set<Role> findByIdIn(List<Integer> roles);
}
