package com.px.tool.domain.request.payload;

import com.px.tool.infrastructure.model.payload.AbstractPageResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageDashBoardCongViecCuaToi extends AbstractPageResponse<DashBoardCongViecCuaToi> {
    public PageDashBoardCongViecCuaToi(Integer page, Integer size) {
        super(page, size);
    }

    public void setPage(Integer page) {
        if (page == 0) {
            this.page = 1;
        } else {
            this.page = page;
        }
    }
}
