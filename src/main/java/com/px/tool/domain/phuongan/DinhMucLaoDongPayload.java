package com.px.tool.domain.phuongan;

import com.px.tool.infrastructure.model.payload.AbstractObject;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.Objects;

@Getter
@Setter
public class DinhMucLaoDongPayload extends AbstractObject {
    public Long dmId;

    private String tt;

    private String noiDungCongViec;

    private String bacCV;

    private String dm;

    private String ghiChu;

    public static DinhMucLaoDongPayload fromEntity(DinhMucLaoDong dinhMucLaoDong) {
        DinhMucLaoDongPayload dinhMucLaoDongPayload = new DinhMucLaoDongPayload();
        BeanUtils.copyProperties(dinhMucLaoDong, dinhMucLaoDongPayload);
        return dinhMucLaoDongPayload;
    }

    public DinhMucLaoDong toEntity() {
        DinhMucLaoDong dinhMucLaoDong = new DinhMucLaoDong();
        if (dmId != null && dmId <= 0) {
            dmId = null;
        }
        BeanUtils.copyProperties(this, dinhMucLaoDong);
        return dinhMucLaoDong;
    }

    public boolean isInvalidData() {
        return Objects.isNull(noiDungCongViec) || Objects.isNull(bacCV);
    }
}
