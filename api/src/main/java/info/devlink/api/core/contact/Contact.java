package info.devlink.api.core.contact;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class Contact {
    private int developerId;
    private int recruiterId;
    private int contactId;
    private String email;
    private String phoneNumber;
    private String serviceAddress;

    public Contact() {
        developerId = 0;
        recruiterId = 0;
        contactId = 0;
        email = null;
        phoneNumber = null;
        serviceAddress = null;
    }
}
