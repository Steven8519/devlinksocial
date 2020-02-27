package info.devlink.microservices.core.contact.domain;

import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "contacts", indexes = { @Index(name = "contacts_unique_idx", unique = true, columnList = "developerId,contactId") })
public class ContactEntity {

    @Id
    @GeneratedValue
    private int id;

    @Version
    private int version;

    private int developerId;
    private int recruiterId;
    private int contactId;
    private String email;
    private String phoneNumber;

    public ContactEntity(int developerId, int recruiterId, int contactId, String email, String phoneNumber) {
        this.developerId = developerId;
        this.recruiterId = recruiterId;
        this.contactId = contactId;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
