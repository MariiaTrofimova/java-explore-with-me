package ru.practicum.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.user.User;

import java.util.List;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    @Override
    public List<User> findByIds(List<Long> userIds) {
        return null;
    }

    @Override
    public User findById(long initiator) {
        return null;
    }

    @Override
    public User add(User user) {
        return null;
    }

    @Override
    public List<User> getByIds(List<Long> ids, int from, int size) {
        return null;
    }

    @Override
    public void deleteById(Long userId) {

    }
}
