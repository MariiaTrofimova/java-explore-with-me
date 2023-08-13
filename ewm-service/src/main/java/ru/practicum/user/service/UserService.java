package ru.practicum.user.service;

import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto add(NewUserRequest newUserRequest);

    List<UserDto> get(List<Long> ids, int from, int size);

    void delete(Long userId);
}
