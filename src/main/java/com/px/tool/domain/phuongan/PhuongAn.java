package com.px.tool.domain.phuongan;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.px.tool.domain.RequestType;
import com.px.tool.domain.cntp.CongNhanThanhPham;
import com.px.tool.infrastructure.model.payload.EntityDefault;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "phuong_an")
public class PhuongAn extends EntityDefault {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long paId;

    @Column
    private String tenNhaMay;

    @Column
    private Long ngayThangNamPheDuyet;

    @Column
    private String maSo;

    @Column
    private String sanPham;
    @Column
    private String noiDung;

    @Column
    private String nguonKinhPhi;

    @Column
    private String toSo;

    @Column
    private String soTo;

    @Column
    private String PDH;

    //end
    @Column
    private Long ngayThangNamTPKTHK;

    @Column
    private String truongPhongKTHK;

    @Column
    private Long ngayThangNamTPKEHOACH;

    @Column
    private String truongPhongKeHoach;

    @Column
    private Long ngayThangNamtpVatTu;

    @Column
    private String truongPhongVatTu;

    @Column
    private Long ngayThangNamNguoiLap;

    @Column
    private String NguoiLap;

    // cac field tong cong
    @Column
    private BigDecimal tongCongDinhMucLaoDong;

    @Column
    private BigDecimal tongDMVTKho;

    @Column
    private BigDecimal tongDMVTMuaNgoai;

    @Column
    private BigDecimal tienLuong;


    @Column(name = "truongphong_kthk_xacnhan")
    private Boolean truongPhongKTHKXacNhan;

    @Column(name = "truongphong_kehoach_xacnhan")
    private Boolean truongPhongKeHoachXacNhan;

    @Column(name = "truongphong_vattu_xacnhan")
    private Boolean truongPhongVatTuXacNhan;

    @Column(name = "nguoilap_xacnhan")
    private Boolean nguoiLapXacNhan;

    @Column(name = "giamdoc_xacnhan")
    private Boolean giamDocXacNhan;

    @Column
    private Long ngayThangNamGiamDoc;

    @Column
    private String yKienNguoiLap;

    @Column
    private String yKienTruongPhongKTHK;

    @Column
    private String yKienTruongPhongKeHoach;

    @Column
    private String yKienTruongPhongVatTu;

    @Column
    private String ykienTruongPhongKeHoach;

    // signId , fullname
    @Column
    private Long truongPhongKTHKId;

    @Column
    private Long truongPhongKeHoachId;

    @Column
    private Long truongPhongVatTuId;

    @Column
    private Long nguoiLapId;

    @Column
    private Long giamDocId;

    @Column
    private Long phuongAnReceiverId;

    @Column
    private Long cntpReceiverId;

    @Column
    @Enumerated
    private RequestType status;

    @Column
    private String cusReceivers;

    @Column
    private String cusNoiDung;

    @Column
    private String nguoiThucHien; // NOTE: cac don vi thuc hien

    @Column
    private Long step; // 0: van dang xu ly, 1: success_phuong_an

    @Column
    private Long ngayGui;

    @JsonManagedReference
    @OneToMany(mappedBy = "phuongAn", cascade = CascadeType.ALL)
    private Set<DinhMucLaoDong> dinhMucLaoDongs = new LinkedHashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "phuongAn", cascade = CascadeType.ALL)
    private Set<DinhMucVatTu> dinhMucVatTus = new LinkedHashSet<>();

    @JsonManagedReference
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tpId")
    private CongNhanThanhPham congNhanThanhPham;

    public Long getStep() {
        return step == null ? 0 : step;
    }

    public boolean allApproved() {
        return
                Objects.nonNull(truongPhongKTHKXacNhan) && truongPhongKTHKXacNhan &&
                        Objects.nonNull(truongPhongKeHoachXacNhan) && truongPhongKeHoachXacNhan &&
                        Objects.nonNull(truongPhongVatTuXacNhan) && truongPhongVatTuXacNhan &&
                        Objects.nonNull(nguoiLapXacNhan) && nguoiLapXacNhan &&
                        Objects.nonNull(giamDocXacNhan) && giamDocXacNhan;
    }

    public String getMaSo() {
        return StringUtils.isEmpty(maSo) ? "PA-" + paId : maSo;
    }

    public RequestType getStatus() {
        return status == null ? RequestType.PHUONG_AN : status;
    }

    public Boolean getTruongPhongKTHKXacNhan() {
        return getBol(truongPhongKTHKXacNhan);
    }

    public Boolean getTruongPhongKeHoachXacNhan() {
        return getBol(truongPhongKeHoachXacNhan);
    }

    public Boolean getTruongPhongVatTuXacNhan() {
        return getBol(truongPhongVatTuXacNhan);
    }

    public Boolean getNguoiLapXacNhan() {
        return getBol(nguoiLapXacNhan);
    }

    public Boolean getGiamDocXacNhan() {
        return getBol(giamDocXacNhan);
    }
}
