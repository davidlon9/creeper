package demo.cocodiy.service;

import com.dlong.creeper.execution.context.ContextParamStore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CoGiftService {

    public void resolveGiftDetail(String result, ContextParamStore contextParamStore) {
        Document rootPage = Jsoup.parse(result);
        Element detailDiv = rootPage.getElementsByClass("detail_pro_cont").get(0);
        String title = detailDiv.getElementsByClass("pro-title").get(0).text();
        String desc = detailDiv.getElementsByClass("pro-tips").get(0).text();
        String favNum = detailDiv.getElementsByClass("zans").get(0).text();
        Elements imgs = detailDiv.getElementsByTag("img");
        Set<String> imgUrls=new HashSet<>();
        for (Element img : imgs) {
            String imgUrl = img.attr("src");
            imgUrls.add(imgUrl);
        }
        contextParamStore.addParam("imgUrls",imgUrls);
    }
}
