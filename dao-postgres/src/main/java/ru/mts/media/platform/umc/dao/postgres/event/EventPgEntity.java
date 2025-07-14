package ru.mts.media.platform.umc.dao.postgres.event;

import jakarta.persistence.*;
import lombok.*;
import ru.mts.media.platform.umc.dao.postgres.venue.VenuePgEntity;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "event")
public class EventPgEntity {
    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @ManyToMany
    @JoinTable(
            name = "venue_event",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = {
                    @JoinColumn(name = "venue_brand", referencedColumnName = "brand"),
                    @JoinColumn(name = "venue_provider", referencedColumnName = "provider"),
                    @JoinColumn(name = "venue_external_id", referencedColumnName = "external_id")
            }
    )
    private Set<VenuePgEntity> venues = new HashSet<>();
}
