package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@Slf4j
@Validated
@RequiredArgsConstructor
public class UserAdminController {
    private static final String FROM_ERROR_MESSAGE = "Индекс первого элемента не может быть отрицательным";
    private static final String SIZE_ERROR_MESSAGE = "Количество элементов для отображения должно быть положительным";

    private final UserService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto add(@RequestBody NewUserRequest newUserRequest) {
        return service.add(newUserRequest);
    }

    @GetMapping
    public List<UserDto> getStat(@RequestParam(required = false, defaultValue = "") List<Long> ids,
                                 @PositiveOrZero(message = FROM_ERROR_MESSAGE)
                                 @RequestParam(defaultValue = "0") Integer from,
                                 @Positive(message = SIZE_ERROR_MESSAGE)
                                 @RequestParam(defaultValue = "10") Integer size) {
        return service.getAll(ids, from, size);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) {
        service.delete(userId);
    }

}