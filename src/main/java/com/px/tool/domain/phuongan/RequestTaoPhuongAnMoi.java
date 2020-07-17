package com.px.tool.domain.phuongan;

import com.px.tool.infrastructure.model.payload.AbstractObject;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestTaoPhuongAnMoi extends AbstractObject {
    private List<Long> detailIds;
}
