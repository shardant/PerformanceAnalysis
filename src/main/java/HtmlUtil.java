import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class HtmlUtil {
    /**
     * 根据cssQuery获取html的内容
     * @param pathName
     * @param cssQuery
     * @return
     * @throws IOException
     */
    public ArrayList<String> readHtmlByCssQuery(String pathName, String cssQuery) throws IOException {
        File input = new File(pathName);
        Document document = Jsoup.parse(input, "UTF-8");
        Elements rows = document.select(cssQuery);
        ArrayList<String> reportInformationList = new ArrayList<>();
        for(Element row : rows){
            reportInformationList.add(row.text());
        }
        return reportInformationList;
    }

    public JsonObject readFromJsonFile(String pathName) throws IOException {
        File input = new File(pathName);
        String jsonstr = FileUtils.readFileToString(input, "gbk");
        JsonObject jsonObject = new JsonParser().parse(jsonstr).getAsJsonObject();
        return jsonObject;
    }
}
