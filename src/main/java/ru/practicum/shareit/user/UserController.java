package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.validation.UserValidator;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> findAll() {
        log.debug("GET: Get all users.");
        return UserMapper.mapToUserDto(userService.findAll());
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable Long userId) {
        log.debug("GET: Get user with ID = {}.", userId);
        return UserMapper.mapToUserDto(userService.findById(userId));
    }

    @PostMapping
    public UserDto create(@Validated(UserValidator.create.class) @RequestBody UserDto userDto) {
        log.debug("POST: Create user - {}.", userDto);
        User user = UserMapper.mapToUser(userDto);
        return UserMapper.mapToUserDto(userService.create(user));
    }

    @PatchMapping("/{userId}")
    public UserDto update(
            @PathVariable Long userId,
            @RequestBody @Validated(UserValidator.update.class) UserDto userDto
    ) {
        log.debug("PATCH: Update user with ID = {}. New data: {}.", userId, userDto);
        User user = UserMapper.mapToUser(userDto);
        return UserMapper.mapToUserDto(userService.update(userId, user));
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable Long userId) {
        log.debug("DELETE: Delete user with  ID = {}.", userId);
        userService.deleteById(userId);
    }
}
