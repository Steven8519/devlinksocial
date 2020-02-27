package info.devlink.api.core.developer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class Developer {
    private int developerId;
    private String firstName;
    private String lastName;
    private String developerType;
    private String serviceAddress;

    public Developer() {
        developerId = 0;
        firstName = null;
        lastName = null;
        developerType = null;
        serviceAddress = null;
    }
}
