package info.devlink.api.core.developer;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

public interface DeveloperService {
    Developer createDeveloper(@RequestBody Developer body);

    /**
     * Sample usage: curl $HOST:$PORT/developer/1
     *
     * @param developerId
     * @return the developer, if found, else null
     */
    @GetMapping(
            value    = "/developer/{developerId}",
            produces = "application/json")
    Mono<Developer> getDeveloper(@PathVariable int developerId);

    void deleteDeveloper(@PathVariable int developerId);
}
