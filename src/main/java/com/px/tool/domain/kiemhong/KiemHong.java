package com.px.tool.domain.kiemhong;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.px.tool.domain.request.Request;
import com.px.tool.infrastructure.model.payload.EntityDefault;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@Table(name = "kiem_hong")
public class KiemHong extends EntityDefault {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long khId;

    @Column
    private String tenNhaMay;

    @Column
    private Long phanXuong;

    @Column
    private Long toSX;

    @Column
    private String tenVKTBKT; // may bay L39

    @Column
    private String nguonVao;// SCL TTNH

    @Column
    private String congDoan; // kiem hong chi tiet

    @Column
    private String soHieu; // 8843

    @Column
    private String soXX; // 8373y64

    @Column
    private String toSo;

    @Column
    private String soTo;

    @Column
    private Long noiNhan;

    @Column
    private Long ngayThangNamQuanDoc;

    @Column(name = "quan_doc_xac_nhan")
    private Boolean quanDocXacNhan;

    @Column
    private String quanDoc;

    @Column
    private Long ngayThangNamTroLyKT;

    @Column(name = "tro_lykt_xac_nhan")
    private Boolean troLyKTXacNhan;

    @Column
    private String troLyKT;

    @Column
    private Long ngayThangNamToTruong;

    @Column(name = "to_truong_xac_nhan")
    private Boolean toTruongXacNhan;

    @Column
    private String toTruong;

    @Column(name = "giam_doc_xac_nhan")
    private Boolean giamDocXacNhan;

    @Column
    private String yKienGiamDoc;

    @Column
    private String yKienQuanDoc;

    @Column
    private String yKienToTruong;

    @Column
    private String yKienTroLyKT;

    // sign Id
    @Column
    private Long quanDocId;
    @Column
    private Long troLyId;
    @Column
    private Long toTruongId;

    // gui van ban den
    @Column
    private String cusReceivers;
    @Column
    private String cusNoiDung;

    @JsonManagedReference
    @OneToMany(mappedBy = "kiemHong", cascade = CascadeType.ALL)
    private Set<KiemHongDetail> kiemHongDetails = new LinkedHashSet<>();

    @JsonBackReference
    @OneToOne(mappedBy = "kiemHong")
    private Request request;

    public Boolean getQuanDocXacNhan() {
        return quanDocXacNhan == null ? false : quanDocXacNhan;
    }

    public Boolean getTroLyKTXacNhan() {
        return troLyKTXacNhan == null ? false : troLyKTXacNhan;
    }

    public Boolean getToTruongXacNhan() {
        return toTruongXacNhan == null ? false : toTruongXacNhan;
    }

    public Boolean getGiamDocXacNhan() {
        return giamDocXacNhan == null ? false : giamDocXacNhan;
    }

    @JsonIgnore
    public boolean allApproved() {
        return
                Objects.nonNull(toTruongXacNhan) && toTruongXacNhan
                        && Objects.nonNull(troLyKTXacNhan) && troLyKTXacNhan
                        && Objects.nonNull(quanDocXacNhan) && quanDocXacNhan;

    }
}
