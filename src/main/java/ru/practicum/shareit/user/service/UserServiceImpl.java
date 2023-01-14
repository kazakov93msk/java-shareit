package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
    @Transactional
    public User createUser(User user) {
        return userRep.save(user);
    }

    @Override
    @Transactional
    public User updateUser(Long userId, User user) {
        User oldUser = userRep.findById(userId).orElseThrow(
                () -> new NotFoundException(User.class.toString(), userId)
        );
        if (user.getName() != null && !user.getName().isBlank()) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            oldUser.setEmail(user.getEmail());
        }
        return oldUser;
    }

    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        if (!userRep.existsById(userId)) {
            throw new NotFoundException(User.class.toString(), userId);
        }
        userRep.deleteById(userId);
    }
}
