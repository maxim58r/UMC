package ru.mts.media.platform.umc.dao.postgres.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EventPgRepository extends JpaRepository<EventPgEntity, UUID> {
    List<EventPgEntity> findByVenues_ReferenceId(
            @Param("referenceId") String referenceId,
            Pageable pageable
    );
}
