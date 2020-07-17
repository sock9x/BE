package com.px.tool.infrastructure.model.payload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PageRequest extends AbstractObject {
    protected Integer page;
    protected Integer size;

    public PageRequest(Integer page, Integer size) {
        setPage(page);
        this.size = size;
    }

    public void setPage(Integer page) {
        if (page == null || page <= 0) {
            this.page = 0;
        } else {
            this.page = page - 1;
        }
    }

    public org.springframework.data.domain.PageRequest toPageRequest() {
        return org.springframework.data.domain.PageRequest.of(page, size);
    }
}
