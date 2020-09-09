package demo.cocodiy.service;

import demo.cocodiy.model.Cat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CatService {
    public Set<String> resvoleLeafCatGiftPaths(String result){
        Set<String> set=new HashSet<>();
        Document rootPage = Jsoup.parse(result);
        Elements goodsDivs = rootPage.getElementsByClass("goods-box");
        for (Element goodsDiv : goodsDivs) {
            String path = goodsDiv.getElementsByTag("a").get(0).attr("href");
            set.add(path.substring(1,path.length()));
        }
        return set;
    }

    public List<Cat> resolveLeafCats(String result) throws IOException {
        List<Cat> catList = getCatList(result);
        List<Cat> leafCats = new ArrayList<>();
        for (Cat cat : catList) {
            addPathCat(cat,leafCats);
        }
        return leafCats;
    }


    public void addPathCat(Cat cat, List<Cat> list){
        String path = cat.getPath();
        List<Cat> childs = cat.getChilds();
        if(path!=null && childs.size()==0){
            list.add(cat);
        }
        if (childs.size()>0) {
            for (Cat child : childs) {
                addPathCat(child, list);
            }
        }
    }

    public List<Cat> getCatList(String result) {
        Document rootPage = Jsoup.parse(result);
        Elements links = rootPage.getElementsByClass("leftbar").get(0).getElementsByClass("link");

        List<Cat> catList = new ArrayList<>();
        for (Element link : links) {
            Element titleDiv = link.getElementsByClass("lbr").get(0);
            Element a = titleDiv.children().get(0);
            String path1 = a.attr("href");
            String title1 = a.text();
            //一级分类
            Cat cat1 = new Cat(title1, path1.substring(1,path1.length()));
            Elements topics = link.getElementsByClass("topic");
            for (Element topic : topics) {
                String title2 = topic.getElementsByClass("title").text();
                //二级分类
                Cat cat2 = new Cat(title2);
                Elements aList = topic.getElementsByClass("link-a");
                for (Element linkA : aList) {
                    String path3 = linkA.attr("href");
                    //三级分类
                    String title3 = linkA.text();
                    Cat cat3 = new Cat(title3,path3.substring(1,path3.length()));
                    cat2.addChild(cat3);
                }
                cat1.addChild(cat2);
            }
            catList.add(cat1);
        }
        return catList;
    }

}
