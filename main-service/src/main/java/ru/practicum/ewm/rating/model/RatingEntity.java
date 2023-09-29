package ru.practicum.ewm.rating.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.events.model.EventEntity;
import ru.practicum.ewm.users.model.UserEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.EmbeddedId;
import javax.persistence.MapsId;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Builder
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "event_rating", schema = "public")
public class RatingEntity {
    @EmbeddedId
    private RatingId id;

    @ManyToOne
    @MapsId("likerId")
    @JoinColumn(name = "liker_id")
    @ToString.Exclude
    private UserEntity liker;

    @ManyToOne
    @MapsId("eventId")
    @JoinColumn(name = "event_id")
    @ToString.Exclude
    private EventEntity event;

    @Column(name = "is_positive", nullable = false)
    private Boolean isPositive;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RatingEntity)) return false;
        RatingEntity rating = (RatingEntity) o;
        return Objects.equals(id, rating.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
