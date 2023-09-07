package ru.practicum.ewm.setup;

import ru.practicum.ewm.users.dto.NewUserRequest;

public class GenericInitEntity {
    protected Long FIRST_ID = 1L;

    protected Long SECOND_ID = 2L;

    protected NewUserRequest firstNewUserRequest;

    protected NewUserRequest secondNewUserRequest;

    protected NewUserRequest thirdNewUserRequest;

    protected void initUserCreateTO() {
        firstNewUserRequest = NewUserRequest.builder()
                .email("firstEmail@test.ru")
                .name("firstUserTest")
                .build();
        secondNewUserRequest = NewUserRequest.builder()
                .email("secondEmail@test.ru")
                .name("secondUserTest")
                .build();
        thirdNewUserRequest = NewUserRequest.builder()
                .email("thirdEmail@test.ru")
                .name("thirdUserTest")
                .build();
    }

}
