package info.devlink.api.composite;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class RecruiterSummary {
    private final int RecruiterId;
    private final String recruiterName;
    private final String recruiterCompany;
    private final String companyRating;


}
