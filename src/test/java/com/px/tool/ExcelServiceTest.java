package com.px.tool;

import com.px.tool.infrastructure.service.ExcelService;
import com.px.tool.infrastructure.service.impl.ExcelServiceImpl;
import com.px.tool.infrastructure.utils.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelServiceTest {

    private ExcelService excelService;

    private void printCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case BOOLEAN:
                System.out.print(cell.getBooleanCellValue());
                break;
            case STRING:
                System.out.print(cell.getRichStringCellValue().getString());
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    System.out.print(cell.getDateCellValue());
                } else {
                    System.out.print(cell.getNumericCellValue());
                }
                break;
            case FORMULA:
                System.out.print(cell.getCellFormula());
                break;
            case BLANK:
            default:
                System.out.print("");
                break;
        }

        System.out.print("\t");
    }

    @Before
    public void init() {
        excelService = new ExcelServiceImpl();
    }

    @Test
    public void exportFile() throws IOException {
//        excelService.exportFile();
        FileUtils.copy(new File("/mnt/project/Sources/NGHIA/free/px-toool/nghia-file/temp.xlsx"), new File("/mnt/project/Sources/NGHIA/free/px-toool/nghia-file/temp2.xlsx"));
    }

    @Test
    public void appendFile() throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File("/mnt/project/Sources/NGHIA/free/px-toool/src/main/resources/templates/1_Kiem_Hong.xlsx")));
        XSSFSheet sheet = workbook.getSheetAt(0);
        int totalLine = 50;
        if (1 > -2) {
            sheet.copyRows(24, 27, 27 + (totalLine - 18), new CellCopyPolicy()); // copy and paste

            for (int i = 24; i < 27 + (totalLine - 18); i++) {
                sheet.createRow(i);
                sheet.copyRows(6, 6, i - 1, new CellCopyPolicy()); // copy and paste
            }
        }
        XSSFRow row0 = sheet.getRow(0);
        XSSFRow row1 = sheet.getRow(1);
        XSSFRow row2 = sheet.getRow(2);


        setCellVal(row0, 4, "val_Tên VKTBKT ");
        setCellVal(row0, 6, "val_Số Hiệu ");
        setCellVal(row0, 8, "val_Tờ số ");
        setCellVal(row1, 2, "val_ Phan xuong");
        setCellVal(row1, 4, "val_ Nguồn v ");
        setCellVal(row1, 6, "val_ số XX ");
        setCellVal(row1, 8, "val_ số tờ ");
        setCellVal(row2, 2, "val_ tổ sx ");
        setCellVal(row2, 4, "val_ công đoạn");

        for (int i = 0; i < totalLine; i++) {
            XSSFRow currRow = sheet.getRow(5 + i);
            setCellVal(currRow, 0, "val_ TT");
            setCellVal(currRow, 1, "val_ tên phụ kiện");
            setCellVal(currRow, 3, "val_ tên linh kiện");
            setCellVal(currRow, 4, "val_ ký hiệu");
            setCellVal(currRow, 5, "val_ SL");
            setCellVal(currRow, 6, "val_ Dạng hư hỏng");
            setCellVal(currRow, 7, "val_ pp khắc phục");
            setCellVal(currRow, 8, "val_ ng kiểm hỏng");
        }


        FileOutputStream out = new FileOutputStream("/mnt/project/Sources/NGHIA/free/px-toool/src/main/resources/templates/new_Kiem_Hong.xlsx");
        workbook.write(out);
        out.close();
    }

    private void setCellVal(Row row, int cell, String val) {
        row.getCell(cell).setCellValue(val);
    }


    @Test
    public void exportDatHang() throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File("/mnt/project/Sources/NGHIA/free/px-toool/src/main/resources/templates/2_Dat_Hang.xlsx")));
        XSSFSheet sheet = workbook.getSheetAt(0);
        int totalLine = 40;
        XSSFRow row13 = sheet.getRow(13);
        setCellVal(row13, 2, "kye1");
        setCellVal(row13, 6, "kye2");
        setCellVal(row13, 8, "kye3");

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


        setCellVal(row0, 6, "Số");
        setCellVal(row1, 6, "Đơn vị yêu cầu ");
        setCellVal(row1, 8, "Phân Xưởng");

        setCellVal(row2, 6, "Nội Dung");


        for (int i = 0; i < totalLine; i++) {
            XSSFRow crrRow = sheet.getRow(6 + i);
            setCellVal(crrRow, 0, "TT");
            setCellVal(crrRow, 1, "Tên pHụ kiện");
            setCellVal(crrRow, 2, "Tên Vật Tư");
            setCellVal(crrRow, 3, "Mã Kí Hiệu");
            setCellVal(crrRow, 4, "DVT");
            setCellVal(crrRow, 5, "SL");
            setCellVal(crrRow, 6, "Mục Đích ");
            setCellVal(crrRow, 7, "Phương pháp kahwsc phục");
            setCellVal(crrRow, 8, "Số pghieesu đặt hàng ");
            setCellVal(crrRow, 9, "người thự hiện");
        }
        FileOutputStream out = new FileOutputStream("/mnt/project/Sources/NGHIA/free/px-toool/src/main/resources/templates/new_Dat_hang.xlsx");
        workbook.write(out);
        out.close();
    }

    @Test
    public void exportCNTP() throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File("./src/main/resources/templates/4_cntp.xlsx")));
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFRow row0 = sheet.getRow(2);
        XSSFRow row1 = sheet.getRow(3);
        XSSFRow row2 = sheet.getRow(4);
        XSSFRow row3 = sheet.getRow(5);
        XSSFRow row4 = sheet.getRow(6);
        XSSFRow row5 = sheet.getRow(7);

        XSSFRow row19 = sheet.getRow(19);
        XSSFRow row21 = sheet.getRow(21);
        XSSFRow row25 = sheet.getRow(25);
        XSSFRow row26 = sheet.getRow(26);

        setCellVal(row0, 1, "Ten san phjam");
        setCellVal(row1, 1, "Noi dung");
        setCellVal(row2, 1, "So phuong an");
        setCellVal(row3, 1, "do vi thuc hien");
        setCellVal(row3, 4, "To lam viec");
        setCellVal(row4, 1, "dn vi dat hang");
        setCellVal(row4, 3, "so luong");
        setCellVal(row4, 5, "DVT");
        setCellVal(row5, 1, "So nghiem thu dc");
        setCellVal(row19, 1, "Lao dong tien luong");
        setCellVal(row19, 3, "gio ");
        setCellVal(row19, 5, "dong");

        setCellVal(row21, 1, "quan doc");
        setCellVal(row21, 4, "tpkcs");

        setCellVal(row25, 0, "ngay_totruong1");
        setCellVal(row25, 1, "ngay_totruong2");
        setCellVal(row25, 2, "ngay_totruong3");
        setCellVal(row25, 3, "ngay_totruong4");
        setCellVal(row25, 4, "ngay_totruong5");

        setCellVal(row26, 0, "totruong1");
        setCellVal(row26, 1, "totruong2");
        setCellVal(row26, 2, "totruong3");
        setCellVal(row26, 3, "totruong4");
        setCellVal(row26, 4, "totruong5");


//

        int totalLine = 40;
        if (totalLine > 5) {
            sheet.copyRows(18, 28, 24 + (totalLine - 6), new CellCopyPolicy()); // copy and paste

            for (int i = 18; i < 24 + (totalLine - 6); i++) {
                sheet.createRow(i);
                sheet.copyRows(12, 12, i - 1, new CellCopyPolicy()); // copy and paste
            }
        }

        for (int i = 0; i < totalLine; i++) {
            XSSFRow crrRow = sheet.getRow(11 + i);
            setCellVal(crrRow, 0, "Nội dung thực hiện");
            setCellVal(crrRow, 3, "KQ");
            setCellVal(crrRow, 4, "Nghiệm Thu");
        }
        FileOutputStream out = new FileOutputStream("/mnt/project/Sources/NGHIA/free/px-toool/src/main/resources/templates/new_CNTP.xlsx");
        workbook.write(out);
        out.close();
    }

    @Test
    public void exportPhuongAn() throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File("./src/main/resources/templates/3_phuong_an.xlsx")));
        XSSFSheet sheet = workbook.getSheetAt(0);

        XSSFRow row1 = sheet.getRow(1);
        XSSFRow row2 = sheet.getRow(2);
        XSSFRow row3 = sheet.getRow(3);
        XSSFRow row4 = sheet.getRow(4);
        XSSFRow row5 = sheet.getRow(5);
        XSSFRow row15 = sheet.getRow(15);
        XSSFRow row29 = sheet.getRow(29);
        XSSFRow row30 = sheet.getRow(30);
        XSSFRow row32 = sheet.getRow(32);

        setCellVal(row1, 13, "To so");
        setCellVal(row2, 13, "so to");
        setCellVal(row2, 6, "Ma soxxxx");
        setCellVal(row3, 13, "PDH");
        setCellVal(row3, 13, "PDH");
        setCellVal(row3, 6, "san pham");
        setCellVal(row4, 6, "noi dung");
        setCellVal(row5, 6, "nguon kinh phi");
        setCellVal(row15, 2, "Tong......");

//

        setCellVal(row32, 1, "TP. KTHK");
        setCellVal(row32, 3, "TP.KẾ HOẠCH");
        setCellVal(row32, 8, "TP. VẬT TƯ");
        setCellVal(row32, 12, "NGƯỜI LẬP");
        setCellVal(row29, 9, "tien huy dong kho");
        setCellVal(row29, 13, "tien mua ngoai");
        setCellVal(row30, 2, "tien luong, tien cong");

//

        int totalLine = 6;
        int startFix1 = 15;
        int endFix1 = 35;
        if (totalLine > 5) {
            sheet.copyRows(startFix1, endFix1, endFix1 + (totalLine - 6), new CellCopyPolicy()); // copy and paste

            for (int i = startFix1; i < endFix1 + (totalLine - 6); i++) {
                sheet.createRow(i);
                sheet.copyRows(9, 9, i - 1, new CellCopyPolicy()); // copy and paste
            }
        }

        for (int i = 0; i < totalLine; i++) {
            XSSFRow crrRow = sheet.getRow(9 + i);
            setCellVal(crrRow, 0, i + 1 + "");
            setCellVal(crrRow, 1, "Nội dung thực hiện" + i);
            setCellVal(crrRow, 10, "Bac");
            setCellVal(crrRow, 11, "DM");
            setCellVal(crrRow, 12, "Ghi chu");
        }
//
        int soDongBiLech = (totalLine > 5 ? totalLine + 14 : 0);
        int startFix2 = 29 + soDongBiLech;
        int endFix2 = 35 + soDongBiLech;
        int totalLine2 = 15;
        int row_mau = 20 + soDongBiLech;
        if (totalLine2 > 9) {
            sheet.copyRows(startFix2, endFix2, endFix2 + (totalLine2 - 14), new CellCopyPolicy()); // copy and paste

            for (int i = startFix2; i < endFix2 + (totalLine2 - 14); i++) {
                sheet.createRow(i);
                sheet.copyRows(row_mau, row_mau, i - 1, new CellCopyPolicy()); // copy and paste
            }
        }
//
//        // dang in o dong 35 => 34
//        // expect 49 => 48
        for (int i = 0; i < totalLine2; i++) {
            XSSFRow crrRow2 = sheet.getRow(row_mau + i);
            setCellVal(crrRow2, 0, i + 1 + "");
            setCellVal(crrRow2, 1, "Ten vat tu");
            setCellVal(crrRow2, 2, "kky ma hieu");
            setCellVal(crrRow2, 3, "dvt");
            setCellVal(crrRow2, 4, "dinh muc 1 sp");
            setCellVal(crrRow2, 5, "so luong san pham");
            setCellVal(crrRow2, 6, "tong nhu cau");
            setCellVal(crrRow2, 7, "don gia");
            setCellVal(crrRow2, 8, "sl");
            setCellVal(crrRow2, 9, "thanh tien");
            setCellVal(crrRow2, 10, "do gia");
            setCellVal(crrRow2, 11, "so luong");
            setCellVal(crrRow2, 12, "thanh tien");
            setCellVal(crrRow2, 13, "ghi chu");
        }

        FileOutputStream out = new FileOutputStream("/mnt/project/Sources/NGHIA/free/px-toool/src/main/resources/templates/new_Phuong_An.xlsx");
        workbook.write(out);
        out.close();
    }

    @After
    public void clean() {

        this.excelService = null;
    }
}
