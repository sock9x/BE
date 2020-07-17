package com.px.tool.domain.request.payload;

import com.google.common.collect.ImmutableList;
import com.px.tool.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class PhanXuongPayload {
    public static List<PhanXuongPayload> emptyList = ImmutableList.of(emptyObject());

    private Long id;
    private String name;

    public static PhanXuongPayload fromUserEntity(User user) {
        PhanXuongPayload payload = new PhanXuongPayload();
        payload.id = user.getPhongBan().getPhongBanId();
        payload.name = user.getAlias();
        return payload;
    }

    public static PhanXuongPayload emptyObject() {
        PhanXuongPayload payload = new PhanXuongPayload();
        payload.id = -1L;
        payload.name = "";
        return payload;
    }
}
