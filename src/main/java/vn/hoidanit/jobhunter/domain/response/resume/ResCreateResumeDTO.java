package vn.hoidanit.jobhunter.domain.response.resume;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResCreateResumeDTO {
    private long id;
    private String createdBy;
    private Instant createdAt;

    public ResCreateResumeDTO(long id, String createdBy, Instant createdAt) {
        this.id = id;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }
}
