package com.px.tool.domain.request.payload;

import com.px.tool.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ToSXPayload {
    private Long id;
    private String name;

    public static ToSXPayload fromUserEntity(User user) {
        ToSXPayload payload = new ToSXPayload();
        payload.id = user.getUserId();
        payload.name = user.getAlias();
        return payload;
    }
}
