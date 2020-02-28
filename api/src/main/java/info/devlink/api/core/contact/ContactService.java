package info.devlink.api.core.contact;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;

public interface ContactService {

    Contact createContact(@RequestBody Contact body);

    /**
     * Sample usage: curl $HOST:$PORT/contact?developerId=1
     *
     * @param developerId
     * @return
     */
    @GetMapping(
            value    = "/contact",
            produces = "application/json")
    Flux<Contact> getContacts(@RequestParam(value = "developerId", required = true) int developerId);

    void deleteContacts(@RequestParam(value = "developerId", required = true)  int developerId);

}
