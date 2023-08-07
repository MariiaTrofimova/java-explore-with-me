package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
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
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getByIds(List<Long> ids, Integer from, Integer size) {
        return null;
    }

    @Override
    public void delete(Long userId) {

    }
}
