package com.px.tool.domain.user.payload;

import com.px.tool.domain.user.User;
import com.px.tool.infrastructure.model.payload.AbstractObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailResponse extends AbstractObject {
    public Long userId;
    private String email;
    private String fullName;

    private Long phanXuong;
    private Long level;
    private String alias;

    public static UserDetailResponse fromEntity(User user) {
        UserDetailResponse response = new UserDetailResponse();
        try {
            response.userId = user.getUserId();
            response.email = user.getEmail();
            response.fullName = user.getFullName();
            response.phanXuong = user.getPhongBan().getPhongBanId();
            response.level = user.getAuthorities()
                    .stream()
                    .findFirst().orElse(null)
                    .getRoleId();
            response.alias = user.getAlias();
        } catch (Exception e) {

        }
        return response;
    }
}
