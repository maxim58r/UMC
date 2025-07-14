CREATE TABLE event (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       name VARCHAR(255) NOT NULL,
                       start_time TIMESTAMP NOT NULL,
                       end_time TIMESTAMP NOT NULL
);

CREATE TABLE venue_event (
                             event_id UUID NOT NULL,
                             venue_brand VARCHAR(255) NOT NULL,
                             venue_provider VARCHAR(255) NOT NULL,
                             venue_external_id VARCHAR(255) NOT NULL,
                             PRIMARY KEY (event_id, venue_brand, venue_provider, venue_external_id),
                             FOREIGN KEY (event_id) REFERENCES event(id),
                             FOREIGN KEY (venue_brand, venue_provider, venue_external_id) REFERENCES venue(brand, provider, external_id)
);

-- Индекс для поиска событий по venue
CREATE INDEX idx_venue_event_venue ON venue_event (venue_brand, venue_provider, venue_external_id);

-- Индекс для поиска площадок по событию
CREATE INDEX idx_venue_event_event ON venue_event (event_id);