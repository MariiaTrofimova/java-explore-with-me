package ru.practicum.user.repository;

import ru.practicum.user.User;

import java.util.List;

public interface UserRepository {
    List<User> findByIds(List<Long> userIds);

    User findById(long initiator);

    User add(User user);

    List<User> getByIds(List<Long> ids, int from, int size);

    void deleteById(Long userId);
}
