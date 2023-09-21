package ru.practicum.ewm.events.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import ru.practicum.ewm.category.model.CategoryEntity;
import ru.practicum.ewm.rating.model.RatingEntity;
import ru.practicum.ewm.users.model.UserEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static ru.practicum.ewm.config.CommonConfig.*;

@Entity
@Builder
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "event", schema = "public")
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "annotation", nullable = false)
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @ToString.Exclude
    private CategoryEntity category;

    @Builder.Default
    @Column(name = "confirmed_requests", nullable = false)
    private Integer confirmedRequests = 0;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "initiator_id", nullable = false)
    @ToString.Exclude
    private UserEntity initiator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    @ToString.Exclude
    private LocationEntity location;

    @Builder.Default
    @Column(name = "paid", nullable = false)
    private Boolean paid = DEFAULT_FIELD_PAID;

    @Builder.Default
    @Column(name = "participant_limit", nullable = false)
    private Integer participantLimit = DEFAULT_FIELD_PARTICIPANT;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Builder.Default
    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration = DEFAULT_FIELD_RQS_MODERATION;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "state", nullable = false)
    private EventState state;

    @Column(name = "title", nullable = false)
    private String title;

    @Builder.Default
    @Column(name = "views", nullable = false)
    private Integer views = 0;

    @OneToMany(mappedBy = "id.liker", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Where(clause = "is_positive = true")
    @ToString.Exclude
    private Set<RatingEntity> like = new HashSet<>();

    @OneToMany(mappedBy = "id.liker", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Where(clause = "is_positive = false")
    @ToString.Exclude
    private Set<RatingEntity> disLike = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventEntity)) return false;
        return id != null && id.equals(((EventEntity) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
