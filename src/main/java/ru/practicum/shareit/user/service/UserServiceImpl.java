package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRep;

    @Override
    public List<User> findAllUsers() {
        return userRep.findAll();
    }

    @Override
    public User findUserById(Long id) {
        return userRep.findById(id).orElseThrow(
                () -> new NotFoundException(User.class.toString(), id)
        );
    }

    @Override
    public User createUser(User user) {
        try {
            return userRep.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistsException(User.class.toString(), user.getEmail());
        }
    }

    @Override
    public User updateUser(Long userId, User user) {
        User oldUser = userRep.findById(userId).orElseThrow(
                () -> new NotFoundException(User.class.toString(), userId)
        );
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }
        try {
            user = userRep.save(oldUser);
            return user;
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistsException(User.class.toString(), user.getEmail());
        }
    }

    @Override
    public void deleteUserById(Long userId) {
        if (!userRep.existsById(userId)) {
            throw new NotFoundException(User.class.toString(), userId);
        }
        userRep.deleteById(userId);
    }
}
