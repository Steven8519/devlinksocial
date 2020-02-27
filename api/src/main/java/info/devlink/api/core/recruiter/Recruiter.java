package info.devlink.api.core.recruiter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class Recruiter {
    private int developerId;
    private int recruiterId;
    private String recruiterName;
    private String recruitingCompany;
    private String companyRating;
    private String serviceAddress;

    public Recruiter() {
        developerId = 0;
        recruiterId = 0;
        recruiterName = null;
        recruitingCompany = null;
        companyRating = null;
        serviceAddress = null;
    }
}
