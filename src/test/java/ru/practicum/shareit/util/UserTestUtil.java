package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class UserTestUtil {
    public static final String USER_DEFAULT_PATH = "/users";
    public static final Long USER_ID = 1L;
    public static final String USER_NAME = "first";
    public static final String USER_EMAIL = "first@test.ru";
    public static final Long ANOTHER_USER_ID = 2L;
    public static final String ANOTHER_USER_NAME = "second";
    public static final String USER_PATH = USER_DEFAULT_PATH + "/" + USER_ID;
    public static final String ANOTHER_USER_PATH = USER_DEFAULT_PATH + "/" + ANOTHER_USER_ID;


    public static User getUser() {
        return new User(USER_ID, USER_EMAIL, USER_NAME);
    }

    public static User getUpdatedUser() {
        return new User(USER_ID, USER_EMAIL, ANOTHER_USER_NAME);
    }

    public static User getAnotherUser() {
        return new User(ANOTHER_USER_ID, "another@test.com", ANOTHER_USER_NAME);
    }

    public static UserDto getUserDto() {
        return new UserDto(null, USER_EMAIL, USER_NAME);
    }

    public static UserDto getUpdatedUserDto() {
        return new UserDto(null, USER_EMAIL, ANOTHER_USER_NAME);
    }
}
