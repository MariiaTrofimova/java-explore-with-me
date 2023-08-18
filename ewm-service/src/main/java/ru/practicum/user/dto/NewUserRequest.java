package ru.practicum.user.dto;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class NewUserRequest {
    @NotBlank(message = "E-mail не может быть пустым")
    @Size(min = 6, max = 254, message = "Длина e-mail должна быть от 6 до 254 символов")
    @Email(message = "Введен некорректный e-mail")
    private String email;
    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 250, message = "Длина имени должна быть от 2 до 250 символов")
    private String name;
}