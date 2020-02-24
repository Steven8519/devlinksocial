package info.devlink.api.composite;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@Getter
public class DeveloperAggregate {
    private final int developerId;
    private final String firstName;
    private final String lastName;
    private final String developerType;
    private final List<ContactSummary> contactSummaries;
    private final List<RecruiterSummary> recruiterSummaries;
    private final ServiceAddresses serviceAddresses;

}
