import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class JmeterUtil {
    /**
     * 获取操作系统信息
     */
    public final static String OS_NAME_LC = System.getProperty("os.name").toLowerCase(java.util.Locale.ENGLISH);

    public final static String JMETER_HOME = "D:\\apache-jmeter-5.3";

    /**
     * 获取Jmeter的bin目录
     * @return
     */
    public String getJmeterHomeBin(){
        String jmeterHomeBin = JMETER_HOME + File.separator + "bin";
        return jmeterHomeBin;
    }

    /**
     * 获取Jmeter的Slave节点
     * @return
     */
    public String getSlaveStr(){
        String slaveStr = "";
        return slaveStr;
    }

    /**
     * 根据操作系统信息获取可以执行的jmeter主程序
     */
    public String getJmeterExc() {
        String jmeterExc = "jmeter";
        if (OS_NAME_LC.startsWith("windows")) {
            jmeterExc = "jmeter.bat";
        }
        return jmeterExc;
    }

    /**
     * 获取Jmeter的script根目录
     * @return
     */
    public String getCasePath(){
        String casePath = JMETER_HOME + File.separator + "script";
        return casePath;
    }

    /**
     * 获取Jmeter的result根目录
     * @return
     */
    public String getResultPath(){
        String resultPath = JMETER_HOME + File.separator + "result";
        return resultPath;
    }

    /**
     * 获取Jmeter的report路径
     * @return
     */
    public String getReportPath(){
        String reportPath = JMETER_HOME + File.separator + "report";
        return reportPath;
    }

    /**
     * 获取Jmeter的jmx文件列表
     * @param casePath
     * @return
     */
    public ArrayList<String> getJmxFileList(String casePath){
        ArrayList<String> jmxFileList = new ArrayList<>();
        File file = new File(casePath);
        if(!file.isDirectory())
            return null;
        File[] files = file.listFiles();
        for(File fileName : files){
            if(fileName.isFile()&&fileName.getName().endsWith("jmx"))
                jmxFileList.add(fileName.getName());
        }
        return jmxFileList;
    }

    /**
     * 获取Jmeter的report文件目录列表
     * @param reportPath
     * @return
     */
    public ArrayList<String> gethtmlFileList(String reportPath){
        ArrayList<String> htmlFileList = new ArrayList<>();
        File file = new File(reportPath);
        if(!file.isDirectory())
            return null;
        File[] files = file.listFiles();
        for (File fileName : files) {
            if (fileName.isDirectory()) {
                htmlFileList.add(fileName.toString());
            }
        }
        return htmlFileList;
    }

    /**
     * 根据Jmeter的jmx文件名称生成jtl/html文件名称
     * @param jmxFileName
     * @return
     */
    public String getReportFileName(String jmxFileName){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentTime = sdf.format(date);
        String fileName = jmxFileName.substring(0,jmxFileName.lastIndexOf(".")) + currentTime;
        return fileName;
    }

    /**
     * 执行Jmeter的脚本文件，采用Apache的commons-exec来执行。
     */
    public void excuteJmeterRunByScript(String threadCount, Map map) throws IOException {
        String jmeterHomeBin = getJmeterHomeBin();
        String jmeterExc = getJmeterExc();
        CommandLine cmdLine = new CommandLine(jmeterHomeBin + File.separator + jmeterExc);
        cmdLine.addArgument("-JthreadCount="+threadCount);
        cmdLine.addArgument("-n");
        cmdLine.addArgument("-t");
        cmdLine.addArgument("${jmxFile}");
        cmdLine.addArgument("-l");
        cmdLine.addArgument("${jtlFile}");
        cmdLine.addArgument("-e");
        cmdLine.addArgument("-o");
        cmdLine.addArgument("${htmlFile}");
        String slaveStr = getSlaveStr();
        if (StringUtils.isNotEmpty(slaveStr)) {
            cmdLine.addArgument("-R");
            cmdLine.addArgument(slaveStr);
        }
        cmdLine.setSubstitutionMap(map);
        System.out.println(cmdLine);
        DefaultExecutor executor = new DefaultExecutor();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);
        executor.setStreamHandler(streamHandler);
        executor.execute(cmdLine);
    }
}

