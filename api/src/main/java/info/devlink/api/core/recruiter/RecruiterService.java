package info.devlink.api.core.recruiter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;

public interface RecruiterService {
    Recruiter createRecruiter(@RequestBody Recruiter body);

    /**
     * Sample usage:
     *
     * curl $HOST:$PORT/recruiter?developerId=1
     *
     * @param developerId
     * @return
     */
    @GetMapping(
            value    = "/recruiter",
            produces = "application/json")
    Flux<Recruiter> getRecruiters(@RequestParam(value = "developerId", required = true) int developerId);

    void deleteRecruiters(@RequestParam(value = "developerId", required = true)  int developerId);
}
