/*
 * Copyright (c) 2023 Graduate. Yandex Practicum. All rights reserved.
 *
 * DangerZone!!!
 *
 * DEFAULT_CONSTRAIN_TIME
 * Задает отрезок времени, который используется при входящей проверке
 * DTO - NewEventDto на контроллер EventPrivateController
 * Валидирует поле времени eventDate. Работает следующим образом:
 * задается ограничение возможности создания [события] время которого
 * не может быть раньше на отрезок времени DEFAULT_CONSTRAIN_TIME
 *
 *
 * DEFAULT_INITIAL_STATE
 * Задает начальный статус при создании нового [события]
 * Добавляется в сервисе EventServiceImpl автоматически и
 * присваивает этот статус всем новым событиям
 *
 *
 * DEFAULT_FIELD_PAID
 * Задает поле по умолчанию в сущности EventEntity
 * Событие платное?
 *
 *
 * DEFAULT_FIELD_RQS_MODERATION
 * Задает поле по умолчанию в сущности EventEntity
 * Требуется ли модерация?
 *
 *
 * DEFAULT_FIELD_PARTICIPANT
 * Задает поле по умолчанию в сущности EventEntity
 * Ограничивает количество участников
 *
 *
 * DEFAULT_MONTHS_COUNT
 * Если администратор не указал свой диапазон времени,
 * Срабатывает дефолтный диапазон поиска по схеме:
 * текущее время + COUNT
 *
 *
 * DATA_SEARCH_HITS
 * Дата запуска проекта в прод
 * Именно с этой даты сервер статистики будет искать
 * количество просмотров URI
 *
 *
 */

package ru.practicum.ewm.config;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.events.model.EventState;

import java.time.Duration;
import java.time.LocalDateTime;

@UtilityClass
public class CommonConfig {
    public static final Duration DEFAULT_CONSTRAIN_TIME = Duration.ofHours(2);

    public static final Duration ADMIN_CONSTRAIN_TIME = Duration.ofHours(1);

    public static final EventState DEFAULT_INITIAL_STATE = EventState.PENDING;

    public static final boolean DEFAULT_FIELD_PAID = false;

    public static final boolean DEFAULT_FIELD_RQS_MODERATION = true;

    public static final int DEFAULT_FIELD_PARTICIPANT = 0;

    public static final int DEFAULT_MONTHS_COUNT = 3;

    public static final LocalDateTime DATA_SEARCH_HITS = LocalDateTime.of(2023, 1, 1, 0, 0);
}
