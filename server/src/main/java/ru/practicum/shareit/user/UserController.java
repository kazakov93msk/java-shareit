package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> findAll() {
        return UserMapper.mapToUserDto(userService.findAll());
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable Long userId) {
        return UserMapper.mapToUserDto(userService.findById(userId));
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        user = userService.create(user);
        return UserMapper.mapToUserDto(user);
    }

    @PatchMapping("/{userId}")
    public UserDto update(
            @PathVariable Long userId,
            @RequestBody UserDto userDto
    ) {
        User user = UserMapper.mapToUser(userDto);
        return UserMapper.mapToUserDto(userService.update(userId, user));
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable Long userId) {
        userService.deleteById(userId);
    }
}
