package com.px.tool.domain.user.payload;

import com.px.tool.domain.user.Role;
import com.px.tool.infrastructure.model.payload.AbstractObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RolePayLoad extends AbstractObject {
    private long roleId;

    private String authority;

    public static RolePayLoad fromEntity(Role role) {
        RolePayLoad payload = new RolePayLoad();
        payload.roleId = role.getRoleId();
        payload.authority = role.getAuthority();
        return payload;
    }
}
