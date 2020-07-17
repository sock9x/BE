package com.px.tool.domain.request.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.px.tool.domain.RequestType;
import com.px.tool.domain.cntp.CongNhanThanhPham;
import com.px.tool.domain.dathang.PhieuDatHangDetail;
import com.px.tool.domain.kiemhong.KiemHongDetail;
import com.px.tool.domain.phuongan.PhuongAn;
import com.px.tool.domain.request.Request;
import com.px.tool.infrastructure.model.payload.AbstractObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.px.tool.infrastructure.utils.DateTimeUtils.dateLongToString;

@Getter
@Setter
@ToString
public class ThongKeDetailPayload extends AbstractObject implements Comparable<ThongKeDetailPayload> {
    public Long tt;
    public Long detailId;
    public String tenPhuKien;
    public String tenLinhKien;
    public String kyHieu;
    public Long SL;
    public String dangHuHong;
    public String ngayKiemHong;
    public String phuongPhapKhacPhuc;
    public String ngayChuyenPhongVatTu;
    public String soPhieuDatHang;
    public String ngayChuyenKT;
    public String soPA;
    public String ngayRaPA;
    public String ngayChuyenKH;
    public String ngayPheDuyet;
    public String ngayHoanThanh;
    public String xacNhanHoanThanh;
    public Long requestId;
    public Long createdAt;
    public Long pdhId;
    public RequestType currentStatus;

    // update: add 2 fields
    private String toTruongFullName;
    @JsonIgnore
    private Long toTruongId;
    private Long soCNTP;

    public static List<ThongKeDetailPayload> fromRequestEntity(Request request, Map<Long, PhuongAn> phuongAnById) {
//        List<ThongKeDetailPayload> tks = new ArrayList<>(request.getKiemHong().getKiemHongDetails().size());
        List<ThongKeDetailPayload> tks = new ArrayList<>();
        if(Objects.nonNull(request.getKiemHong())){
            for (KiemHongDetail detail : request.getKiemHong().getKiemHongDetails()) {
                //        tk.tt =
                ThongKeDetailPayload tk =  new ThongKeDetailPayload();
                tk.currentStatus = request.getStatus();
                tk.createdAt = request.getCreatedAt();
                tk.tenPhuKien = detail.getTenPhuKien();
                tk.tenLinhKien = detail.getTenLinhKien();
                tk.kyHieu = detail.getKyHieu();
                try {
                    tk.SL = Long.valueOf(detail.getSl() != null ? detail.getSl() : "0");
                } catch (NumberFormatException e) {
                    tk.SL = 0L;
                }
                tk.dangHuHong = detail.getDangHuHong();
                tk.ngayKiemHong = dateLongToString(request.getKiemHong().getNgayThangNamTroLyKT());
                tk.phuongPhapKhacPhuc = detail.getPhuongPhapKhacPhuc();
                tk.ngayChuyenPhongVatTu = dateLongToString(request.getKiemHong().getNgayThangNamQuanDoc());
                tk.detailId = detail.getKhDetailId(); // detailId de phuc vu cho viec tao phuong an
                tk.pdhId = request.getRequestId(); // dang dung request mapping 1-1 kiemhong/dathang
                try {
                    tk.soPhieuDatHang = request.getPhieuDatHang().getSo();

                } catch (Exception e) {
                }
                tk.ngayChuyenKT = dateLongToString(request.getPhieuDatHang().getNgayThangNamTPVatTu());
                tk.requestId = 0L;
                tk.toTruongId = request.getKiemHong().getToTruongId();
                setThongkePAAndCNTP(tk,phuongAnById,detail.getPaId());
                tks.add(tk);
            }

        }else{
            ThongKeDetailPayload tk =  new ThongKeDetailPayload();
            setThongkePAAndCNTP(tk,phuongAnById,request.getPhuongAn().getPaId());
            tks.add(tk);
        }

        return tks;
    }

    static void setThongkePAAndCNTP(ThongKeDetailPayload tk, Map<Long, PhuongAn> phuongAnById, Long paId) {
        try {
            PhuongAn pa = phuongAnById.get(paId);
            tk.requestId = paId;
            tk.soPA = pa.getMaSo();
            tk.ngayRaPA = dateLongToString(pa.getNgayThangNamNguoiLap());
            tk.ngayChuyenKH = dateLongToString(pa.getNgayThangNamtpVatTu());
            tk.ngayPheDuyet = dateLongToString(pa.getNgayThangNamGiamDoc());
            CongNhanThanhPham cntp = pa.getCongNhanThanhPham();
            Long ngayht = getBiggest(cntp.getNgayThangNamToTruong1(), cntp.getNgayThangNamToTruong2(), cntp.getNgayThangNamToTruong3(), cntp.getNgayThangNamToTruong4(), cntp.getNgayThangNamToTruong5());
            tk.ngayHoanThanh = dateLongToString(ngayht > 0 ? ngayht : null);
            tk.xacNhanHoanThanh = dateLongToString(cntp.getNgayThangNamTPKCS());
            tk.soCNTP = pa.getCongNhanThanhPham().getTpId();
        } catch (Exception e) {
        }
    }

    @JsonIgnore
    static long getBiggest(Long... dates) {
        long biggest = -1;
        for (Long date : dates) {
            if (date != null && biggest < date) {
                biggest = date;
            }
        }
        return biggest;
    }


    @JsonIgnore
    @Override
    public int compareTo(ThongKeDetailPayload o) {
        return (this.soPA == null) ? -1 : 1;
    }

    @JsonIgnore
    public String getSoPAAsStr() {
        return this.soPA == null ? "" : this.soPA;
    }

    @JsonIgnore
    public Long getDetailIdAsStr() {
        return detailId == null ? -1 : this.detailId;
    }
}
