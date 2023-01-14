package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> findAllUsers() {
        log.debug("GET: Get all users.");
        return UserMapper.mapToUserDto(userService.findAllUsers());
    }

    @GetMapping("/{userId}")
    public UserDto findUserById(@PathVariable Long userId) {
        log.debug("GET: Get user with ID = {}.", userId);
        return UserMapper.mapToUserDto(userService.findUserById(userId));
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.debug("POST: Create user - {}.", userDto);
        User user = UserMapper.mapToUser(userDto);
        return UserMapper.mapToUserDto(userService.createUser(user));
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(
            @PathVariable Long userId,
            @RequestBody UserDto userDto
    ) {
        log.debug("PATCH: Update user with ID = {}. New data: {}.", userId, userDto);
        User user = UserMapper.mapToUser(userDto);
        return UserMapper.mapToUserDto(userService.updateUser(userId, user));
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        log.debug("DELETE: Delete user with  ID = {}.", userId);
        userService.deleteUserById(userId);
    }
}
