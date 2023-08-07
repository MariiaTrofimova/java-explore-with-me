package ru.practicum.user.dto;


import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
@Builder
public class NewUserRequest {
    @Size(min = 6, max = 254, message = "Длина e-mail должна быть от 6 до 254 символов")
    @Email(message = "Введен некорректный e-mail")
    private String email;
    @Size(min = 2, max = 250, message = "Длина имени должна быть от 2 до 250 символов")
    private String name;
}
