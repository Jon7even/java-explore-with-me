package ru.practicum.ewm.stats.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.stats.dto.HitCreateTO;
import ru.practicum.ewm.stats.dto.RequestStatListTO;

import java.util.Map;

import static ru.practicum.ewm.stats.dto.constants.Constants.DEFAULT_TIME_FORMAT;

@Service
public class StatClient extends BaseClient {
    @Autowired
    public StatClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createHit(HitCreateTO hitCreateTO) {
        return post("/hit", hitCreateTO);
    }

    public ResponseEntity<Object> getStats(RequestStatListTO requestStatListTO) {

        if (requestStatListTO.getUris() == null || requestStatListTO.getUris().isEmpty()) {
            Map<String, Object> parameters = Map.of(
                    "start", requestStatListTO.getStart().format(DEFAULT_TIME_FORMAT),
                    "end", requestStatListTO.getEnd().format(DEFAULT_TIME_FORMAT),
                    "unique", requestStatListTO.getUnique()
            );
            return get("/stats?start={start}&end={end}&unique={unique}", parameters);
        } else {
            Map<String, Object> parameters = Map.of(
                    "start", requestStatListTO.getStart().format(DEFAULT_TIME_FORMAT),
                    "end", requestStatListTO.getEnd().format(DEFAULT_TIME_FORMAT),
                    "uris", String.join(",", requestStatListTO.getUris()),
                    "unique", requestStatListTO.getUnique()
            );
            return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
        }
    }

}
