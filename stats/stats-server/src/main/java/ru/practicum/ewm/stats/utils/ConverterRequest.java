package ru.practicum.ewm.stats.utils;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.stats.dto.RequestStatListTO;
import ru.practicum.ewm.stats.model.HitRequest;

@UtilityClass
public class ConverterRequest {
    public HitRequest getHitRequest(RequestStatListTO requestStatListTO) {
        if (requestStatListTO.isUnique()) {

            if (requestStatListTO.getUris() != null && !requestStatListTO.getUris().isEmpty()) {
                return HitRequest.URI_UNIQUE_STATS;
            } else {
                return HitRequest.ALL_UNIQUE_STATS;
            }

        } else {

            if (requestStatListTO.getUris() != null && !requestStatListTO.getUris().isEmpty()) {
                return HitRequest.URI_STATS;
            } else {
                return HitRequest.ALL_STATS;
            }

        }
    }

}
