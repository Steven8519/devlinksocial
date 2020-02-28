package info.devlink.microservices.core.recruiter.domain;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
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
    private String name;
    private String recruitingCompany;
    private String companyRating;

    public RecruiterEntity(int developerId, int recruiterId, String name, String recruitingCompany, String companyRating) {
        this.developerId = developerId;
        this.recruiterId = recruiterId;
        this.name = name;
        this.recruitingCompany = recruitingCompany;
        this.companyRating = companyRating;
    }


}
