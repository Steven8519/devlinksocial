package info.devlink.microservices.core.developer.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "developers")
public class Developer {

    @Id
    private String id;

    @Version
    private Integer version;

    @Indexed(unique = true)
    private int developerId;

    public Developer(int developerId, String firstName, String lastName, String developerType) {
        this.developerId = developerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.developerType = developerType;
    }

    private String firstName;

    private String lastName;

    private String developerType;
}
