package ru.practicum.ewm.rating.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "event_rating", schema = "public")
public class RatingEntity {
    @EmbeddedId
    private RatingId id;

    @Column(name = "is_positive", nullable = false)
    private Boolean is_positive;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;
}
