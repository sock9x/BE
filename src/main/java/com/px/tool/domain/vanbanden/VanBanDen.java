package com.px.tool.domain.vanbanden;

import com.px.tool.domain.RequestType;
import com.px.tool.infrastructure.model.payload.EntityDefault;
import com.px.tool.infrastructure.utils.DateTimeUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
@Table(name = "van_ban_den")
public class VanBanDen extends EntityDefault {
    // @formatter:off
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long vbdId;

    @Column public String noiNhan;
    @Column @Type(type = "text") public String noiDung;
    @Column public String type;
    @Column @Enumerated public RequestType requestType;
    @Column private String soPa;
    @Column private Boolean read;
    @Column private Long folder;
    @Column private Long requestId; // save kh_id, dh_id, paId

    // @formatter:on
    public Long getFolder() {
        return folder == null ? 0 : folder;
    }

    public String getSoPa() {
        return soPa == null ? "" : soPa;
    }

    public Long getRequestId() {
        return Objects.isNull(requestId) ? -1L : requestId;
    }

    @PrePersist
    public void init() {
        this.setCreatedAt(DateTimeUtils.nowAsMilliSec());
    }
}
