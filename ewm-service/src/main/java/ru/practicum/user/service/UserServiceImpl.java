package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.error.exceptions.ConflictException;
import ru.practicum.user.User;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public UserDto add(NewUserRequest newUserRequest) {
        User user = UserMapper.toUser(newUserRequest);
        try {
            user = repository.add(user);
        } catch (RuntimeException e) {
            reportUniqueConflict(e, user);
        }

        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> get(List<Long> ids, int from, int size) {
        List<User> users;
        if (ids.isEmpty()) {
            users = repository.get(from, size);
        } else {
            users = repository.findByIds(ids, from, size);
        }
        return UserMapper.toUserDto(users);
    }

    @Override
    public void delete(Long userId) {
        repository.deleteById(userId);
    }

    private void reportUniqueConflict(RuntimeException e, User user) {
        String error = e.getMessage();
        String constraintName = "uq_user_name";
        String constraintEmail = "uq_user_email";
        if (error.contains(constraintName)) {
            error = String.format("Пользователь с именем %s уже существует", user.getName());
            log.warn("Попытка дублирования имени пользователя: {}", user.getName());
            throw new ConflictException(error);
        } else if (error.contains(constraintEmail)) {
            error = String.format("Пользователь с e-mail %s уже существует", user.getEmail());
            log.warn("Попытка дублирования e-mail пользователя: {}", user.getEmail());
            throw new ConflictException(error);
        }
        throw new RuntimeException("Ошибка при передаче данных в БД");
    }
}
