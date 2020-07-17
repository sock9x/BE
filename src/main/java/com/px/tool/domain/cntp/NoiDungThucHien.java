package com.px.tool.domain.cntp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.px.tool.infrastructure.model.payload.AbstractObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "noi_dung_thuc_hien")
@Getter
@Setter
@NoArgsConstructor
public class NoiDungThucHien extends AbstractObject {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long noiDungId;

    @Column
    private String noiDung;

    @Column
    private String ketQua;

    @Column
    private String nguoiLam;

    @Column
    private Long nghiemThu;

    @Column
    private boolean xacNhan;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "tpId")
    private CongNhanThanhPham congNhanThanhPham;

    public NoiDungThucHien(String noiDung) {
        this.noiDung = noiDung;
    }
}
