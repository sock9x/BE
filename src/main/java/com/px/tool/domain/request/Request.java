package com.px.tool.domain.request;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.px.tool.domain.RequestType;
import com.px.tool.domain.cntp.CongNhanThanhPham;
import com.px.tool.domain.dathang.PhieuDatHang;
import com.px.tool.domain.kiemhong.KiemHong;
import com.px.tool.domain.phuongan.PhuongAn;
import com.px.tool.infrastructure.model.payload.EntityDefault;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "request")
public class Request extends EntityDefault {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long requestId;

    @Column
    @Enumerated
    private RequestType status;

    @Column
    private Long kiemHongReceiverId;

    @Column
    private Long phieuDatHangReceiverId;

    @Column
    private Long phuongAnReceiverId;

    @Column
    private Long cntpReceiverId;

    @JsonManagedReference
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "khId")
    private KiemHong kiemHong;

    @JsonManagedReference
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pdhId")
    private PhieuDatHang phieuDatHang;

    @Column
    private Long ngayGui;

    @JsonManagedReference
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "paId")
    private PhuongAn phuongAn;

//    @JsonManagedReference
//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "tpId")
//    private CongNhanThanhPham congNhanThanhPham;

    public RequestType getType() {
        return this.status;
    }
}
