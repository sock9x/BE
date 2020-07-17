package com.px.tool.domain.user.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.px.tool.domain.user.User;
import com.px.tool.infrastructure.model.payload.AbstractObject;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Setter
@Getter
public class UserRequest extends AbstractObject {
    // @formatter:off
    private Long userId;
    private String email;

    @JsonProperty("password")
    private String password;
    private String imgBase64;


    @NotNull private Integer level;
    @NotEmpty(message = "Full name không đc để trống.")
    private String fullName;

    @NotNull private Long phanXuong;
    @NotEmpty(message = "Tên chức vụ không được để trống.") private String alias;

    @Deprecated private Long phongBanId;
    // @formatter:on
    public Integer getLevel() {
        return level != null ? level : 2; // defaullt thi setting role la giam doc, truong phong.
    }

    public User toUserEntity() {
        User entity = new User();
        entity.setUserId(userId);
        if (!StringUtils.isEmpty(email)) {
            entity.setEmail(email);
        }
        if (!StringUtils.isEmpty(imgBase64)) {
            entity.setSignImg(imgBase64);
        }
        if (!StringUtils.isEmpty(fullName)) {
            entity.setFullName(fullName);
        }
        return entity;
    }

    @Override
    public String toString() {
        return "UserRequest{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", level=" + level +
                ", fullName='" + fullName + '\'' +
                ", phanXuong=" + phanXuong +
                ", alias='" + alias + '\'' +
                '}';
    }
}
