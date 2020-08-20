package com.davidlong.demo.pdf.serivce;

import com.davidlong.creeper.execution.context.ContextParamStore;
import com.davidlong.demo.pdf.model.PDFDetail;
import com.davidlong.demo.pdf.model.PDFListBook;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class DZSWService {
    public static int getMaxPageNum(Document rootPage,ContextParamStore contextParamStore) {
        Element pagenavi = rootPage.getElementById("pagenavi");
        Elements as = pagenavi.getElementsByTag("a");
        Element element = as.get(as.size() - 1);
        String href = element.attr("href");
        String substring = href.substring(href.indexOf('_')+1, href.indexOf(".html"));
        return Integer.parseInt(substring);
    }

    public static int createPDFJsonInfo(Document rootPage,ContextParamStore contextParamStore) {
        Element pagenavi = rootPage.getElementById("pagenavi");
        Elements as = pagenavi.getElementsByTag("a");
        Element element = as.get(as.size() - 1);
        String href = element.attr("href");
        String substring = href.substring(href.indexOf('_')+1, href.indexOf(".html"));
        return Integer.parseInt(substring);
    }

    public static void handlePDFDetail(Document rootPage,ContextParamStore contextParamStore) {
        Map<String,PDFListBook> pdfListBookMap = (Map<String,PDFListBook>) contextParamStore.getValue("pdfListBookMap");
        String detailUrl = (String) contextParamStore.getValue("detailUrl");
        PDFListBook pdfListBook = pdfListBookMap.get(detailUrl);

        Map<String,PDFDetail> pdfDetailMap = (Map<String,PDFDetail>) contextParamStore.getValue("pdfDetailMap");
        if(pdfDetailMap==null){
            pdfDetailMap=new LinkedHashMap<>();
            contextParamStore.addParam("pdfDetailMap",pdfDetailMap);
        }

        Set<String> pdfGetFileUrls = (Set<String>) contextParamStore.getValue("pdfGetFileUrls");
        if(pdfGetFileUrls==null){
            pdfGetFileUrls=new LinkedHashSet<>();
            contextParamStore.addParam("pdfGetFileUrls",pdfGetFileUrls);
        }

        PDFDetail pdfDetail = new PDFDetail();
        pdfDetail.setDetailUrl((String) contextParamStore.getValue("detailUrl"));

        Element downarea = rootPage.getElementsByClass("downarea").get(0);
        Elements as = downarea.getElementsByTag("a");
        String downUrl = as.get(0).attr("href");

        pdfDetail.setDownloadUrl(downUrl);

        Elements ps = rootPage.getElementsByTag("p");
        List<String> lines=new ArrayList<>(ps.size());
        String currentInfo="";
        StringBuilder authorDetailBuilder=new StringBuilder();
        StringBuilder contentDetiailBuilder=new StringBuilder();
        for (Element p : ps) {
            String style = p.attr("style");
            if(style!=null && style.contains("text-align: left;")){
                String text = p.html();
                if(text.contains("strong")){
                    if(text.contains("书名")){
                        currentInfo="书名";
                        String bookName = p.getElementsByTag("strong").get(0).text();
                        pdfDetail.setBookName(bookName);
                    }else if(text.contains("作者简介")){
                        currentInfo="作者简介";
                    }else if(text.contains("内容简介")){
                        currentInfo="内容简介";
                    }
                }else{
                    if(currentInfo.equals("书名")){
                        String[] split = text.split("：");
                        if(split==null || split.length!=2){
                            continue;
                        }
                        if(text.contains("作者")){
                            pdfDetail.setAuthor(split[1].trim());
                        }else if(text.contains("出版社")){
                            pdfDetail.setPublisher(split[1].trim());
                        }else if(text.contains("译者")){
                            pdfDetail.setTranslater(split[1].trim());
                        }else if(text.contains("副标题")){
                            pdfDetail.setSubTitle(text);
                        }else if(text.contains("出版年")){
                            pdfDetail.setPublishTime(split[1].trim());
                        }else if(text.contains("页数")){
                            pdfDetail.setPageNum(split[1].trim());
                        }else if(text.contains("类别")){
                            pdfDetail.setCategory(split[1].trim());
                        }else if(text.contains("格式")){
                            pdfDetail.setFormat(split[1].trim());
                        }else if(text.contains("ISBN")){
                            pdfDetail.setIsbn(split[1].trim());
                        }
                    }else if(currentInfo.equals("作者简介")){
                        authorDetailBuilder.append(text).append("\n");
                    }else if(currentInfo.equals("内容简介")){
                        contentDetiailBuilder.append(text).append("\n");
                    }
                }
            }
        }
        pdfDetail.setAuthorDetail(authorDetailBuilder.toString());
        pdfDetail.setContentDetail(contentDetiailBuilder.toString());
        String downloadUrl = pdfDetail.getDownloadUrl();
        int i = downloadUrl.indexOf("file/");
        String getFileUrl="getfile.php?passcode=&ref=&f=";
        if(i!=-1){
            getFileUrl += downloadUrl.substring(i + 5).trim();
        }else if((i = downloadUrl.indexOf("fs/"))!=-1){
            getFileUrl += downloadUrl.substring(i + 3).trim();
        }
        pdfGetFileUrls.add(getFileUrl);

        pdfListBook.setDetail(pdfDetail);
        pdfDetailMap.put(getFileUrl,pdfDetail);
    }

    public static void handlePDFListBook(Document rootPage,ContextParamStore contextParamStore){
        Element contentleft = rootPage.getElementById("contentleft");
        Elements lis = contentleft.getElementsByTag("li");
        Map<String,PDFListBook> pdfListBookMap = (Map<String,PDFListBook>) contextParamStore.getValue("pdfListBookMap");
        if(pdfListBookMap==null){
            pdfListBookMap=new LinkedHashMap<>();
            contextParamStore.addParam("pdfListBookMap",pdfListBookMap);
        }
        Set<String> urlSet=new LinkedHashSet<>();
        boolean isZhanzhu=true;
        for (Element li : lis) {
            if(isZhanzhu){
                isZhanzhu=false;
                continue;
            }
            try {
                Elements as = li.getElementsByTag("a");
                Element element = as.get(0);
                String href = element.attr("href");
                Elements img = element.getElementsByTag("img");
                String dataOriginal = img.attr("data-original");
                String title = img.attr("title");
                if (title.contains("pdf") || title.contains("PDF")) {
                    int pdf = title.indexOf("pdf");
                    int PDF = title.indexOf("PDF");
                    if (pdf!=-1) {
                        title = title.substring(0, pdf).trim();
                    }
                    if (PDF!=-1) {
                        title = title.substring(0, PDF).trim();
                    }
                }
                PDFListBook pdfListBook = new PDFListBook();
                pdfListBook.setPageUrl(href);
                pdfListBook.setBookImageUrl(dataOriginal);
                pdfListBook.setTitle(title);

                Elements classAttr = li.getElementsByAttributeValue("class", "attr");
                Element attr = classAttr.get(0);
                Elements children = attr.children();
                String date = children.get(0).text();
                String category = children.get(1).getElementsByTag("a").text();
                String viewStr = children.get(2).text();
                String[] split = viewStr.split("：");
                int viewNum =Integer.parseInt(split[1]);

                pdfListBook.setDate(date);
                pdfListBook.setViewNum(viewNum);
                pdfListBook.setCategory(category);
                if(children.size()==4){
                    Element tags = children.get(3);
                    Elements aas = tags.getElementsByTag("a");
                    List<String> tagsList=new ArrayList<>(aas.size());
                    for (Element aa : aas) {
                        tagsList.add(aa.text());
                    }
                    pdfListBook.setTags(tagsList);
                }
                urlSet.add(href);
                pdfListBookMap.put(href,pdfListBook);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        contextParamStore.addParam("pagePDFDetailUrls",urlSet);
    }
}
