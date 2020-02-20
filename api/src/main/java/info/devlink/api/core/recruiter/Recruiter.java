package info.devlink.api.core.recruiter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class Recruiter {
    private final int developerId;
    private final int recruiterId;
    private final String recruiterName;
    private final String recruitingCompany;
    private final String companyRating;
    private final String serviceAddress;

    public Recruiter() {
        developerId = 0;
        recruiterId = 0;
        recruiterName = null;
        recruitingCompany = null;
        companyRating = null;
        serviceAddress = null;
    }
}
