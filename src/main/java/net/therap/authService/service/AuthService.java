package net.therap.authService.service;

import lombok.RequiredArgsConstructor;
import net.therap.authService.domain.User;
import net.therap.authService.repository.UserRepository;
import org.hibernate.Hibernate;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author erfan
 * @since 7/6/23
 */
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;

    private final MessageSourceAccessor msa;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        msa.getMessage("username.not.found", new Object[]{username})));
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        User user = userRepository.findByUsername(username).get();

        //error : no session found. temporarily made these relations eager at domain level.
        Hibernate.initialize(user.getRoles());
        Hibernate.initialize(user.getAuthoritySet());

        return user;
    }
}
