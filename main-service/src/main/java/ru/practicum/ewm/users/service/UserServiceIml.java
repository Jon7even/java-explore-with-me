package ru.practicum.ewm.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.EntityNotDeletedException;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.exception.IntegrityConstraintException;
import ru.practicum.ewm.users.dto.NewUserRequest;
import ru.practicum.ewm.users.dto.UserDto;
import ru.practicum.ewm.users.mapper.UserMapper;
import ru.practicum.ewm.users.model.UserEntity;
import ru.practicum.ewm.users.repository.UserRepository;
import ru.practicum.ewm.utils.ConverterPage;

import java.util.List;
import java.util.Optional;

import static ru.practicum.ewm.constants.CommonSort.DEFAULT_SORT_BY_ID;
import static ru.practicum.ewm.constants.NamesExceptions.DUPLICATE_EMAIL;
import static ru.practicum.ewm.constants.NamesLogsInService.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceIml implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto createUser(NewUserRequest newUserRequest) {
        log.debug("New newUserRequest came {} [newUserRequest={}]", SERVICE_FROM_CONTROLLER, newUserRequest);
        UserEntity user = UserMapper.INSTANCE.toEntityFromDTOCreate(newUserRequest);

        checkEmailOnDuplicate(newUserRequest.getEmail());

        log.debug("Add new entity [user={}] {}", user, SERVICE_IN_DB);
        UserEntity createdUser = userRepository.save(user);

        log.debug("New user has returned [user={}] {}", createdUser, SERVICE_FROM_DB);
        return UserMapper.INSTANCE.toDTOResponseFromEntity(createdUser);
    }

    @Override
    public List<UserDto> getUsersByListId(List<Long> ids, Integer from, Integer size) {
        log.debug("Get all users {}", SERVICE_IN_DB);
        List<UserEntity> listUsers;
        Pageable pageable = ConverterPage.getPageRequest(from, size, Optional.of(DEFAULT_SORT_BY_ID));

        if (ids.isEmpty()) {
            listUsers = userRepository.findAll(pageable).getContent();
        } else {
            listUsers = userRepository.findAllByIdIn(ids, pageable);
        }

        if (listUsers.isEmpty()) {
            log.debug("Has returned empty list users {}", SERVICE_FROM_DB);
        } else {
            log.debug("Found list users [count={}] {}", listUsers.size(), SERVICE_FROM_DB);
        }

        return UserMapper.INSTANCE.toDTOResponseFromEntityList(listUsers);
    }

    @Transactional
    @Override
    public void deleteUserById(Long idUser) {
        existDoesUserEntityById(idUser);

        log.debug("Remove [idUser={}] {}", idUser, SERVICE_IN_DB);
        userRepository.deleteById(idUser);
        boolean isRemoved = userRepository.existsById(idUser);

        if (!isRemoved) {
            log.debug("User by [id={}] has removed {}", idUser, SERVICE_FROM_DB);
        } else {
            log.error("User by [id={}] was not removed", idUser);
            throw new EntityNotDeletedException(String.format("User with id=%d was not deleted", idUser));
        }
    }

    private void existDoesUserEntityById(Long idUser) {
        log.debug("Start check exist [idUser={}] {}", idUser, SERVICE_IN_DB);

        if (userRepository.existsById(idUser)) {
            log.debug("Check was successful found [idUser={}] {}", idUser, SERVICE_FROM_DB);
        } else {
            log.warn("User by [id={}] was not found", idUser);
            throw new EntityNotFoundException(String.format("User with id=%d was not found", idUser));
        }
    }

    private void checkEmailOnDuplicate(String email) {
        if (userRepository.existsByEmail(email)) {
            log.debug("Checked email={} fail: email already exist in DB", email);
            throw new IntegrityConstraintException(DUPLICATE_EMAIL);
        }
    }

}
