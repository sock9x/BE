package com.px.tool.domain.phuongan;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "dinh_muc_vat_tu")
public class DinhMucVatTu {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long vtId;

    @Column
    private String tt;

    @Column
    private String tenVatTuKyThuat;

    @Column
    private String kyMaKyHieu;

    @Column
    private String dvt;

    @Column
    private String dm1SP;

    @Column
    private String soLuongSanPham;

    @Column
    private String tongNhuCau;
    // huy dong kho
    @Column
    private String khoDonGia;

    @Column
    private String khoSoLuong;

    @Column
    private String khoThanhTien;

    @Column
    private String khoTongTien;

    //mua ngoai
    @Column
    private String mnDonGia;

    @Column
    private String mnSoLuong;

    @Column
    private String mnThanhTien;

    @Column
    private String mnTongTien;

    @Column
    private String ghiChu;

    @Column
    private String tienLuong;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "paId")
    private PhuongAn phuongAn;
}
