package ru.practicum.ewm.users.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.users.dto.NewUserRequest;
import ru.practicum.ewm.users.dto.UserDto;
import ru.practicum.ewm.users.model.UserEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "newUserRequest.name", target = "name")
    @Mapping(source = "newUserRequest.email", target = "email")
    UserEntity toEntityFromDTOCreate(NewUserRequest newUserRequest);

    @Mapping(source = "userEntity.id", target = "id")
    @Mapping(source = "userEntity.name", target = "name")
    @Mapping(source = "userEntity.email", target = "email")
    UserDto toDTOResponseFromEntity(UserEntity userEntity);

    @Mapping(source = "userEntity.id", target = "id")
    @Mapping(source = "userEntity.name", target = "name")
    @Mapping(source = "userEntity.email", target = "email")
    List<UserDto> toDTOResponseFromEntityList(List<UserEntity> listUsers);

}
