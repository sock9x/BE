package com.px.tool.domain.user.payload;

import com.px.tool.domain.user.PhongBan;
import com.px.tool.infrastructure.model.payload.AbstractObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhongBanPayload extends AbstractObject {
    private Long id;
    private String name;

    public static PhongBanPayload fromEntity(PhongBan phongBan) {
        PhongBanPayload phongBanPayload = new PhongBanPayload();
        phongBanPayload.id = phongBan.getPhongBanId();
        phongBanPayload.name = phongBan.getName();
        return phongBanPayload;
    }
}
