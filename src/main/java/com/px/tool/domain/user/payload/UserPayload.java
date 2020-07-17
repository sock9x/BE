package com.px.tool.domain.user.payload;

import com.px.tool.domain.user.User;
import com.px.tool.infrastructure.model.payload.AbstractObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserPayload extends AbstractObject {
    public Long userId;
    private String email;
    private String signImg;
    private String fullName;
    // external fields for [admin-page]-list-users
    private String phanXuong;
    private String level;
    private String chucVu;
    private UserType type;

    public static UserPayload fromEntity(User user) {
        UserPayload payload = new UserPayload();
        payload.userId = user.getUserId();
        payload.email = user.getEmail();
        payload.signImg = user.getSignImg();
        payload.fullName = user.getFullName();
        payload.level = String.valueOf(user.getLevel());
        if (user.getUserId().equals(1L)) {
            payload.type = UserType.ADMIN;
        } else if (user.getUserId().equals(27L)) {
            payload.type = UserType.VAN_THU_BAO_MAT;
        } else if (user.isNhanVienVatTu()) {
            payload.type = UserType.NV_VAT_TU;
        } else if (user.isTruongPhongVatTu()) {
            payload.type = UserType.TP_VAT_TU;
        } else if (user.isNhanVienDinhMuc()) {
            payload.type = UserType.NV_DINH_MUC;
        } else if (user.isTroLyKT()) {
            payload.type = UserType.TL_KY_THUAT;
        } else if (user.isTruongPhongKTHK()) {
            payload.type = UserType.TRUONG_PHONG;
        } else if (user.isToTruong()) {
            payload.type = UserType.TO_TRUONG;
        } else if (user.isNhanVienKCS()) {
            payload.type = UserType.NV_KCS;
        } else if (user.isTruongPhongKeHoach()) {
            payload.type = UserType.TP_KE_HOACH;
        } else if (user.isTruongPhongKCS()) {
            payload.type = UserType.TP_KCS;
        } else {
            payload.type = UserType.GENERAL;
        }
        try {
            payload.setPhanXuong(user.getPhongBan().getName());
        } catch (Exception e) {
        }
        payload.chucVu = user.getAlias();
        return payload;
    }

    public static UserPayload fromEntityNoImg(User user) {
        UserPayload payload = new UserPayload();
        payload.userId = user.getUserId();
        payload.email = user.getEmail();
        payload.fullName = user.getFullName();
        try {
            payload.phanXuong = user.getPhongBan().getName() == null ? "" : user.getPhongBan().getName();
            payload.level = String.valueOf(user.getLevel());
        } catch (Exception e) {
        }
        return payload;
    }
}
