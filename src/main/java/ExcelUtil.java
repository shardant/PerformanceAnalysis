import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;


public class ExcelUtil {
    InputStream in = null;
    OutputStream out = null;
    Workbook workbook = null;
    ExcelUtil(String pathname) throws IOException {
        in = new FileInputStream(pathname);
        workbook=new XSSFWorkbook(in);
    }

    /**
     * 保存excel文件
     * @param pathname
     * @throws IOException
     */
    public void save(String pathname) throws IOException {
        out = new FileOutputStream(pathname);
        workbook.write(out);
    }

    /**
     * 关闭excel文件
     * @throws IOException
     */
    public void close() throws IOException {
        if (out != null)
            out.close();
        if (in != null)
            in.close();
        if (workbook != null)
            workbook.close();
    }

    /**
     * 获取excel有效行数
     * @param sheetName
     * @return
     */
    public int getEffectiveRowNum(String sheetName){
        Sheet sheet = workbook.getSheet(sheetName);
        return sheet.getLastRowNum();
    }

    /**
     * 获取excel单元格内容
     * @param sheetName
     * @param rowNum
     * @param colNum
     * @return
     * @throws Exception
     */
    public String getCell(String sheetName, int rowNum, int colNum) throws Exception {
        Sheet sheet = workbook.getSheet(sheetName);
        Row row = sheet.getRow(rowNum);
        if(row == null)
            return "";
        Cell cell = row.getCell(colNum);
        if(cell == null)
            return "";
        return cell.toString();
    }

    /**
     * 设置excel单元格内容
     * @param sheetName
     * @param rowNum
     * @param colNum
     * @param value
     */
    public void setCell(String sheetName, int rowNum, int colNum, String value){
        Sheet sheet = workbook.getSheet(sheetName);
        Row row = sheet.getRow(rowNum);
        if(row == null)
            row = sheet.createRow(rowNum);
        Cell cell = row.getCell(colNum);
        if(cell == null){
            cell = row.createCell(colNum);
        }
        cell.setCellValue(value);
    }

    /**
     *
     * @param sheetName
     * @param cellContent
     * @return
     */
    public int[] findCellByContent(String sheetName, String cellContent){
        int[] cellAddress = new int[]{};
        Sheet sheet = workbook.getSheet(sheetName);
        for(Row row : sheet) {
            if(row == null)
                continue;
            for (Cell cell : row) {
                if(cell == null)
                    continue;
                if (cell.toString().equals(cellContent)) {
                    cellAddress = new int[]{row.getRowNum(), cell.getColumnIndex()};
                    return cellAddress;
                }
            }
        }
        return cellAddress;
    }
}
