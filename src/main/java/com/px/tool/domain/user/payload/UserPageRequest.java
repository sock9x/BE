package com.px.tool.domain.user.payload;

import com.px.tool.infrastructure.model.payload.PageRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPageRequest extends PageRequest {
    public UserPageRequest(Integer page, Integer size) {
        super(page, size);
    }
}
