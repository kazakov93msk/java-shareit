package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.validation.UserValidator;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    @NotBlank(groups = UserValidator.Create.class)
    @Email(groups = {UserValidator.Create.class, UserValidator.Update.class})
    private String email;
    private String name;
}
