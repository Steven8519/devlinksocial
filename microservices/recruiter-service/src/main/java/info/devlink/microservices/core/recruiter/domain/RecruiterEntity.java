package info.devlink.microservices.core.recruiter.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection="recruiters")
@CompoundIndex(name = "dev-rec-id", unique = true, def = "{'developerId': 1, 'recruiterId' : 1}")
public class RecruiterEntity {

    @Id
    private String id;

    @Version
    private Integer version;

    private int developerId;
    private int recruiterId;
    private String recruiterName;
    private String recruitingCompany;
    private String companyRating;
}
