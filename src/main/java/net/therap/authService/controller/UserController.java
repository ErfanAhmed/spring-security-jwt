package net.therap.authService.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.therap.authService.domain.RoleAuthorityConstant;
import net.therap.authService.domain.Status;
import net.therap.authService.domain.User;
import net.therap.authService.dto.ErrorResponseDto;
import net.therap.authService.dto.UserDto;
import net.therap.authService.dto.UserResponseDto;
import net.therap.authService.repository.AuthorityRepository;
import net.therap.authService.repository.RoleRepository;
import net.therap.authService.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static net.therap.authService.util.ApiResponseUtil.*;

/**
 * @author erfan
 * @since 9/17/23
 */
@RestController
@Slf4j
@RequestMapping(value = "/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private static final String DEFAULT_STATUS = "ACTIVE";
    private static final String DEFAULT_MAX_ITEMS_PER_PAGE = "10";
    private static final String DEFAULT_ORDER_BY_PROPERTY = "username";
    private static final String DEFAULT_ORDER_DIRECTION = "ASC";

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;
    private final MessageSourceAccessor msa;

    @GetMapping
    @RolesAllowed({RoleAuthorityConstant.ADMIN, RoleAuthorityConstant.EMPLOYEE})
    public ResponseEntity<Page<UserResponseDto>> listByStatus(
            @RequestParam(defaultValue = DEFAULT_STATUS) Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = DEFAULT_MAX_ITEMS_PER_PAGE) int size,
            @RequestParam(defaultValue = DEFAULT_ORDER_BY_PROPERTY) String orderBy,
            @RequestParam(defaultValue = DEFAULT_ORDER_DIRECTION) Direction direction) {

        Sort sort = Sort.by(direction, orderBy);

        return successResponse(userService.findAllByStatus(status, page, size, sort));
    }

    @RolesAllowed({RoleAuthorityConstant.ADMIN})
    @PostMapping()
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserDto userDto,
                                          BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return validationErrorResponse(bindingResult);
        }

        if (userService.doesUsernameAlreadyExist(userDto.getUsername())) {
            ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                    HttpStatus.BAD_REQUEST.toString(),
                    msa.getMessage("username.not.available")
            );


            return errorResponse(errorResponseDto);
        }

        User user = modelMapper.map(userDto, User.class);
        user.setRoles(roleRepository.findByIdIn(userDto.getRoleIds()));
        user.setAuthoritySet(authorityRepository.findByIdIn(userDto.getAuthorityIds()));

        user = userService.save(user);

        log.info("Created new user with id " + user.getId());

        return successResponse(modelMapper.map(user, UserResponseDto.class));
    }

    @RolesAllowed(RoleAuthorityConstant.ADMIN)
    @PutMapping()
    public ResponseEntity<?> updateUser(@RequestBody @Valid UserDto userDto,
                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return validationErrorResponse(bindingResult);
        }

        User user = userService.update(userDto);

        log.info("Updated user id: " + user.getId());

        return successResponse(modelMapper.map(user, UserResponseDto.class));
    }

    @RolesAllowed(RoleAuthorityConstant.VIEW)
    @GetMapping(value = "/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable("id") int id) {
        User user = userService.findById(id).get();

        return successResponse(modelMapper.map(user, UserResponseDto.class));
    }
}
