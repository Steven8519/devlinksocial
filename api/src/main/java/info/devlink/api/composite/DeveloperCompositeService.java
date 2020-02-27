package info.devlink.api.composite;

import info.devlink.api.core.developer.Developer;
import org.springframework.web.bind.annotation.*;

public interface DeveloperCompositeService {

    /**
     * Sample usage:
     *
     * curl -X POST $HOST:$PORT/developer-composite \
     *   -H "Content-Type: application/json" --data
     *
     * @param body
     */
    @PostMapping(
            value    = "/developer-composite",
            consumes = "application/json")
    void createCompositeDeveloper(@RequestBody DeveloperAggregate body);

    /**
     * Sample usage: curl $HOST:$PORT/developer-composite/1
     *
     * @param developerId
     * @return the composite developer info, if found, else null
     */
    @GetMapping(
            value    = "/developer-composite/{developerId}",
            produces = "application/json")
    DeveloperAggregate getCompositeDeveloper(@PathVariable int developerId);


    /**
     * Sample usage:
     *
     * curl -X DELETE $HOST:$PORT/developer-composite/1
     *
     * @param developerId
     */
    @DeleteMapping(value = "/developer-composite/{developerId}")
    void deleteCompositeDeveloper(@PathVariable int developerId);



}
