package ru.practicum.ewm.rating.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Builder
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "event_rating", schema = "public")
public class RatingId implements Serializable {
    @Column(name = "liker_id")
    private Long likerId;

    @Column(name = "event_id")
    private Long eventId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RatingId)) return false;
        RatingId ratingId = (RatingId) o;
        return Objects.equals(likerId, ratingId.likerId) && Objects.equals(eventId, ratingId.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(likerId, eventId);
    }
}
