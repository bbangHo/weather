package org.pknu.weather.repository;

import org.pknu.weather.domain.Location;

import java.util.List;

public interface LocationCustomRepository {
    List<Location> getRainProbability(Location locationEntity);

}
