package info.devlink.api.core.developer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class Developer {
    private final int developerId;
    private final String firstName;
    private final String lastName;
    private final String developerType;
    private final String serviceAddress;

    public Developer() {
        developerId = 0;
        firstName = null;
        lastName = null;
        developerType = null;
        serviceAddress = null;
    }
}
