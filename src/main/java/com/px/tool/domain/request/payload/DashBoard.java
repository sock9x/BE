package com.px.tool.domain.request.payload;

import com.px.tool.domain.RequestStatus;
import com.px.tool.infrastructure.model.payload.AbstractObject;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DashBoard extends AbstractObject {
    protected String key;
    protected String noiDung;
    protected RequestStatus trangThaiRequest;


}
