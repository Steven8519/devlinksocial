package info.devlink.api.composite;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class ServiceAddresses {
    private final String compositeAddress;
    private final String developerAddress;
    private final String contactAddress;
    private final String recruiterAddress;

    public ServiceAddresses() {
        compositeAddress = null;
        developerAddress = null;
        contactAddress = null;
        recruiterAddress = null;
    }
}
