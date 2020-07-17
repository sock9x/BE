package com.px.tool.domain.user.payload;

import com.px.tool.infrastructure.model.payload.AbstractPageResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPageResponse extends AbstractPageResponse<UserPayload> {

    public UserPageResponse(Integer page, Integer size) {
        super(page, size);
    }
}
