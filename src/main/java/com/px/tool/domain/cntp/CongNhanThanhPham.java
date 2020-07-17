package com.px.tool.domain.cntp;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.px.tool.domain.phuongan.PhuongAn;
import com.px.tool.infrastructure.model.payload.EntityDefault;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "cong_nhan_thanh_pham")
public class CongNhanThanhPham extends EntityDefault {
    // @formatter:off
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long tpId; // thanh pham id
    @Column private String tenSanPham;

    @Column private String noiDung;

    @Column private String soPA;
    @Column private Long paId;

    @Column private String donviThucHien;

    @Column private String donviDatHang;

    @Column private String soNghiemThuDuoc;

    @Column private Float dong;

    @Column private Float gioX;

    @Column private Float laoDongTienLuong;

    @Column private String dvt;

    @Column private String to;

    @Column private String soLuong;

    @Column private Long noiNhan;

    @Column private String quanDocIds;

    @Column private Boolean tpkcsXacNhan;
    @Column private Long tpkcsId;
    @Column private Long ngayThangNamTPKCS;
    @Column private String ykientpkcs;


    // danh sach 5 to truong:
    @Column private Boolean toTruong1XacNhan;
    @Column private Long toTruong1Id;
    @Column private String ykienToTruong1;
    @Column private Long ngayThangNamToTruong1;

    @Column private Boolean toTruong2XacNhan;
    @Column private Long toTruong2Id;
    @Column private String ykienToTruong2;
    @Column private Long ngayThangNamToTruong2;

    @Column private Boolean toTruong3XacNhan;
    @Column private Long toTruong3Id;
    @Column private String ykienToTruong3;
    @Column private Long ngayThangNamToTruong3;

    @Column private Boolean toTruong4XacNhan;
    @Column private Long toTruong4Id;
    @Column private String ykienToTruong4;
    @Column private Long ngayThangNamToTruong4;

    @Column private Boolean toTruong5XacNhan;
    @Column private Long toTruong5Id;
    @Column private String ykienToTruong5;
    @Column private Long ngayThangNamToTruong5;



    @Column private Long ngayGui;
    @Column private Long step = 0L;


    @JsonManagedReference
    @OneToMany(mappedBy = "congNhanThanhPham", cascade = CascadeType.ALL)
    private Set<NoiDungThucHien> noiDungThucHiens = new LinkedHashSet<>();

    @JsonBackReference
    @OneToOne(mappedBy = "congNhanThanhPham")
    private PhuongAn phuongAn;
// @formatter:on

    public Boolean getTpkcsXacNhan() {
        return tpkcsXacNhan == null ? false : tpkcsXacNhan;
    }

    public Boolean getToTruong1XacNhan() {
        return toTruong1XacNhan == null ? false : toTruong1XacNhan;
    }

    public Boolean getToTruong2XacNhan() {
        return toTruong2XacNhan == null ? false : toTruong2XacNhan;
    }

    public Boolean getToTruong3XacNhan() {
        return toTruong3XacNhan == null ? false : toTruong3XacNhan;
    }

    public Boolean getToTruong4XacNhan() {
        return toTruong4XacNhan == null ? false : toTruong4XacNhan;
    }

    public Boolean getToTruong5XacNhan() {
        return toTruong5XacNhan == null ? false : toTruong5XacNhan;
    }

    public boolean allApproved() {
        return this.step != null && this.step.equals(1L);
    }
}
