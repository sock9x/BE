package com.px.tool.domain.request.payload;

import com.px.tool.domain.RequestType;
import com.px.tool.domain.cntp.CongNhanThanhPham;
import com.px.tool.domain.cntp.NoiDungThucHien;
import com.px.tool.domain.phuongan.PhuongAn;
import com.px.tool.domain.request.Request;
import com.px.tool.domain.user.User;
import com.px.tool.infrastructure.model.payload.AbstractObject;
import com.px.tool.infrastructure.utils.DateTimeUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Objects;

@Getter
@Setter
public class DashBoardCongViecCuaToi extends AbstractObject {
    private String ma;
    private long requestId;
    private String noiDung;
    private String status;
    private String ngayGui;
    private RequestType type;

    public static DashBoardCongViecCuaToi fromEntity(Request request, Map<Long, User> userById) {
        DashBoardCongViecCuaToi dashBoardCongViecCuaToi = new DashBoardCongViecCuaToi();
        dashBoardCongViecCuaToi.ma = "PKH-" + request.getRequestId();
        dashBoardCongViecCuaToi.requestId = request.getRequestId();
        dashBoardCongViecCuaToi.status = "Vừa tạo";
        dashBoardCongViecCuaToi.type = request.getType();
        try {
            if (dashBoardCongViecCuaToi.type == RequestType.KIEM_HONG) {
                dashBoardCongViecCuaToi.noiDung = "Gửi từ phân xưởng: " + getVal(userById, request.getKiemHong().getPhanXuong()) + " - Tổ sản xuất: " + getVal(userById, request.getKiemHong().getToSX());
                if (request.getKiemHong().getToTruongXacNhan()) {
                    dashBoardCongViecCuaToi.status = "Tổ trưởng đã ký";
                }
                if (request.getKiemHong().getTroLyKTXacNhan()) {
                    dashBoardCongViecCuaToi.status = "Trợ lý KT đã ký";
                }
                if (request.getKiemHong().getQuanDocXacNhan()) {
                    dashBoardCongViecCuaToi.status = "Quản đốc đã ký";
                }
            } else if (dashBoardCongViecCuaToi.type == RequestType.DAT_HANG) {
                dashBoardCongViecCuaToi.noiDung = request.getPhieuDatHang().getNoiDung();
                dashBoardCongViecCuaToi.ma = request.getPhieuDatHang().getSo();
                dashBoardCongViecCuaToi.status = "Vừa tạo";
                if (request.getPhieuDatHang().getNguoiDatHangXacNhan()) {
                    dashBoardCongViecCuaToi.status = "Người đặt hàng đã ký";
                }
                if (request.getPhieuDatHang().getTpvatTuXacNhan()) {
                    dashBoardCongViecCuaToi.status = "T.P Vật Tư đã ký";
                }
                if (request.getPhieuDatHang().getTpkthkXacNhan()) {
                    dashBoardCongViecCuaToi.status = "Đặt Hàng thành công";
//                     NOTE: [update] đặt hàng thành công thì không hiển thị ở đây nữa.
//                    return null;
                }
            }
        } catch (Exception ex) {
            // DO no thing
            ex.printStackTrace();
        }
        dashBoardCongViecCuaToi.ngayGui = DateTimeUtils.toString(request.getNgayGui());
        return dashBoardCongViecCuaToi;
    }

    private static String getVal(Map<Long, User> userById, Long key) {
        if (CollectionUtils.isEmpty(userById)) {
            return key.toString();
        }
        Long k = Long.valueOf(key);
        if (userById.containsKey(k)) {
            return userById.get(k) == null ? key.toString() : userById.get(k).getAlias();
        }
        return key.toString();
    }

    public static DashBoardCongViecCuaToi fromPhuongAn(PhuongAn el, Map<Long, User> userById) {
        DashBoardCongViecCuaToi dashBoardCongViecCuaToi = new DashBoardCongViecCuaToi();
//        dashBoardCongViecCuaToi.ma = "Key-" + el.getPaId();
        dashBoardCongViecCuaToi.ma = el.getMaSo();
        dashBoardCongViecCuaToi.requestId = el.getPaId();
        dashBoardCongViecCuaToi.status = "Vừa tạo";
        if (el.getCntpReceiverId() == null) {
            dashBoardCongViecCuaToi.type = RequestType.PHUONG_AN;
            dashBoardCongViecCuaToi.setNoiDung(el.getNoiDung());
            if (el.getNguoiLapXacNhan()) {
                dashBoardCongViecCuaToi.status = "Người lập phiếu đã ký";
            }
            if (el.getTruongPhongKTHKXacNhan()) {
                dashBoardCongViecCuaToi.status = "Trưởng phòng đã ký";
            }
            if (el.getTruongPhongVatTuXacNhan()) {
                dashBoardCongViecCuaToi.status = "Phòng vật tư phiếu đã ký";
            }
            if (el.getTruongPhongKeHoachXacNhan()) {
                dashBoardCongViecCuaToi.status = " Phòng kế hoạch đã ký";
            }
            if (el.getGiamDocXacNhan()) {
                dashBoardCongViecCuaToi.status = " Giám đốc đã ký";
            }
        } else {
            dashBoardCongViecCuaToi.type = RequestType.CONG_NHAN_THANH_PHAM;

        }

        dashBoardCongViecCuaToi.ngayGui = DateTimeUtils.toString(el.getNgayGui());
        return dashBoardCongViecCuaToi;
    }

    public static DashBoardCongViecCuaToi fromCNTP(CongNhanThanhPham el, Map<Long, User> userById) {
        DashBoardCongViecCuaToi dashboard = new DashBoardCongViecCuaToi();
        dashboard.ma = "CNTP-" + el.getTpId();
        dashboard.requestId = el.getTpId();
        dashboard.status = "Đang xử lý ... ";
        dashboard.noiDung = el.getNoiDung();
        dashboard.setType(RequestType.CONG_NHAN_THANH_PHAM);

        if (!CollectionUtils.isEmpty(el.getNoiDungThucHiens())) {
            int count = 0;
            int tongKcs = 0;
            boolean dataValid = true;
            for (NoiDungThucHien noiDungThucHien : el.getNoiDungThucHiens()) {
                if (Objects.isNull(noiDungThucHien.getNghiemThu())) {
                    dataValid = false;
                }
                if (Objects.nonNull(noiDungThucHien.getNghiemThu())) {
                    tongKcs++;
                }
                if (noiDungThucHien.isXacNhan()) {
                    count++;
                }
            }
            if (tongKcs < el.getNoiDungThucHiens().size()) {
                dashboard.status = "Chưa giao hết việc cho n.Viên KCS";
            }
            if (dataValid) {
                dashboard.status = String.format(" %s/%s nhân viên KCS đã ký", count, el.getNoiDungThucHiens().size());
            }

        }
        if (el.getTpkcsXacNhan()) {
            dashboard.status = "TP.KCS đã ký";
        }
//        if (el.getQuanDocXacNhan() && el.getTpkcsXacNhan()) {
//            dashboard.status = "Hoàn Thành";
//        }
        dashboard.ngayGui = DateTimeUtils.toString(el.getNgayGui());
        return dashboard;
    }
}



