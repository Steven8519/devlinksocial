package info.devlink.api.composite;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@AllArgsConstructor
@Getter
public class DeveloperAggregate {
    private final int developerId;
    private final String firstName;
    private final String lastName;
    private final String developerType;
    private final List<RecruiterSummary> recruiters;
    private final List<ContactSummary> contacts;
    private final ServiceAddresses serviceAddresses;

    public DeveloperAggregate() {
        developerId = 0;
        firstName = null;
        lastName = null;
        developerType = null;
        recruiters = null;
        contacts = null;
        serviceAddresses = null;
    }

}
