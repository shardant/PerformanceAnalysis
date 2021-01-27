import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainApplication {
    public static void main(String[] args) throws Exception {
        //执行jmeter脚本
        JmeterUtil jmeterUtil = new JmeterUtil();
        String casePath = jmeterUtil.getCasePath();
        String resultPath = jmeterUtil.getResultPath();
        String reportPath = jmeterUtil.getReportPath();
        String excelPath = reportPath + File.separator + "性能测试记录模板.xlsx";

        ArrayList<String> jmxfileList = jmeterUtil.getJmxFileList(casePath);
        for(String jmxFileName:jmxfileList){
            String jtlFileName = resultPath + File.separator + jmeterUtil.getReportFileName(jmxFileName) + ".jtl";
            String htmlFileName = reportPath + File.separator + jmeterUtil.getReportFileName(jmxFileName);
            File jmxFile = new File(casePath + File.separator + jmxFileName);
            File jtlFile = new File(jtlFileName);
            File htmlFile = new File(htmlFileName);
            Map map = new HashMap();
            map.put("jmxFile", jmxFile);
            map.put("jtlFile", jtlFile);
            map.put("htmlFile",htmlFile);
            String threadCount = getTheadCount(excelPath, jmxFileName);
            jmeterUtil.excuteJmeterRunByScript(threadCount, map);
            String scenarios = getScenariosByJmx(excelPath, jmxFileName);
            htmlToExcel(excelPath, htmlFileName, scenarios,threadCount);
            //判断ART是否超过预期值，大于预期10%则减小线程数，小于预期10%则加大线程数，10%范围内则结束此脚本执行。
            String expectAverageRT = getExpectAverageRT(excelPath, jmxFileName);

        }
    }

    private static String getExpectAverageRT(String excelPath, String jmxFileName) throws Exception {
        String expectAverageRT = "";
        ExcelUtil excelUtil = new ExcelUtil(excelPath);
        String sheetNamePlan = "测试方案";
        int row = excelUtil.findCellByContent(sheetNamePlan,jmxFileName)[0];
        int col = excelUtil.findCellByContent(sheetNamePlan,"预期Average(ms)")[1];
        expectAverageRT = excelUtil.getCell(sheetNamePlan,row,col);
        return expectAverageRT;
    }

    private static String getTheadCount(String excelPath, String jmxFileName) throws Exception {
        String theadCount = "";
        ExcelUtil excelUtil = new ExcelUtil(excelPath);
        String sheetNamePlan = "测试方案";
        int rowNum = excelUtil.findCellByContent(sheetNamePlan,jmxFileName)[0];
        int colNum = excelUtil.findCellByContent(sheetNamePlan,"预期并发数")[1];
        theadCount = excelUtil.getCell(sheetNamePlan, rowNum, colNum);
        return theadCount;
    }

    private static String getScenariosByJmx(String excelPath, String jmxFileName) throws Exception {
        String scenarios = "";
        ExcelUtil excelUtil = new ExcelUtil(excelPath);
        String sheetNamePlan = "测试方案";
        int rowNum = excelUtil.findCellByContent(sheetNamePlan,jmxFileName)[0];
        int colNum = excelUtil.findCellByContent(sheetNamePlan,"服务端测试场景")[1];
        scenarios = excelUtil.getCell(sheetNamePlan, rowNum, colNum);
        return scenarios;
    }

    private static void htmlToExcel(String excelPath, String htmlFileName, String scenarios, String threadCount) throws IOException {
        ExcelUtil excelUtil = new ExcelUtil(excelPath);
        String sheetNameRecord = "测试记录";
        HtmlUtil htmlUtil = new HtmlUtil();
        ArrayList<String> reportInformationList =  htmlUtil.readHtmlByCssQuery(htmlFileName + File.separator + "index.html", "table>tbody>tr>td");
        JsonObject jsonObject = htmlUtil.readFromJsonFile(htmlFileName + File.separator + "statistics.json");
        //从有效行数下一行开始写
        int rowStart = excelUtil.getEffectiveRowNum(sheetNameRecord) + 1;
        excelUtil.setCell(sheetNameRecord, rowStart, 0,reportInformationList.get(1));
        excelUtil.setCell(sheetNameRecord, rowStart, 1,reportInformationList.get(3));
        excelUtil.setCell(sheetNameRecord, rowStart, 2,reportInformationList.get(5));
        excelUtil.setCell(sheetNameRecord, rowStart, 3, threadCount);
        //
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            int colStart = 4;//从第4列开始写
            if(entry.getKey().contains(scenarios)) {
                for (Map.Entry<String, JsonElement> entryChild : entry.getValue().getAsJsonObject().entrySet()) {
                    String value = entryChild.getValue().getAsString();
                    excelUtil.setCell(sheetNameRecord,rowStart,colStart,value);
                    colStart = colStart + 1;
                }
                break;
            }
        }
        excelUtil.save(excelPath);
        excelUtil.close();
    }
}
