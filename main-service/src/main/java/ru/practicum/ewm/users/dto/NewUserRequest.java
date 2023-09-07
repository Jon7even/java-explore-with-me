package ru.practicum.ewm.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {
    @Email(message = "Field: email. Error: must be valid")
    @NotBlank(message = "Field: email. Error: must not be blank. Value: null")
    @Size(min = 6, max = 254,
            message = "Field: email. Error: must not go beyond. Min=6, Max=254 symbols")
    private String email;

    @NotBlank(message = "Field: name. Error: must not be blank. Value: null")
    @Size(min = 2, max = 250,
            message = "Field: name. Error: must not go beyond. Min=2, Max=250 symbols")
    private String name;

}
