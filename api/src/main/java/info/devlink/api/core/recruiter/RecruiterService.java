package info.devlink.api.core.recruiter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface RecruiterService {

    /**
     * Sample usage: curl $HOST:$PORT/recruiter?developerId=1
     *
     * @param developerId
     * @return
     */
    @GetMapping(
            value    = "/recruiter",
            produces = "application/json")
    List<Recruiter> getRecruiters(@RequestParam(value = "developerId", required = true) int developerId);
}
