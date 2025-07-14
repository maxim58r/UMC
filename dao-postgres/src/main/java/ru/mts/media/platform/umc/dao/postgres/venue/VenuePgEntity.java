package ru.mts.media.platform.umc.dao.postgres.venue;

import jakarta.persistence.*;
import lombok.Data;
import ru.mts.media.platform.umc.dao.postgres.common.FullExternalIdPk;
import ru.mts.media.platform.umc.dao.postgres.event.EventPgEntity;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@IdClass(FullExternalIdPk.class)
@Table(name = "venue",
        indexes = {
                @Index(name = "idx_venue_referenceId",
                        columnList = "reference_id",
                        unique = true)
        })
public class VenuePgEntity {
    @Id
    @Column(name = "brand")
    private String brand;

    @Id
    @Column(name = "provider")
    private String provider;

    @Id
    @Column(name = "external_id")
    private String externalId;

    @Column(name = "reference_id")
    private String referenceId;

    @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "venues")
    private Set<EventPgEntity> events = new HashSet<>();
}
