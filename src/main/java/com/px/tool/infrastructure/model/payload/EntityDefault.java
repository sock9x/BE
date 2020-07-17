package com.px.tool.infrastructure.model.payload;

import com.px.tool.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import static com.px.tool.infrastructure.utils.DateTimeUtils.nowAsMilliSec;

@Getter
@Setter
@MappedSuperclass
public abstract class EntityDefault extends AbstractObject {

    @Column
    private Long createdAt;

    @Column
    private Long createdBy;

    @Column
    private Long updatedAt;

    @Column
    private Long updatedBy;

    @Column
    private Boolean deleted = false;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = nowAsMilliSec();
        try {
            this.updatedBy = ((User) SecurityContextHolder.getContext().getAuthentication().getDetails()).getUserId();
        } catch (Exception e) {
        }
    }

    @PrePersist
    protected void onSave() {
        this.createdAt = nowAsMilliSec();
        this.createdAt = nowAsMilliSec();
        try {
            this.createdBy = ((User) SecurityContextHolder.getContext().getAuthentication().getDetails()).getUserId();
            this.updatedBy = ((User) SecurityContextHolder.getContext().getAuthentication().getDetails()).getUserId();
        } catch (Exception e) {
        }
    }

    public Boolean getDeleted() {
        return deleted == null ? false : deleted;
    }

    public Long getCreatedAt() {
        return createdAt == null ? 0 : createdAt;
    }
}
