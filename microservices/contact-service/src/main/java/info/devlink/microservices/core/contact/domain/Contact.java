package info.devlink.microservices.core.contact.domain;

import lombok.*;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "contacts", indexes = { @Index(name = "contacts_unique_idx", unique = true, columnList = "developerId,contactId") })
public class Contact {

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
}
