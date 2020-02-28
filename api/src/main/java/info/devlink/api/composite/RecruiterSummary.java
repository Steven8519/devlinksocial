package info.devlink.api.composite;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class RecruiterSummary {
    private final int recruiterId;
    private final String recruiterName;
    private final String recruitingCompany;
    private final String companyRating;

    public RecruiterSummary() {
        this.recruiterId = 0;
        this.recruiterName = null;
        this.recruitingCompany = null;
        this.companyRating = null;
    }
}
