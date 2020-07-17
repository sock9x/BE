package com.px.tool.infrastructure.model.payload;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class AbstractPageResponse<O> extends AbstractObject {
    protected List<O> details;
    protected Integer page;
    protected Integer size;
    private Long total;

    public AbstractPageResponse(Integer page, Integer size) {
        super();
        this.page = page;
        this.size = size;
    }

    private AbstractPageResponse() {
        this.details = new ArrayList<>();
        this.total = 0L;
    }

    public void setPage(Integer page) {
        if (page == 0) {
            this.page = 1;
        } else {
            this.page = page;
        }
    }

    public Long getTotal() {
        try {
            return size == 0 ? 1 : (long) Math.ceil((double) total / (double) size);
        } catch (Exception e) {
            return 0L;
        }
    }

}
