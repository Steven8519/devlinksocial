package info.devlink.api.composite;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class ContactSummary {
    private final int contactId;
    private final String email;
    private final String phoneNumber;
}
