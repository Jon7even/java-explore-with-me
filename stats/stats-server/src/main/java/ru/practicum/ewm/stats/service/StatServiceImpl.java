package ru.practicum.ewm.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.dto.HitCreateTO;
import ru.practicum.ewm.stats.dto.HitResponseTO;
import ru.practicum.ewm.stats.dto.RequestStatListTO;
import ru.practicum.ewm.stats.mapper.StatMapper;
import ru.practicum.ewm.stats.model.HitRequest;
import ru.practicum.ewm.stats.projections.HitTO;
import ru.practicum.ewm.stats.repository.StatRepository;
import ru.practicum.ewm.stats.utils.ConverterRequest;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    private final StatRepository repository;

    @Transactional
    @Override
    public void createHit(HitCreateTO hitCreateTO) {
        log.debug("New hitTO came from controller [hitCreateTO={}]", hitCreateTO);
        repository.save(StatMapper.toHitEntity(hitCreateTO));
        log.debug("New Hit was successfully saved");
    }

    @Override
    public List<HitResponseTO> getStats(RequestStatListTO requestStatListTO) {
        log.debug("Get request [requestStatListTO={}]", requestStatListTO);
        List<HitTO> listHits = Collections.emptyList();

        HitRequest request = ConverterRequest.getHitRequest(requestStatListTO);

        switch (request) {
            case ALL_STATS:
                listHits = repository.findAllStatsByStartTimeAndEndTime(
                        requestStatListTO.getStart(), requestStatListTO.getEnd()
                );
                break;
            case ALL_UNIQUE_STATS:
                listHits = repository.findAllUniqueStatsByStartTimeAndEndTime(
                        requestStatListTO.getStart(), requestStatListTO.getEnd()
                );
                break;
            case URI_STATS:
                listHits = repository.findAllStatsByUriAndStartTimeAndEndTime(
                        requestStatListTO.getStart(), requestStatListTO.getEnd(), requestStatListTO.getUris()
                );
                break;
            case URI_UNIQUE_STATS:
                listHits = repository.findAllUniqueStatsByUriAndStartTimeAndEndTime(
                        requestStatListTO.getStart(), requestStatListTO.getEnd(), requestStatListTO.getUris()
                );
                break;
            default:
                log.error("Unknown detection error of [HitRequest={}]", request);
                break;
        }

        if (listHits.isEmpty()) {
            log.debug("Has returned empty list Hits from Repository");
        } else {
            log.debug("Found list Hits [count={}] from Repository", listHits.size());
        }

        return listHits.stream().map(StatMapper::toDTOFromProjections).collect(Collectors.toList());
    }

}
