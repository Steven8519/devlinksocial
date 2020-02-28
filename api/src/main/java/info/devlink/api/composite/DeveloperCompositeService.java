package info.devlink.api.composite;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Api
public interface DeveloperCompositeService {

    /**
     * Sample usage:
     *
     * curl -X POST $HOST:$PORT/developer-composite \
     *   -H "Content-Type: application/json" --data
     *
     * @param body
     */
    @ApiOperation(
            value = "${api.developer-composite.create-composite-developer.description}",
            notes = "${api.developer-composite.create-composite-developer.notes}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.")
    })
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
    @ApiOperation(
            value = "${api.developer-composite.get-composite-developer.description}",
            notes = "${api.developer-composite.get-composite-developer.notes}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 404, message = "Not found, the specified id does not exist."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.")
    })
    @GetMapping(
            value    = "/developer-composite/{developerId}",
            produces = "application/json")
    Mono<DeveloperAggregate> getCompositeDeveloper(@PathVariable int developerId);


    /**
     * Sample usage:
     *
     * curl -X DELETE $HOST:$PORT/developer-composite/1
     *
     * @param developerId
     */
    @ApiOperation(
            value = "${api.developer-composite.delete-composite-developer.description}",
            notes = "${api.developer-composite.delete-composite-developer.notes}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.")
    })
    @DeleteMapping(value = "/developer-composite/{developerId}")
    void deleteCompositeDeveloper(@PathVariable int developerId);



}
