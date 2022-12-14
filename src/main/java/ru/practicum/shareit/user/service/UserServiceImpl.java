package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRep;

    @Override
    public List<User> findAllUsers() {
        return userRep.findAllUsers();
    }

    @Override
    public User findUserById(Long id) {
        User user = userRep.findUserById(id).orElseThrow(
                () -> new NotFoundException(User.class.toString(), id)
        );
        log.debug("UserService: User {} returned.", user);
        return user;
    }

    @Override
    public User createUser(User user) {
        if (user.getId() != null && userRep.userExists(user.getId())) {
            throw new AlreadyExistsException(User.class.toString(), user.getId());
        }
        if (userRep.findAllUsers().contains(user)) {
            throw new AlreadyExistsException(User.class.toString(), user.getEmail());
        }
        user = userRep.createUser(user);
        log.debug("User {} created.", user);
        return user;
    }

    @Override
    public User updateUser(Long userId, User user) {
        if (!userRep.userExists(userId)) {
            throw new NotFoundException(User.class.toString(), userId);
        }
        User oldUser = findUserById(userId);
        Optional<User> dupleEmailUser = userRep.findUserByEmail(user.getEmail());
        if (dupleEmailUser.isPresent() && !dupleEmailUser.get().getId().equals(userId)) {
            throw new AlreadyExistsException(User.class.toString(), user.getEmail());
        }
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }
        log.debug("UserService: User with ID = {} updated. New user data: {}.", userId, oldUser);
        return userRep.updateUser(userId, oldUser);
    }

    @Override
    public void deleteUserById(Long userId) {
        if (!userRep.userExists(userId)) {
            throw new NotFoundException(User.class.toString(), userId);
        }
        userRep.deleteUserById(userId);
        log.debug("UserService: User with ID = {} deleted.", userId);
    }
}
