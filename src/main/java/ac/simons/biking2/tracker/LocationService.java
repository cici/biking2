/*
 * Copyright 2014-2016 michael-simons.eu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ac.simons.biking2.tracker;

import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.List;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import static java.time.ZoneId.systemDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michael J. Simons, 2014-03-20
 */
@Service
public class LocationService {

    public static final Logger LOGGER = LoggerFactory.getLogger(LocationService.class.getPackage().getName());

    private final LocationRepository locationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public LocationService(final LocationRepository locationRepository, final SimpMessagingTemplate messagingTemplate) {
        this.locationRepository = locationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public LocationEntity createAndSendNewLocation(final NewLocationCmd newLocation) {
        final LocationEntity location = this.locationRepository.save(new LocationEntity(newLocation.getLatitude(), newLocation.getLongitude(), newLocation.getCreatedAt()));
        this.messagingTemplate.convertAndSend("/topic/currentLocation", location);
        return location;
    }

    public List<LocationEntity> getLocationsForTheLastNHours(final int hours) {
        return locationRepository.findByCreatedAtGreaterThanOrderByCreatedAtAsc(GregorianCalendar.from(ZonedDateTime.now(systemDefault()).minusHours(hours)));
    }

    /**
     * @return The total number of locations tracked
     */
    public long getLocationCount() {
        return locationRepository.count();
    }
}
