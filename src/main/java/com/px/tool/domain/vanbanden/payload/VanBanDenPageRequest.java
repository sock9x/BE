package com.px.tool.domain.vanbanden.payload;

import com.px.tool.domain.RequestType;
import com.px.tool.infrastructure.model.payload.PageRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

@Getter
@Setter
@NoArgsConstructor
public class VanBanDenPageRequest extends PageRequest {
    private Long userId;
    private Long date;
    private String soVb;
    private Long folder;
    private RequestType loaiVb;

    public VanBanDenPageRequest(Integer page, Integer size) {
        super(page, size);
    }

    public void setFolder(Long folder) {
        if (folder != null) {
            this.folder = folder < 1 ? null : folder;
        }

    }

    @Override
    public org.springframework.data.domain.PageRequest toPageRequest() {
        return org.springframework.data.domain.PageRequest.of(page, size, Sort.by("createdAt").descending());
    }
}
