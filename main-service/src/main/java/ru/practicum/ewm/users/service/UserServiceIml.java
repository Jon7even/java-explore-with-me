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
        List<UserEntity> listUsers;
        Pageable pageable = ConverterPage.getPageRequest(from, size, Optional.of(DEFAULT_SORT_BY_ID));

        log.debug("Get list users by [ids={}] and pages {}", ids, SERVICE_IN_DB);
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
    public void deleteUserById(Long userId) {
        existDoesUserEntityById(userId);

        log.debug("Remove [userId={}] {}", userId, SERVICE_IN_DB);
        userRepository.deleteById(userId);
        boolean isRemoved = userRepository.existsById(userId);

        if (!isRemoved) {
            log.debug("User by [userId={}] has removed {}", userId, SERVICE_FROM_DB);
        } else {
            log.error("User by [userId={}] was not removed", userId);
            throw new EntityNotDeletedException(String.format("User with id=%d was not deleted", userId));
        }
    }

    private void existDoesUserEntityById(Long userId) {
        log.debug("Start check exist [userId={}] {}", userId, SERVICE_IN_DB);

        if (userRepository.existsById(userId)) {
            log.debug("Check was successful found [userId={}] {}", userId, SERVICE_FROM_DB);
        } else {
            log.warn("User by [userId={}] was not found", userId);
            throw new EntityNotFoundException(String.format("User with id=%d was not found", userId));
        }
    }

    private void checkEmailOnDuplicate(String email) {
        if (userRepository.existsByEmail(email)) {
            log.debug("Checked email={} fail: email already exist in DB", email);
            throw new IntegrityConstraintException(DUPLICATE_EMAIL);
        }
    }

}
