package net.therap.authService.repository;

import net.therap.authService.domain.Authority;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

/**
 * @author erfan
 * @since 9/17/23
 */
public interface AuthorityRepository extends CrudRepository<Authority, Integer> {

    Set<Authority> findByIdIn(List<Integer> ids);
}
