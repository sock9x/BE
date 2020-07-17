package com.px.tool.domain.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.px.tool.infrastructure.model.payload.EntityDefault;
import com.px.tool.infrastructure.utils.CommonUtils;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User extends EntityDefault implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    @Type(type = "text")
    private String signImg;

    @Column
    private String fullName;

    @Column
    private String alias;

    @JsonManagedReference
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> authorities = new HashSet<>();

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "phongBanId")
    private PhongBan phongBan;

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Folder> folders = new HashSet<>();

    @JsonManagedReference
    @Override
    public Collection<Role> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getFullName() {
        return StringUtils.isEmpty(fullName) ? email : CommonUtils.limitStr(fullName);
    }

    public boolean isAdmin() {
        if (CollectionUtils.isEmpty(this.authorities)) {
            return false;
        } else {
            for (Role authority : authorities) {
                return authority.getAuthority().equalsIgnoreCase("ADMIN") || authority.getAuthority().equalsIgnoreCase("ROLE_ADMIN");
            }
        }
        return false;
    }

    public int getLevel() {
        if (CollectionUtils.isEmpty(this.authorities)) {
            return -1;
        } else {
            for (Role authority : authorities) {
                switch (authority.getAuthority()) {
                    case "ADMIN":
                        return 1;
                    case "LEVEL2":
                        return 2;
                    case "LEVEL3":
                        return 3;
                    case "LEVEL4":
                        return 4;
                    case "LEVEL5":
                        return 5;
                    default:
                        return 0;
                }
            }
        }
        return -1;
    }

    /**
     * (cấp 4b từ account 51 đến 81) là người lập phiếu) => Lưu và Chuyển phiếu cho Trợ lý KT (cấp 4a từ account 29 đến 40)
     * Hiện cấp 4a để lựa chọn và chuyển
     */
    public boolean isToTruong() {
        return this.getLevel() == 5 && this.phongBan != null && (
                phongBan.getGroup().equals(17)
                        || phongBan.getGroup().equals(18)
                        || phongBan.getGroup().equals(19)
                        || phongBan.getGroup().equals(20)
                        || phongBan.getGroup().equals(21)
                        || phongBan.getGroup().equals(22)
                        || phongBan.getGroup().equals(23)
                        || phongBan.getGroup().equals(24)
        );
    }

    /**
     * (cấp 4a từ account 29 đến 40) => Lưu và Chuyển phiếu cho Quản đốc phân xưởng (cấp 3 từ account 18 đến 25) (cấp 3 từ account 17 đến 25)
     * Hiện cấp 3 (từ account 17 đến 25) để lựa chọn và chuyển
     */
    public boolean isTroLyKT() {
        return this.getLevel() == 4 && this.phongBan != null && (
                phongBan.getGroup().equals(8)
                        || phongBan.getGroup().equals(9)
        );
    }

    /**
     * (cấp 3 từ account 18 đến 25) => Lưu và Chuyển phiếu cho Nhân viên vật tư (50a, 50b, 50c)
     * Hiện 50a, 50b, 50c để chọn và chuyển
     * Hiện cấp 4a từ account 29 đến 40 nếu không đồng ý (phải có nguyên nhân ) (lặp đến khi nào Quản đốc đồng ý)
     */
    public boolean isQuanDocPhanXuong() {
        return this.getLevel() == 3 && this.phongBan != null && (
                phongBan.getGroup().equals(17)
                        || phongBan.getGroup().equals(18)
                        || phongBan.getGroup().equals(19)
                        || phongBan.getGroup().equals(20)
                        || phongBan.getGroup().equals(21)
                        || phongBan.getGroup().equals(22)
                        || phongBan.getGroup().equals(23)
                        || phongBan.getGroup().equals(24)
                        || phongBan.getGroup().equals(25)
        );
    }


    /**
     * Nhân viên vật tư (50a, 50b, 50c) Chuyển cho Nhân viên vật tư khác (50a, 50b, 50c) hoặc Trưởng phòng Vật tư (account 12).
     */
    public boolean isNhanVienVatTu() {
        return this.getLevel() == 4 && this.phongBan != null && (phongBan.getGroup().equals(12));
    }

    /**
     * Chuyển cho Trợ lý Phòng KTHK (Trợ lý Phòng Kỹ thuật hàng không) hoặc Trợ lý Phòng Xe máy đặc chủng (các trợ lý này từ account 29 đến 40).
     * Nếu đồng ý  Hiện cấp 50a, 50b, 50c
     * nếu không đồng ý phải có nguyên nhân (lặp đến khi nào Trưởng phòng đồng ý)
     */
    public boolean isTruongPhongVatTu() {
        return this.getLevel() == 3 && this.phongBan != null && (phongBan.getGroup().equals(12));
    }

    /**
     * (Trợ lý Phòng Kỹ thuật hàng không) hoặc Trợ lý Phòng Xe máy đặc chủng (các trợ lý này từ account 29 đến 40)
     * Chuyển cho Trưởng Phòng KTHK (account 8) hoặc Trưởng Phòng Xe máy đặc chủng (account 9).
     */
    public boolean isTroLyPhongKTHK() {
        return isTroLyKT();
    }

    /**
     * (account 8) hoặc Trưởng Phòng Xe máy đặc chủng (account 9)
     * Nếu đồng ý ký chuyển, văn bản đến sẽ được gửi đến: account có trong NGƯỜI THỰC HIỆN (H7) và PVT, PKTHK (hay Phòng Xe máy ĐẶC CHỦNG tùy thằng ký) Lưu ý trong VĂN BẢN ĐẾN (KHÔNG PHẢI VĂN BẢN CẦN GIẢI QUYẾT)
     * Hiện cấp account 29 đến 40 nếu không đồng ý phải có nguyên nhân (lặp đến khi nào Trưởng phòng đồng ý)
     */
    public boolean isTruongPhongKTHK() {
        return this.getLevel() == 3 && this.phongBan != null && (
                phongBan.getGroup().equals(8)
                        || phongBan.getGroup().equals(9)
        );
    }

    /**
     * Người lập phiếu: Trợ lý Phòng KTHK (Trợ lý Phòng Kỹ thuật hàng không) hoặc Trợ lý Phòng Xe máy đặc chủng (các trợ lý này từ account 29 đến 40): nhập liệu, sau đó Lưu và
     * Chuyển cho Trưởng Phòng KTHK (account 8) hoặc Trưởng Phòng Xe máy đặc chủng (account 9).
     *
     * @return
     */
    public boolean isNguoiLapPhieu() {
        return isTroLyPhongKTHK();
    }

    /**
     * Nhân viên tiếp liệu (account 50.d): nhập liệu trang 2 từ H29 đến N29, sau đó ấn Lưu và Chuyển cho Trưởng phòng Vật tư (account 12)
     */
    public boolean isNhanVienTiepLieu() {
//        return this.getLevel() == 4 && this.phongBan != null && (phongBan.getGroup().equals(12));
        return this.userId.equals(54L);
    }


    /**
     * Nhân viên Định mức (account 50.e): sửa số liệu, sửa cột dữ liệu (dòng L11 đến L15),  nhập liệu D33 ấn Lưu và Chuyển cho Trưởng phòng Kế hoạch (account 14).
     */
    public boolean isNhanVienDinhMuc() {
        return this.getLevel() == 4 && this.phongBan != null && (phongBan.getGroup().equals(14));
    }

    /**
     * Trưởng phòng Kế hoạch (account 14): sửa số liệu, sau đó đồng ý (chữ ký) và ấn Chuyển cho account số 2,4,5,6 cấp 2 chỉ được chọn 1 và lựa chọn account 50.e (em vẫn chưa hiểu chỗ này ạ?)
     * Hiện acount 2 đến 6 để chọn và chuyển, nếu đồng ý
     * Hiện cấp account 50d nếu không đồng ý phải có nguyên nhân (lặp đến khi nào Trưởng phòng đồng ý)
     */
    public boolean isTruongPhongKeHoach() {
        return this.getLevel() == 3 && this.phongBan != null && (phongBan.getGroup().equals(14));
    }

    /**
     * Người lập phiếu (Người Thực hiện): Tổ trưởng (Phương án sẽ chọn tổ trưởng)
     * Phiếu công nhận được gửi theo N38-N40 bảng chọn của phương án cấp Phân xưởng;
     */
    public boolean isNguoiLapPhieuCNTP() {
        return isToTruong();
    }

    /**
     * Chuyển cho các Nhân viên KCS (account từ 41 đến 50)
     */
    public boolean isNhanVienKCS() {
        return this.getLevel() == 4 && this.phongBan != null && (phongBan.getGroup().equals(10));
    }

    public boolean isTruongPhongKCS() {
        return this.getLevel() == 3 && this.phongBan != null && (phongBan.getGroup().equals(10) || phongBan.getGroup().equals(14));
    }

    public String getAlias() {
        return StringUtils.isEmpty(alias) ? getFullName() : alias;
    }

    public boolean isGiamDoc() {
        return this.getLevel() == 2;
    }

//    public String getSignImg() {
//        return Objects.isNull(signImg) ? "" : new String(signImg);
//    }
//
//    public void setSignImg(String base64){
//        this.signImg = base64.getBytes();
//    }
}
