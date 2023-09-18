package ru.practicum.ewm.controllers.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.practicum.ewm.setup.GenericControllerTest;
import ru.practicum.ewm.users.dto.UserDto;
import ru.practicum.ewm.users.service.UserService;

import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.ewm.constants.EndpointsPaths.USERS_ADMIN;
import static ru.practicum.ewm.constants.NamesExceptions.DUPLICATE_EMAIL;

public class UserAdminControllerTest extends GenericControllerTest {
    @Autowired
    protected UserService userService;

    @BeforeEach
    void setUp() {
        initNewUserRequest();
    }

    @Test
    @DisplayName("Пользователь должен создаться с релевантными полями [createUser]")
    void shouldCreateUser_thenStatus201() throws Exception {
        mockMvc.perform(post(USERS_ADMIN)
                        .content(objectMapper.writeValueAsString(firstNewUserRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(firstId))
                .andExpect(jsonPath("email").value(firstNewUserRequest.getEmail()))
                .andExpect(jsonPath("name").value(firstNewUserRequest.getName()));
    }

    @Test
    @DisplayName("Пользователь не должен создаться [createUser]")
    void shouldNotCreateUser_thenStatus400And409() throws Exception {
        secondNewUserRequest.setName(null);
        mockMvc.perform(post(USERS_ADMIN)
                        .content(objectMapper.writeValueAsString(secondNewUserRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value("BAD_REQUEST"))
                .andExpect(jsonPath("reason").value("Incorrectly made request."))
                .andExpect(jsonPath("message")
                        .value("Field: name. Error: must not be blank. Value: null"))
                .andExpect(jsonPath("timestamp").value(notNullValue()));

        thirdNewUserRequest.setEmail("2@r.t");
        mockMvc.perform(post(USERS_ADMIN)
                        .content(objectMapper.writeValueAsString(thirdNewUserRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value("BAD_REQUEST"))
                .andExpect(jsonPath("reason").value("Incorrectly made request."))
                .andExpect(jsonPath("message")
                        .value("Field: email. Error: must not go beyond. Min=6, Max=254 symbols"))
                .andExpect(jsonPath("timestamp").value(notNullValue()));

        userService.createUser(firstNewUserRequest);
        mockMvc.perform(post(USERS_ADMIN)
                        .content(objectMapper.writeValueAsString(firstNewUserRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("status").value("CONFLICT"))
                .andExpect(jsonPath("reason").value("Integrity constraint has been violated."))
                .andExpect(jsonPath("message").value(DUPLICATE_EMAIL))
                .andExpect(jsonPath("timestamp").value(notNullValue()));
    }

    @Test
    @DisplayName("Пользователь должен удалиться по [ID] [removeUserById]")
    void shouldDeleteUserById_thenStatus204And404() throws Exception {
        userService.createUser(firstNewUserRequest);
        Long idFirstUser = userService.getUsersByListId(Collections.emptyList(), 0, 10).get(0).getId();

        mockMvc.perform(delete("/admin/users/{id}", secondId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value("NOT_FOUND"))
                .andExpect(jsonPath("reason").value("The required object was not found."))
                .andExpect(jsonPath("message")
                        .value("User with id=" + secondId + " was not found"))
                .andExpect(jsonPath("timestamp").value(notNullValue()));

        mockMvc.perform(delete("/admin/users/{id}", idFirstUser))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/admin/users/{id}", idFirstUser))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Получить всех пользователей без указания ListIds [getUsersByListId]")
    void shouldGetAllUsersByListEmpty_thenStatus200() throws Exception {
        UserDto user1 = userService.createUser(firstNewUserRequest);
        UserDto user2 = userService.createUser(secondNewUserRequest);
        UserDto user3 = userService.createUser(thirdNewUserRequest);

        mockMvc.perform(get(USERS_ADMIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(user1.getId()))
                .andExpect(jsonPath("$[0].name").value(user1.getName()))
                .andExpect(jsonPath("$[1].id").value(user2.getId()))
                .andExpect(jsonPath("$[1].name").value(user2.getName()))
                .andExpect(jsonPath("$[2].id").value(user3.getId()))
                .andExpect(jsonPath("$[2].name").value(user3.getName()));

        mockMvc.perform(get("/admin/users?from={from}&size={size}", 0, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(user1.getId()))
                .andExpect(jsonPath("$[0].name").value(user1.getName()));
    }

    @Test
    @DisplayName("Получить всех пользователей c указанием ListIds [getUsersByListId]")
    void shouldGetAllUsersByListNotEmpty_thenStatus200() throws Exception {
        UserDto user1 = userService.createUser(firstNewUserRequest);
        UserDto user2 = userService.createUser(secondNewUserRequest);
        UserDto user3 = userService.createUser(thirdNewUserRequest);

        mockMvc.perform(get(USERS_ADMIN)
                        .param("ids", "1")
                        .param("ids", "3")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(user1.getId()))
                .andExpect(jsonPath("$[0].name").value(user1.getName()))
                .andExpect(jsonPath("$[1].id").value(user3.getId()))
                .andExpect(jsonPath("$[1].name").value(user3.getName()));

        mockMvc.perform(get(USERS_ADMIN)
                        .param("ids", "1")
                        .param("ids", "2")
                        .param("ids", "3")
                        .param("from", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(user1.getId()))
                .andExpect(jsonPath("$[0].name").value(user1.getName()))
                .andExpect(jsonPath("$[1].id").value(user2.getId()))
                .andExpect(jsonPath("$[1].name").value(user2.getName()));
    }

}
