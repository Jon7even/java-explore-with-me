package ru.practicum.ewm.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewCategoryDto {
    @Size(min = 1, max = 50,
            message = "Field: name. Error: must not go beyond. Min=1, Max=50 symbols")
    @NotBlank(message = "Field: name. Error: must not be blank. Value: null")
    private String name;
}
