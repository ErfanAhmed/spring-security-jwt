package net.therap.authService.service;

import lombok.RequiredArgsConstructor;
import net.therap.authService.domain.Authority;
import net.therap.authService.domain.Role;
import net.therap.authService.domain.Status;
import net.therap.authService.domain.User;
import net.therap.authService.dto.UserDto;
import net.therap.authService.dto.UserResponseDto;
import net.therap.authService.repository.AuthorityRepository;
import net.therap.authService.repository.RoleRepository;
import net.therap.authService.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static io.jsonwebtoken.lang.Collections.isEmpty;
import static java.util.Optional.ofNullable;

/**
 * @author erfan
 * @since 7/9/23
 */
@Service
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;
    private final MessageSourceAccessor msa;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public boolean doesUsernameAlreadyExist(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public Optional<User> findById(int id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
        user.setUsername(user.getUsername().toLowerCase());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public User update(UserDto userDto) {
        User user = userRepository.findById(userDto.getId()).orElseThrow();

        if (!isEmpty(userDto.getRoleIds())) {
            Set<Role> newRoles = roleRepository.findByIdIn(userDto.getRoleIds());
            user.setRoles(newRoles);
        }

        if (!isEmpty(userDto.getAuthorityIds())) {
            Set<Authority> newAuthorities = authorityRepository.findByIdIn(userDto.getAuthorityIds());
            user.setAuthoritySet(newAuthorities);
        }

        ofNullable(userDto.getEmail()).ifPresent(user::setEmail);
        ofNullable(userDto.getStatus()).ifPresent(user::setStatus);

        user.setUpdated(LocalDateTime.now());

        user = userRepository.save(user);

        return user;
    }

    public Page<UserResponseDto> findAllByStatus(Status status, int page, int size, Sort sort) {

        PageRequest pageRequest = PageRequest.of(page, size, sort);

        return userRepository.findAllByStatus(status, pageRequest)
                .map(user -> modelMapper.map(user, UserResponseDto.class));
    }
}
