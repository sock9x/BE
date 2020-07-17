package com.px.tool.domain.request.payload;

import com.px.tool.domain.RequestType;
import com.px.tool.infrastructure.model.payload.AbstractObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestDeleteObject extends AbstractObject {
    private Long id;
    private RequestType type;
}
