package com.px.tool.domain.kiemhong;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.px.tool.infrastructure.model.payload.AbstractObject;
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
@Table(name = "kiem_hong_detail")
public class KiemHongDetail extends AbstractObject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long khDetailId;

    @Column
    private String tt;

    @Column
    private String tenPhuKien;

    @Column
    private String tenLinhKien;

    @Column
    private String kyHieu;

    @Column
    private String sl;

    @Column
    private String dvt;

    @Column
    private String dangHuHong;

    @Column
    private String phuongPhapKhacPhuc;

    @Column
    private String nguoiKiemHong;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "khId")
    private KiemHong kiemHong;

    @Column
    private Long paId;

    @Column
    private Boolean deleted = false;

    public Boolean getDeleted() {
        return deleted == null ? false : deleted;
    }
}
