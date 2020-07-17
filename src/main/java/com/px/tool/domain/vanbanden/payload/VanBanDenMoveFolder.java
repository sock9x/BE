package com.px.tool.domain.vanbanden.payload;

import com.px.tool.infrastructure.model.payload.AbstractObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VanBanDenMoveFolder extends AbstractObject {
    private Long folderId;
    private Long vbdId;
}
