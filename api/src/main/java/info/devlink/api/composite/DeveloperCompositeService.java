package info.devlink.api.composite;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface DeveloperCompositeService {

    /**
     * Sample usage: curl $HOST:$PORT/developer-composite/1
     *
     * @param developerId
     * @return the composite developer info, if found, else null
     */
    @GetMapping(
            value    = "/developer-composite/{developerId}",
            produces = "application/json")
    DeveloperAggregate getDeveloper(@PathVariable int developerId);
}
