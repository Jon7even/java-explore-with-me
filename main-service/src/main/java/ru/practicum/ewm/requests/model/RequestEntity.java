package ru.practicum.ewm.requests.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.events.model.EventEntity;
import ru.practicum.ewm.users.model.UserEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "request", schema = "public")
public class RequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "event_id", nullable = false)
    @ToString.Exclude
    private EventEntity event;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "requester_id", nullable = false)
    @ToString.Exclude
    private UserEntity requester;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RequestEntity)) return false;
        return id != null && id.equals(((RequestEntity) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
