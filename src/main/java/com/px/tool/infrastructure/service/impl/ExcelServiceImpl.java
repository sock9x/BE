package com.px.tool.infrastructure.service.impl;

import com.px.tool.domain.RequestType;
import com.px.tool.domain.cntp.CongNhanThanhPham;
import com.px.tool.domain.cntp.CongNhanThanhPhamPayload;
import com.px.tool.domain.cntp.repository.CongNhanThanhPhamRepository;
import com.px.tool.domain.cntp.service.CongNhanThanhPhamService;
import com.px.tool.domain.dathang.PhieuDatHangPayload;
import com.px.tool.domain.dathang.service.PhieuDatHangService;
import com.px.tool.domain.kiemhong.KiemHongPayLoad;
import com.px.tool.domain.kiemhong.service.KiemHongService;
import com.px.tool.domain.mucdich.sudung.MucDichSuDung;
import com.px.tool.domain.mucdich.sudung.repository.MucDichSuDungRepository;
import com.px.tool.domain.phuongan.PhuongAn;
import com.px.tool.domain.phuongan.PhuongAnPayload;
import com.px.tool.domain.phuongan.repository.PhuongAnRepository;
import com.px.tool.domain.phuongan.service.PhuongAnService;
import com.px.tool.domain.request.Request;
import com.px.tool.domain.request.payload.ThongKeDetailPayload;
import com.px.tool.domain.request.payload.ThongKePageRequest;
import com.px.tool.domain.request.payload.ThongKePageResponse;
import com.px.tool.domain.request.repository.RequestRepository;
import com.px.tool.domain.request.service.RequestService;
import com.px.tool.domain.user.User;
import com.px.tool.domain.user.service.UserService;
import com.px.tool.infrastructure.exception.PXException;
import com.px.tool.infrastructure.service.ExcelService;
import com.px.tool.infrastructure.utils.CollectionUtils;
import com.px.tool.infrastructure.utils.CommonUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.data.domain.PageRequest.of;

@Service
public class ExcelServiceImpl extends BaseServiceImpl implements ExcelService {
    private static final String base_dir_kiem_hong = "/home/nghiapt/source/thu_muc_cap_nhat_phan_mem/PhuongAnSo_thong_ke/kiem_hong/";
    private static final String base_dir_dat_hang = "/home/nghiapt/source/thu_muc_cap_nhat_phan_mem/PhuongAnSo_thong_ke/dat_hang/";
    private static final String base_dir_pa = "/home/nghiapt/source/thu_muc_cap_nhat_phan_mem/PhuongAnSo_thong_ke/phuong_an/";
    private static final String base_dir_cntp = "/home/nghiapt/source/thu_muc_cap_nhat_phan_mem/PhuongAnSo_thong_ke/cntp/";
    private static final String base_dir_thong_ke = "/home/nghiapt/source/thu_muc_cap_nhat_phan_mem/PhuongAnSo_thong_ke/thongke/";

    @Autowired
    private KiemHongService kiemHongService;

    @Autowired
    private PhieuDatHangService phieuDatHangService;

    @Autowired
    private PhuongAnService phuongAnService;

    @Autowired
    private CongNhanThanhPhamService congNhanThanhPhamService;

    @Autowired
    private UserService userService;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private PhuongAnRepository phuongAnRepository;

    @Autowired
    private CongNhanThanhPhamRepository congNhanThanhPhamRepository;

    @Autowired
    private MucDichSuDungRepository mucDichSuDungRepository;

    @Autowired
    private RequestService requestService;

    @Value("${app.file.export.kiemhong}")
    private String kiemHongPrefix;

    @Value("${app.file.export.dathang}")
    private String datHangPrefix;

    @Value("${app.file.export.pa}")
    private String paPrefix;

    @Value("${app.file.export.cntp}")
    private String cntpPrefix;

    @Value("${app.file.export.thongke}")
    private String thongKePrefix;

    @Override
    public void exportFile(Long requestId, RequestType requestType, OutputStream outputStream, Long startDate, Long endDate, Long toKH, Long spId) {
        FileInputStream fis = null;
        try {
            if (requestType == RequestType.KIEM_HONG) {
                fis = new FileInputStream(new File("./src/main/resources/templates/1_Kiem_Hong.xlsx"));
                exportKiemHong(fis, outputStream, kiemHongService.findThongTinKiemHong(1L, requestId));
            } else if (requestType == RequestType.DAT_HANG) {
                fis = new FileInputStream(new File("./src/main/resources/templates/2_Dat_Hang.xlsx"));
                exportPHieuDatHang(fis, outputStream, phieuDatHangService.findById(1L, requestId), mdsdMap());
            } else if (requestType == RequestType.PHUONG_AN) {
                fis = new FileInputStream(new File("./src/main/resources/templates/3_phuong_an.xlsx"));
                exportPhuongAn(fis, outputStream, phuongAnService.findById(1L, requestId));
            } else if (requestType == RequestType.CONG_NHAN_THANH_PHAM) {
                fis = new FileInputStream(new File("./src/main/resources/templates/4_cntp.xlsx"));
                exportCNTP(fis, outputStream, congNhanThanhPhamService.timCongNhanThanhPham(1L, requestId));
            } else if (requestType == RequestType.THONG_KE) {
                fis = new FileInputStream(new File("./src/main/resources/templates/5_ThongKeTienDoSP.xlsx"));
                Date fromDate = new Date(startDate);
                Date toDate = endDate > 0 ? new Date(endDate) : new Date(System.currentTimeMillis());
                String fromDateStr = CommonUtils.convertDateToPatern(fromDate, "yyyy-MM-dd");
                String toDateStr = CommonUtils.convertDateToPatern(toDate, "yyyy-MM-dd");
                exportThongKe(fis, outputStream, setThongKePayLoad(startDate, endDate, spId, toKH), fromDateStr, toDateStr, toKH);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new PXException("File not found");
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void exportKiemHong(InputStream fis, OutputStream response, KiemHongPayLoad payload) {
        Map<Long, User> userById = userService.userById();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int totalLine = payload.getKiemHongDetails().size();
            XSSFRow row24 = sheet.getRow(24);
            XSSFRow row26 = sheet.getRow(26);
            XSSFRow row27 = sheet.getRow(27);

            String review = "";
            for (Long cus : payload.getCusReceivers()) {
                review += "- " + userById.get(cus).getAlias() + "\n";
            }

            setCellVal(row24, 3, payload.getNgayThangNamQuanDoc());
            setCellVal(row24, 6, payload.getNgayThangNamTroLyKT());
            setCellVal(row24, 8, payload.getNgayThangNamToTruong());

            CellStyle style = workbook.createCellStyle(); //Create new style
            style.setWrapText(true);
            row26.getCell(1).setCellStyle(style);
            setCellVal(row26, 1, review);
            row26.getCell(1).setCellFormula(null);
            setCellVal(row27, 3, payload.getQuanDocfullName());
            setCellVal(row27, 6, payload.getTroLyfullName());
            setCellVal(row27, 8, payload.getToTruongfullName());

            if (totalLine > 18) {
                sheet.copyRows(24, 27, 27 + (totalLine - 18), new CellCopyPolicy()); // copy and paste

                for (int i = 24; i < 27 + (totalLine - 18); i++) {
                    sheet.createRow(i);
                    sheet.copyRows(6, 6, i - 1, new CellCopyPolicy()); // copy and paste
                }
            }
            XSSFRow row0 = sheet.getRow(0);
            XSSFRow row1 = sheet.getRow(1);
            XSSFRow row2 = sheet.getRow(2);

            setCellVal(row0, 4, payload.getTenVKTBKT());
            setCellVal(row0, 6, payload.getSoHieu());
            setCellVal(row0, 8, payload.getToSo());
            setCellVal(row1, 2, fillUserInfo(payload.getPhanXuong(), userById));
            setCellVal(row1, 4, payload.getNguonVao());
            setCellVal(row1, 6, payload.getSoXX());
            setCellVal(row1, 8, payload.getSoTo());
            setCellVal(row2, 2, fillUserInfo(payload.getToSX(), userById));
            setCellVal(row2, 4, payload.getCongDoan());

            for (int i = 0; i < totalLine; i++) {
                XSSFRow currRow = sheet.getRow(5 + i);
                setCellVal(currRow, 0, i + 1 + "");
                setCellVal(currRow, 1, payload.getKiemHongDetails().get(i).getTenPhuKien());
                setCellVal(currRow, 3, payload.getKiemHongDetails().get(i).getTenLinhKien());
                setCellVal(currRow, 4, payload.getKiemHongDetails().get(i).getKyHieu());
                setCellVal(currRow, 5, payload.getKiemHongDetails().get(i).getSl());
                setCellVal(currRow, 6, payload.getKiemHongDetails().get(i).getDangHuHong());
                setCellVal(currRow, 7, payload.getKiemHongDetails().get(i).getPhuongPhapKhacPhuc());
                setCellVal(currRow, 8, payload.getKiemHongDetails().get(i).getNguoiKiemHong());
            }
            int rowMax = 26;
            if (totalLine > 18) {
                rowMax = 29 + (totalLine - 18);
            }
            if (payload.getQuanDocSignImg() != null && !payload.getQuanDocSignImg().isEmpty()) {
                addImageToExcel(workbook, sheet, payload.getQuanDocSignImg(), 3, rowMax, 4, rowMax + 1);
            }
            if (payload.getTroLyKTSignImg() != null && !payload.getTroLyKTSignImg().isEmpty()) {
                addImageToExcel(workbook, sheet, payload.getTroLyKTSignImg(), 6, rowMax, 7, rowMax + 1);
            }
            if (payload.getToTruongSignImg() != null && !payload.getToTruongSignImg().isEmpty()) {
                addImageToExcel(workbook, sheet, payload.getToTruongSignImg(), 8, rowMax, 9, rowMax + 1);
            }
            workbook.write(response);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            response.flush();
        } catch (IOException e) {
        }
    }

    private void exportPHieuDatHang(InputStream is, OutputStream outputStream, PhieuDatHangPayload payload, Map<Long, String> mdsdNameById) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int totalLine = payload.getPhieuDatHangDetails().size();
            XSSFRow row13 = sheet.getRow(13);
            setCellVal(row13, 2, payload.getNgayThangNamTPKTHK());
            setCellVal(row13, 6, payload.getNgayThangNamTPVatTu());
            setCellVal(row13, 8, payload.getNgayThangNamNguoiDatHang());
//            sheet.createRow(15);
            sheet.copyRows(12, 12, 16, new CellCopyPolicy()); // copy and paste
            sheet.createRow(15).createCell(1);
            XSSFRow row15 = sheet.getRow(15);
            CellStyle style = workbook.createCellStyle(); //Create new style
            style.setWrapText(true);
            row15.getCell(1).setCellStyle(style);
            String custormerReview = "";
            List<User> userList = userService.findByIds(payload.getCusReceivers());
            for (User cus : userList) {
                custormerReview += "- " + cus.getAlias() + "\n";
            }

            setCellVal(row15, 1, custormerReview);
            XSSFRow row16 = sheet.getRow(16);
            row16.createCell(2);
            row16.createCell(6);
            setCellVal(row16, 2, payload.getTpkthkFullName());
            setCellVal(row16, 6, payload.getTpvatTuFullName());
            setCellVal(row16, 8, payload.getNguoiDatHangFullName());
            if (totalLine > 5) {
                sheet.copyRows(13, 16, 16 + (totalLine - 5), new CellCopyPolicy()); // copy and paste
                for (int i = 13; i < 16 + (totalLine - 5); i++) {
                    sheet.createRow(i);
                    sheet.copyRows(7, 7, i - 1, new CellCopyPolicy()); // copy and paste
                }
            }

            XSSFRow row0 = sheet.getRow(1);
            XSSFRow row1 = sheet.getRow(2);
            XSSFRow row2 = sheet.getRow(3);

            setCellVal(row0, 6, payload.getSo());
            setCellVal(row1, 6, payload.getDonViYeuCau());
            setCellVal(row1, 8, payload.getPhanXuong());

            setCellVal(row2, 6, payload.getNoiDung());

            for (int i = 0; i < totalLine; i++) {
                XSSFRow crrRow = sheet.getRow(6 + i);
                setCellVal(crrRow, 0, i + 1 + "");
                setCellVal(crrRow, 1, payload.getPhieuDatHangDetails().get(i).getTenPhuKien());
                setCellVal(crrRow, 2, payload.getPhieuDatHangDetails().get(i).getTenVatTuKyThuat());
                setCellVal(crrRow, 3, payload.getPhieuDatHangDetails().get(i).getKiMaHieu());
                setCellVal(crrRow, 4, payload.getPhieuDatHangDetails().get(i).getDvt());
                setCellVal(crrRow, 5, payload.getPhieuDatHangDetails().get(i).getSl());
                setCellVal(crrRow, 6, getVal(mdsdNameById, payload.getPhieuDatHangDetails().get(i).getMucDichSuDung()));
                setCellVal(crrRow, 7, payload.getPhieuDatHangDetails().get(i).getPhuongPhapKhacPhuc());
                setCellVal(crrRow, 8, payload.getPhieuDatHangDetails().get(i).getSoPhieuDatHang());
                setCellVal(crrRow, 9, payload.getPhieuDatHangDetails().get(i).getNguoiThucHien());
            }
            int rowMax = 15;
            if (totalLine > 5) {
                rowMax = 18 + (totalLine - 5);
            }
            if (payload.getTpkthkSignImg() != null && !payload.getTpkthkSignImg().isEmpty()) {
                addImageToExcel(workbook, sheet, payload.getTpkthkSignImg(), 2, rowMax, 3, rowMax + 1);
            }
            if (payload.getTpvatTuSignImg() != null && !payload.getTpvatTuSignImg().isEmpty()) {
                addImageToExcel(workbook, sheet, payload.getTpvatTuSignImg(), 6, rowMax, 7, rowMax + 1);
            }
            if (payload.getNguoiDatHangSignImg() != null && !payload.getNguoiDatHangSignImg().isEmpty()) {
                addImageToExcel(workbook, sheet, payload.getNguoiDatHangSignImg(), 8, rowMax, 9, rowMax + 1);
            }
            workbook.write(outputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                outputStream.flush();
            } catch (IOException e) {
            }
        }
    }

    public void exportCNTP(InputStream fis, OutputStream outputStream, CongNhanThanhPhamPayload payload) {
        Map<Long, User> userById = userService.userById();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheetAt(0);
            XSSFRow row0 = sheet.getRow(2);
            XSSFRow row1 = sheet.getRow(3);
            XSSFRow row2 = sheet.getRow(4);
            XSSFRow row3 = sheet.getRow(5);
            XSSFRow row4 = sheet.getRow(6);
            XSSFRow row5 = sheet.getRow(7);

            XSSFRow row19 = sheet.getRow(19);
            XSSFRow row21 = sheet.getRow(21);
            XSSFRow row22 = sheet.getRow(22);
            XSSFRow row24 = sheet.getRow(24);
            XSSFRow row25 = sheet.getRow(25);
            XSSFRow row26 = sheet.getRow(26);
            sheet.createRow(28);
            XSSFRow row28 = sheet.getRow(28);

            setCellVal(row0, 1, payload.getTenSanPham());
            setCellVal(row1, 1, payload.getNoiDung());
            setCellVal(row2, 1, payload.getSoPA());
            setCellVal(row3, 1, payload.getDonviThucHien());
            setCellVal(row3, 4, payload.getTo());
            setCellVal(row4, 1, payload.getDonviDatHang());
            setCellVal(row4, 3, payload.getSoLuong());
            setCellVal(row4, 5, payload.getDvt());
            setCellVal(row5, 1, payload.getSoNghiemThuDuoc());
            setCellVal(row19, 1, payload.getLaoDongTienLuong().toString());
            setCellVal(row19, 3, payload.getGioX().toString());
            setCellVal(row19, 5, payload.getDong().toString());

            setCellVal(row21, 1, payload.getNgayThangNamTPKCS());

            if (Objects.nonNull(payload.getToTruong1Id())) {
                setCellVal(row25, 0, getVal(payload.getNgayThangNamToTruong1()));
                setCellVal(row26, 0, getVal(payload.getToTruong1Alias()));
                row28.createCell(0);
                setCellVal(row28, 0, getVal(payload.getToTruong1fullName())); // TODO: id /name/chuc vu
            }
            if (Objects.nonNull(payload.getToTruong2Id())) {
                setCellVal(row25, 1, getVal(payload.getNgayThangNamToTruong2()));
                setCellVal(row26, 1, getVal(payload.getToTruong2Alias()));
                row28.createCell(1);
                setCellVal(row28, 1, getVal(payload.getToTruong2fullName()));
            }
            if (Objects.nonNull(payload.getToTruong3Id())) {
                setCellVal(row25, 2, getVal(payload.getNgayThangNamToTruong3()));
                setCellVal(row26, 2, getVal(payload.getToTruong3Alias()));
                row28.createCell(2);
                setCellVal(row28, 2, getVal(payload.getToTruong3fullName()));
            }
            if (Objects.nonNull(payload.getToTruong4Id())) {
                setCellVal(row25, 3, getVal(payload.getNgayThangNamToTruong4()));
                setCellVal(row26, 3, getVal(payload.getToTruong4Alias()));
                row28.createCell(3);
                setCellVal(row28, 3, getVal(payload.getToTruong4fullName()));
            }
            if (Objects.nonNull(payload.getToTruong5Id())) {
                setCellVal(row25, 4, getVal(payload.getNgayThangNamToTruong5()));
                row28.createCell(4);
                setCellVal(row28, 4, getVal(payload.getToTruong5fullName()));
            }
            if (Objects.nonNull(payload.getTpkcsId())) {
                setCellVal(row24, 1, getVal(payload.getTpkcsFullName()));
            }

            int totalLine = payload.getNoiDungThucHiens().size();
            if (totalLine > 5) {
                sheet.copyRows(18, 29, 29 + (totalLine - 6), new CellCopyPolicy()); // copy and paste

                for (int i = 18; i < 29 + (totalLine - 6); i++) {
                    sheet.createRow(i);
                    sheet.copyRows(12, 12, i - 1, new CellCopyPolicy()); // copy and paste
                }
            }

            for (int i = 0; i < totalLine; i++) {
                XSSFRow crrRow = sheet.getRow(11 + i);
                setCellVal(crrRow, 0, String.valueOf(i + 1));
                setCellVal(crrRow, 1, payload.getNoiDungThucHiens().get(i).getNoiDung());
                setCellVal(crrRow, 2, payload.getNoiDungThucHiens().get(i).getKetQua());
                setCellVal(crrRow, 3, payload.getNoiDungThucHiens().get(i).getNguoiLam());
                if (payload.getNoiDungThucHiens().get(i).getNghiemThu() != null)
                    setCellVal(crrRow, 4, userById.get(payload.getNoiDungThucHiens().get(i).getNghiemThu()).getAlias());
                if (payload.getNoiDungThucHiens().get(i).getXacNhan()) {
                    if (payload.getNoiDungThucHiens().get(i).getSignImg() != null && !payload.getNoiDungThucHiens().get(i).getSignImg().isEmpty()) {
                        addImageToExcel(workbook, sheet, payload.getNoiDungThucHiens().get(i).getSignImg(), 5, 11 + i, 6, 12 + i);
                    }
                }

            }

            int rowMax = 27;
            if (totalLine > 5) {
                rowMax = 37 + (totalLine - 5);
            }
            if (payload.getToTruong1SignImg() != null && !payload.getToTruong1SignImg().isEmpty()) {
                addImageToExcel(workbook, sheet, payload.getToTruong1SignImg(), 0, rowMax, 1, rowMax + 1);
            }
            if (payload.getToTruong2SignImg() != null && !payload.getToTruong2SignImg().isEmpty()) {
                addImageToExcel(workbook, sheet, payload.getToTruong2SignImg(), 1, rowMax, 2, rowMax + 1);
            }
            if (payload.getToTruong3SignImg() != null && !payload.getToTruong3SignImg().isEmpty()) {
                addImageToExcel(workbook, sheet, payload.getToTruong3SignImg(), 2, rowMax, 3, rowMax + 1);
            }
            if (payload.getToTruong3SignImg() != null && !payload.getToTruong3SignImg().isEmpty()) {
                addImageToExcel(workbook, sheet, payload.getToTruong4SignImg(), 3, rowMax, 4, rowMax + 1);
            }
            if (payload.getToTruong3SignImg() != null && !payload.getToTruong3SignImg().isEmpty()) {
                addImageToExcel(workbook, sheet, payload.getToTruong5SignImg(), 4, rowMax, 5, rowMax + 1);
            }
            if (payload.getTpkcsSignImg() != null && !payload.getTpkcsSignImg().isEmpty()) {
                addImageToExcel(workbook, sheet, payload.getTpkcsSignImg(), 1, rowMax - 4, 2, rowMax - 3);
            }
            workbook.write(outputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                outputStream.flush();
            } catch (IOException e) {
            }
        }
    }

    public void exportPhuongAn(InputStream fis, OutputStream outputStream, PhuongAnPayload payload) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheetAt(0);

            XSSFRow row1 = sheet.getRow(1);
            XSSFRow row2 = sheet.getRow(2);
            XSSFRow row3 = sheet.getRow(3);
            XSSFRow row4 = sheet.getRow(4);
            XSSFRow row5 = sheet.getRow(5);
            XSSFRow row6 = sheet.getRow(6);
            XSSFRow row15 = sheet.getRow(15);
            XSSFRow row29 = sheet.getRow(29);
            XSSFRow row30 = sheet.getRow(30);
            XSSFRow row32 = sheet.getRow(32);

            sheet.createRow(34);
            XSSFRow row34 = sheet.getRow(34);
            CellStyle style = workbook.createCellStyle(); //Create new style
            style.setWrapText(true);
            row34.createCell(1).setCellStyle(style);
            String userReview = "";
            List<User> userList = userService.findByIds(payload.getCusReceivers());
            for (User user : userList) {
                userReview += "- " + user.getAlias() + "\n";
            }
            setCellVal(row34, 1, userReview);

            sheet.createRow(35);
            XSSFRow row35 = sheet.getRow(35);
            CellStyle style35 = workbook.createCellStyle(); //Create new style
            style35.setAlignment(HorizontalAlignment.CENTER);
            row35.createCell(2).setCellStyle(style35);
            row35.createCell(6).setCellStyle(style35);
            row35.createCell(9).setCellStyle(style35);
            row35.createCell(12).setCellStyle(style35);
            setCellVal(row35, 2, payload.getTruongPhongKTHKFullName());
            setCellVal(row35, 6, payload.getTruongPhongKeHoachFullName());
            setCellVal(row35, 9, payload.getTruongPhongVatTuFullName());
            setCellVal(row35, 12, payload.getNguoiLapFullName());

            setCellVal(row1, 13, payload.getToSo());
            setCellVal(row2, 13, payload.getSoTo());
            setCellVal(row2, 6, payload.getMaSo());
            setCellVal(row3, 13, payload.getPDH());
            setCellVal(row3, 6, payload.getSanPham());
            setCellVal(row4, 6, payload.getNoiDung());
            setCellVal(row5, 6, payload.getNguonKinhPhi());

            if (payload.getGiamDocSignImg() != null && !payload.getGiamDocSignImg().isEmpty()) {
                addImageToExcel(workbook, sheet, payload.getGiamDocSignImg(), 1, 5, 2, 6);
            }
            setCellVal(row6, 1, payload.getGiamDocFullName());
            setCellVal(row15, 11, payload.getTongCongDinhMucLaoDong().toString());

            setCellVal(row3, 0, payload.getNgayThangNamGiamDoc());
            setCellVal(row32, 2, payload.getNgayThangNamTPKTHK());
            setCellVal(row32, 6, payload.getNgayThangNamTPKEHOACH());
            setCellVal(row32, 9, payload.getNgayThangNamtpVatTu());
            setCellVal(row32, 12, payload.getNgayThangNamNguoiLap());

            setCellVal(row29, 9, payload.getTongDMVTKho().toString());
            setCellVal(row29, 12, payload.getTongDMVTMuaNgoai().toString());
            setCellVal(row30, 2, payload.getTienLuong().toString());

//

            int totalLine = payload.getDinhMucLaoDongs().size();
            int startFix1 = 15;
            int endFix1 = 35;
            if (totalLine > 5) {
                sheet.copyRows(startFix1, endFix1, endFix1 + (totalLine - 6), new CellCopyPolicy()); // copy and paste

                for (int i = startFix1; i < endFix1 + (totalLine - 6); i++) {
                    sheet.createRow(i);
                    sheet.copyRows(9, 9, i - 1, new CellCopyPolicy()); // copy and paste
                }
            }

            for (int i = 0; i < payload.getDinhMucLaoDongs().size(); i++) {
                XSSFRow crrRow = sheet.getRow(9 + i);
                setCellVal(crrRow, 0, i + 1 + "");
                setCellVal(crrRow, 1, payload.getDinhMucLaoDongs().get(i).getNoiDungCongViec());
                setCellVal(crrRow, 10, payload.getDinhMucLaoDongs().get(i).getBacCV());
                setCellVal(crrRow, 11, payload.getDinhMucLaoDongs().get(i).getDm());
                setCellVal(crrRow, 12, payload.getDinhMucLaoDongs().get(i).getGhiChu());
            }
//
            int soDongBiLech = (totalLine > 5 ? totalLine + 14 : 0);
            int startFix2 = 29 + soDongBiLech;
            int endFix2 = 35 + soDongBiLech;
            int totalLine2 = payload.getDinhMucVatTus().size();
            int row_mau = 20 + soDongBiLech;
            if (totalLine2 > 9) {
                sheet.copyRows(startFix2, endFix2, endFix2 + (totalLine2 - 14), new CellCopyPolicy()); // copy and paste

                for (int i = startFix2; i < endFix2 + (totalLine2 - 14); i++) {
                    sheet.createRow(i);
                    sheet.copyRows(row_mau, row_mau, i - 1, new CellCopyPolicy()); // copy and paste
                }
            }

            // dang in o dong 35 => 34
            // expect 49 => 48
            for (int i = 0; i < payload.getDinhMucVatTus().size(); i++) {
                XSSFRow crrRow2 = sheet.getRow(row_mau + i);
                setCellVal(crrRow2, 0, i + 1 + "");
                setCellVal(crrRow2, 1, payload.getDinhMucVatTus().get(i).getTenVatTuKyThuat());
                setCellVal(crrRow2, 2, payload.getDinhMucVatTus().get(i).getKyMaKyHieu());
                setCellVal(crrRow2, 3, payload.getDinhMucVatTus().get(i).getDvt());
                setCellVal(crrRow2, 4, payload.getDinhMucVatTus().get(i).getDm1SP());
                setCellVal(crrRow2, 5, payload.getDinhMucVatTus().get(i).getSoLuongSanPham());
                setCellVal(crrRow2, 6, payload.getDinhMucVatTus().get(i).getTongNhuCau());
                setCellVal(crrRow2, 7, payload.getDinhMucVatTus().get(i).getKhoDonGia());
                setCellVal(crrRow2, 8, payload.getDinhMucVatTus().get(i).getKhoSoLuong());
                setCellVal(crrRow2, 9, payload.getDinhMucVatTus().get(i).getKhoThanhTien());
                setCellVal(crrRow2, 10, payload.getDinhMucVatTus().get(i).getMnDonGia());
                setCellVal(crrRow2, 11, payload.getDinhMucVatTus().get(i).getMnSoLuong());
                setCellVal(crrRow2, 12, payload.getDinhMucVatTus().get(i).getMnThanhTien());
                setCellVal(crrRow2, 13, payload.getDinhMucVatTus().get(i).getGhiChu());
            }

            int rowMax = 34;
            if (totalLine > 5) {
                rowMax += soDongBiLech;
            }
            if (totalLine2 > 9) {
                rowMax += 3 + (totalLine2 - 9);
            }
            if (payload.getTruongPhongKTHKSignImg() != null && !payload.getTruongPhongKTHKSignImg().isEmpty()) {
                addImageToExcel(workbook, sheet, payload.getTruongPhongKTHKSignImg(), 2, rowMax, 3, rowMax + 1);
            }
            if (payload.getTruongPhongKeHoachSignImg() != null && !payload.getTruongPhongKeHoachSignImg().isEmpty()) {
                addImageToExcel(workbook, sheet, payload.getTruongPhongKeHoachSignImg(), 5, rowMax, 8, rowMax + 1);
            }
            if (payload.getTruongPhongVatTuSignImg() != null && !payload.getTruongPhongVatTuSignImg().isEmpty()) {
                addImageToExcel(workbook, sheet, payload.getTruongPhongVatTuSignImg(), 8, rowMax, 11, rowMax + 1);
            }
            if (payload.getNguoiLapSignImg() != null && !payload.getNguoiLapSignImg().isEmpty()) {
                addImageToExcel(workbook, sheet, payload.getNguoiLapSignImg(), 11, rowMax, 14, rowMax + 1);
            }
            workbook.write(outputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                outputStream.flush();
            } catch (IOException e) {
            }
        }
    }

    public void exportThongKe(InputStream fis, OutputStream outputStream, ThongKePageResponse payloads, String startDateStr, String endDateStr, Long toKH) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheetAt(0);

            XSSFRow row2 = sheet.getRow(2);
            XSSFRow row3 = sheet.getRow(3);
            XSSFRow row4 = sheet.getRow(4);

            setCellVal(row2, 1, startDateStr);
            setCellVal(row2, 3, endDateStr);

            setCellVal(row3, 3, payloads.getTienDo());
            setCellVal(row3, 1, payloads.getSanPham());

            if (!Objects.isNull(toKH)) {
                setCellVal(row4, 1, userService.findById(toKH).getAlias());
            }

            List<ThongKeDetailPayload> thongKeDetailPayloadList = payloads.getDetails();

            int totalLine = thongKeDetailPayloadList.size();
            if (totalLine > 100) {

                for (int i = 0; i < totalLine - 100; i++) {
                    sheet.createRow(110 + i);
                    sheet.copyRows(9, 9, 110 + i, new CellCopyPolicy()); // copy and paste
                }
            }

            int i = 1;
            for (ThongKeDetailPayload item : thongKeDetailPayloadList) {

                XSSFRow rowdata = sheet.getRow(i + 6);
                setCellVal(rowdata, 0, String.valueOf(i));
                setCellVal(rowdata, 1, item.getTenPhuKien());
                setCellVal(rowdata, 2, item.getTenLinhKien());
                setCellVal(rowdata, 3, item.getKyHieu());
                setCellVal(rowdata, 4, String.valueOf(item.getSL()));
                setCellVal(rowdata, 5, item.getDangHuHong());

                setCellVal(rowdata, 6, item.getToTruongFullName());

                setCellVal(rowdata, 7, item.getNgayKiemHong());
                setCellVal(rowdata, 8, item.getPhuongPhapKhacPhuc());
                setCellVal(rowdata, 9, item.getNgayChuyenPhongVatTu());
                setCellVal(rowdata, 10, item.getSoPhieuDatHang());
                setCellVal(rowdata, 11, item.getNgayChuyenKT());
                setCellVal(rowdata, 12, item.getSoPA());
                setCellVal(rowdata, 13, item.getNgayRaPA());
                setCellVal(rowdata, 14, item.getNgayChuyenKH());
                setCellVal(rowdata, 15, item.getNgayPheDuyet());
                if (!Objects.isNull(item.getSoCNTP())) {
                    setCellVal(rowdata, 16, "CNTP-" + item.getSoCNTP());
                }
                setCellVal(rowdata, 17, item.getNgayHoanThanh());
                setCellVal(rowdata, 18, item.getXacNhanHoanThanh());
                i++;
            }

            workbook.write(outputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                outputStream.flush();
            } catch (IOException e) {
            }
        }
    }

    private void setCellVal(Row row, int cell, String val) {
        row.getCell(cell).setCellValue(val);
    }

    private String fillUserInfo(Long userId, Map<Long, User> userById) {
        if (Objects.isNull(userId) || !userById.containsKey(userId)) {
            return "";
        }
        return userById.get(userId).getAlias();
    }

    @Override
    public void exports(Long startDate, Long endDate, Long spId, Long toTruongId, Integer pagenum, Integer size) {
        mkdirs(base_dir_kiem_hong);
        mkdirs(base_dir_dat_hang);
        mkdirs(base_dir_pa);
        mkdirs(base_dir_cntp);
        mkdirs(base_dir_thong_ke);

        Page<Request> page = requestRepository.findPaging(of(0, Integer.MAX_VALUE), startDate, endDate);
        Map<Long, User> userById = userService.userById();
        try {
            if (!page.isEmpty()) {
                Map<Long, String> mdsdNameById = mdsdMap();
                for (Request request : page) {
                    if (Objects.nonNull(request.getKiemHong()) && request.getKiemHong().allApproved()) {
                        try (FileOutputStream os = new FileOutputStream(base_dir_kiem_hong + kiemHongPrefix + request.getKiemHong().getKhId() + ".xlsx");
                             FileInputStream fis = new FileInputStream(new File("./src/main/resources/templates/1_Kiem_Hong.xlsx"))) {
                            KiemHongPayLoad payload = KiemHongPayLoad
                                    .fromEntity(request.getKiemHong())
                                    .andRequestId(request.getRequestId());
                            payload.processSignImgAndFullName(userById);

                            exportKiemHong(fis, os, payload);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (Objects.nonNull(request.getPhieuDatHang()) && request.getPhieuDatHang().allApproved()) {
                        try (FileOutputStream os = new FileOutputStream(base_dir_dat_hang + datHangPrefix + CommonUtils.removeAllSpecialCharacters(request.getPhieuDatHang().getSo()) + ".xlsx");
                             FileInputStream fis = new FileInputStream(new File("./src/main/resources/templates/2_Dat_Hang.xlsx"))) {
                            PhieuDatHangPayload payload = PhieuDatHangPayload
                                    .fromEntity(request.getPhieuDatHang());

                            payload.setRequestId(request.getRequestId());
                            payload.processSignImgAndFullName(userById);
                            exportPHieuDatHang(fis, os, payload, mdsdNameById);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
            // TODO: find pa.
            List<PhuongAn> pas = phuongAnRepository.findAll();
            if (!CollectionUtils.isEmpty(pas)) {
                for (PhuongAn pa : pas) {
                    if (pa.allApproved()) {
                        try (FileOutputStream os = new FileOutputStream(base_dir_pa + paPrefix + CommonUtils.removeAllSpecialCharacters(pa.getMaSo()) + ".xlsx");
                             FileInputStream fis = new FileInputStream(new File("./src/main/resources/templates/3_phuong_an.xlsx"))) {
                            logger.info("Exporting PA with id: {}", pa.getPaId());
                            PhuongAnPayload payload = PhuongAnPayload.fromEntity(pa);
                            payload.setRequestId(pa.getPaId());
                            payload.processSignImgAndFullName(userById);
                            exportPhuongAn(fis, os, payload);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
            // TODO: find CNTP
            List<CongNhanThanhPham> cntps = congNhanThanhPhamRepository.findAll();
            for (CongNhanThanhPham cntp : cntps) {
                if (cntp.allApproved()) {
                    try (FileOutputStream os = new FileOutputStream(base_dir_cntp + cntpPrefix + cntp.getTpId() + ".xlsx");
                         FileInputStream fis = new FileInputStream(new File("./src/main/resources/templates/4_cntp.xlsx"))) {
                        logger.info("Exporting CNTP with id: {}", cntp.getTpId());
                        CongNhanThanhPhamPayload payload = CongNhanThanhPhamPayload.fromEntity(cntp);
                        payload.setRequestId(cntp.getTpId());
                        payload.processSignImgAndFullName(userById);
                        exportCNTP(fis, os, payload);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            // TODO: find TDSP
            ThongKePageResponse tkPayload = setThongKePayLoad(startDate, endDate, spId, toTruongId);

            Date nowDate = new Date(System.currentTimeMillis());
            Date fromDate = new Date(startDate);
            Date toDate = new Date(endDate);

            String nowDateStr = CommonUtils.convertDateToPatern(nowDate, "yyyyMMdd");
            String fromDateStr = CommonUtils.convertDateToPatern(fromDate, "yyyy-MM-dd");
            String toDateStr = CommonUtils.convertDateToPatern(toDate, "yyyy-MM-dd");


            try (FileOutputStream os = new FileOutputStream(base_dir_thong_ke + thongKePrefix + nowDateStr + ".xlsx");
                 FileInputStream fis = new FileInputStream(new File("./src/main/resources/templates/5_ThongKeTienDoSP.xlsx"))) {
                logger.info("Exporting Thong ke with date: {}", nowDate);
                exportThongKe(fis, os, tkPayload, fromDateStr, toDateStr, toTruongId);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private ThongKePageResponse setThongKePayLoad(Long startDate, Long endDate, Long spId, Long toTruongId) {
        ThongKePageRequest thongKeRequest = new ThongKePageRequest();
        thongKeRequest.setSanPham(spId);
        thongKeRequest.setFromDate(startDate);
        thongKeRequest.setToDate(endDate);
        thongKeRequest.setPage(1);
        thongKeRequest.setSize(10000);
        thongKeRequest.setToTruongId(toTruongId);
        return requestService.collectDataThongKe(thongKeRequest);

    }


    private void mkdirs(String dirPath) {
        try {
            Path path = Paths.get(dirPath);
            //if directory exists?
            if (!Files.exists(path)) Files.createDirectories(path);
        } catch (Exception e) {
            //fail to create directory
            e.printStackTrace();
        }

    }

    private Map<Long, String> mdsdMap() {
        return mucDichSuDungRepository
                .findAll()
                .stream()
                .collect(Collectors.toMap(MucDichSuDung::getMdId, e -> e.getTen()));
    }

    private String getVal(Map<Long, String> map, Long key) {
        if (key == null) {
            return "";
        }
        if (map.containsKey(key)) {
            return map.get(key);
        } else return "";
    }

    private String getVal(String val1) {
        if (val1 == null) {
            return "";
        }
        return val1;
    }

    private void addImageToExcel(XSSFWorkbook workbook, XSSFSheet sheet, String imageBase64, int col1, int row1, int col2, int row2) {
        String imageStr = imageBase64.replace("data:image/png;base64,", "");
        byte[] bytes = Base64.decodeBase64(imageStr);

        //Adds a picture to the workbook
        int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
        CreationHelper helper = workbook.getCreationHelper();
        //Creates the top-level drawing patriarch.
        Drawing drawing = sheet.createDrawingPatriarch();

        //Create an anchor that is attached to the worksheet
        ClientAnchor anchor = helper.createClientAnchor();

        //create an anchor with upper left cell _and_ bottom right cell
        anchor.setCol1(col1); //Column B
        anchor.setRow1(row1); //Row 3
        anchor.setCol2(col2); //Column C
        anchor.setRow2(row2); //Row 4

        //Creates a picture
        Picture pict = drawing.createPicture(anchor, pictureIdx);
    }
}
