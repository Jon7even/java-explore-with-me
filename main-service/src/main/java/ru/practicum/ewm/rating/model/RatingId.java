package ru.practicum.ewm.rating.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.ewm.events.model.EventEntity;
import ru.practicum.ewm.users.model.UserEntity;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "event_rating", schema = "public")
public class RatingId implements Serializable {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "liker_id", nullable = false)
    @ToString.Exclude
    private UserEntity liker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    @ToString.Exclude
    private EventEntity event;
}
