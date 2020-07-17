package com.px.tool.domain.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NguoiDangXuLy {
    private Long nguoiLap;
    private Long tpVatTu;
    private Long tpKeHoach;
    private Long tpKTHK;
    private Long giamDoc;

    public void setGiamDoc(Object object) {
        if (object != null) {
            try {
                this.giamDoc = Long.valueOf(object.toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    public void setNguoiLap(Object object) {
        if (object != null) {
            try {
                this.nguoiLap = Long.valueOf(object.toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    public void setTpKTHK(Object object) {
        if (object != null) {
            try {
                this.tpKTHK = Long.valueOf(object.toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    public void setTpKeHoach(Object object) {
        if (object != null) {
            try {
                this.tpKeHoach = Long.valueOf(object.toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    public void setTpVatTu(Object object) {
        if (object != null) {
            try {
                this.tpVatTu = Long.valueOf(object.toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }
}
