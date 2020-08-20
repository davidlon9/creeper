package com.davidlong.demo.pdf;

import com.alibaba.fastjson.JSONObject;
import com.davidlong.creeper.annotation.*;
import com.davidlong.creeper.annotation.control.FileRecordsIgnore;
import com.davidlong.creeper.annotation.control.looper.ForEach;
import com.davidlong.creeper.annotation.control.looper.ForIndex;
import com.davidlong.creeper.annotation.handler.AfterMethod;
import com.davidlong.creeper.annotation.handler.BeforeMethod;
import com.davidlong.creeper.annotation.http.Get;
import com.davidlong.creeper.annotation.seq.RequestChain;
import com.davidlong.creeper.annotation.seq.SeqRequest;
import com.davidlong.creeper.control.ContinueAction;
import com.davidlong.creeper.control.ForwardAction;
import com.davidlong.creeper.control.MoveAction;
import com.davidlong.creeper.control.MoveActions;
import com.davidlong.creeper.execution.ChainContextExecutor;
import com.davidlong.creeper.execution.base.ContextExecutor;
import com.davidlong.creeper.execution.context.ContextParamStore;
import com.davidlong.creeper.execution.context.ExecutionContext;
import com.davidlong.creeper.execution.context.FormParamStore;
import com.davidlong.creeper.execution.handler.entity.ExecutionHandler;
import com.davidlong.creeper.model.result.ChainResult;
import com.davidlong.creeper.util.FileUtil;
import com.davidlong.creeper.util.HttpDownload;
import com.davidlong.creeper.util.ResultUtil;
import com.davidlong.demo.pdf.model.PDFDetail;
import com.davidlong.demo.pdf.serivce.DZSWService;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Host(value = "www.xgv5.com", scheme = "https")
@RequestChain(index = 1)
@RequestLog(showFilledParams = false, showFilledHeaders = false)
@FileRecordsIgnore(filePath = "D:\\repository\\traiker\\records\\demo.txt")
public class PDFdzswChain {
//    @RequestReference(index = 0,chainClass = PDFdzswChain.PageHandleChain.DownloadFile.class,requestMethod = "getFileInfo")
//    Method getFileInfo;
//    @ChainReference(index = 0,name = "downFileR")
//    PageHandleChain.DownloadFile downFile;

//    @SeqRequest(index = 1, description = "获取最大页数")
//    @Get("/category-30.html")
//    public ExecutionHandler getMaxPageNum = new GetMaxPageNumHandler();

    @SeqRequest(index = 1,description = "获取最大页数")
    @Get("/category-30.html")
    public ExecutionHandler getMaxPageNum = new ExecutionHandler() {
        @Override
        public Boolean beforeHandle(Request request, ExecutionContext context) throws IOException {
            return true;
        }

        @Override
        public MoveAction afterHandle(HttpResponse response, ExecutionContext context) throws IOException {
            String html = EntityUtils.toString(response.getEntity());
            Document rootPage = Jsoup.parse(html);
            ContextParamStore contextStore = context.getContextStore();
            int maxPageNum = DZSWService.getMaxPageNum(rootPage, contextStore);
            contextStore.addParam("maxPage",maxPageNum);
            contextStore.addParam("isHandlePage",true);
            return MoveActions.FORWARD();
        }
    };

//    @SeqRequest(index = 1,description = "获取最大页数")
//    @Get("/category-30.html")
//    public AfterHandler getMaxPageNum = (response, context) -> {
//        String html = EntityUtils.toString(response.getEntity());
//        Document rootPage = Jsoup.parse(html);
//        ContextParamStore contextStore = context.getContextStore();
//        int maxPageNum = DZSWService.getMaxPageNum(rootPage, contextStore);
//        contextStore.addParam("maxPage",maxPageNum);
//        contextStore.addParam("isHandlePage",true);
//        return MoveActions.FORWARD();
//    };

//
//    @SeqRequest(index = 1,description = "获取最大页数")
//    @Get("/category-30.html")
//    public MoveAction getMaxPageNum(HttpResponse response, ContextParamStore contextParamStore) throws IOException {
//        String html = EntityUtils.toString(response.getEntity());
//        Document rootPage = Jsoup.parse(html);
//        int maxPageNum = DZSWService.getMaxPageNum(rootPage, contextParamStore);
//        contextParamStore.addParam("maxPage",maxPageNum);
//        contextParamStore.addParam("isHandlePage",true);
//        return MoveActions.FORWARD();
//    }

//    //stopConditionExpr使用的是main线程的context
//    //queueElement在线程的context中，mainContext中的变量无法获取
//    @MultiRequestQueue(value = 2,stopConditionExpr = "${#isHandlePage}",queueContextKey = "downQueue",queueElementKey = "downUrl",threadSize = 5, delay = 2000,description = "多线程下载队列")
//    @Get(value = "${#downUrl}",urlInheritable = false)
//    public MoveAction multiDownloadQueue(HttpResponse response,ContextParamStore contextParamStore) throws IOException {
//        String html = EntityUtils.toString(response.getEntity());
//        return new ContinueAction(100);
//    }

    @ForIndex(start = "1", end = "${#maxPage}")
    @RequestChain(index = 2)
    @RequestLog(showFilledParams = false, showFilledHeaders = false)
    public class PageHandleChain {
        @AfterMethod("PageHandleChain")
        public MoveAction afterHandle(ContextParamStore contextParamStore) {
            Set pdfGetFileUrls = (Set) contextParamStore.getValue("pdfGetFileUrls");
            pdfGetFileUrls.clear();
            return new ContinueAction(100);
        }

        @SeqRequest(index = 1, description = "处理列表页面")
        @Get("/category-30${#index==1?'':'_'+#index}.html")
        public MoveAction handlePDFListBook(HttpResponse response, ContextParamStore contextParamStore) throws IOException {
            String html = EntityUtils.toString(response.getEntity());
            Document rootPage = Jsoup.parse(html);
            DZSWService.handlePDFListBook(rootPage, contextParamStore);
            return MoveActions.FORWARD();
        }

        @ForEach(itemsContextKey = "pagePDFDetailUrls", itemName = "detailUrl")
        @SeqRequest(index = 2, description = "处理详情页面")
        @Get(value = "${#detailUrl}", urlInheritable = false)
        @FileRecordsIgnore(filePath = "D:\\repository\\traiker\\records\\demo.txt")
        public MoveAction handlePDFBookDetial(HttpResponse response, ContextParamStore contextParamStore) throws IOException {
            String html = EntityUtils.toString(response.getEntity());
            Document rootPage = Jsoup.parse(html);
            DZSWService.handlePDFDetail(rootPage, contextParamStore);
            return new ContinueAction(100);
        }

        @ForEach(itemsContextKey = "pdfGetFileUrls", itemName = "getFileUrl")
        @Host(value = "webapi.400gb.com", scheme = "https")
        @RequestChain(index = 3)
        @RequestLog(showFilledParams = false, showFilledHeaders = false)
        public class DownloadFile {
            @SeqRequest(index = 1, description = "获取下载文件信息")
            @Get(value = "${#getFileUrl}")
            @RequestHeader(name = "Origin", value = "https://sn9.us")
            public MoveAction getFileInfo(JSONObject result, HttpResponse response, FormParamStore formParamStore, ContextParamStore contextParamStore) throws IOException {
                JSONObject json = ResultUtil.getJsonBody(result);
                String file_id = json.getString("file_id");
                String userid = json.getString("userid");
                String file_chk = json.getString("file_chk");
                formParamStore.addParam("uid", userid);
                formParamStore.addParam("fid", file_id);
                formParamStore.addParam("file_chk", file_chk);
                contextParamStore.addParam("fileName", json.getString("file_name"));
                return new ForwardAction();
            }

            @SeqRequest(index = 2, description = "获取下载链接")
            @Get(value = "/get_file_url.php")
            @Parameters({@Parameter(name = "uid"), @Parameter(name = "fid"), @Parameter(name = "file_chk"),})
            @RequestHeader(name = "Origin", value = "https://sn9.us")
            public MoveAction getPDFDownloadUrl(JSONObject result, HttpResponse response, ContextParamStore contextParamStore) throws IOException {
                String s = EntityUtils.toString(response.getEntity());
                JSONObject json = ResultUtil.getJsonBody(result);
                String downurl = json.getString("downurl");
                if (downurl != null) {
                    contextParamStore.addParam("downUrl", downurl);
                }
                return new ForwardAction();
            }

            @BeforeMethod("downloadSingle")
            public boolean skipDownload(ContextParamStore contextParamStore) {
                Object index = contextParamStore.getValue("index");
                Map pdfDetailMap = (Map) contextParamStore.getValue("pdfDetailMap");
                String getFileUrl = (String) contextParamStore.getValue("getFileUrl");
                PDFDetail pdfDetail = (PDFDetail) pdfDetailMap.get(getFileUrl);
                String fileName;
                String bookName = pdfDetail.getBookName();
                if (bookName != null) {
                    fileName = bookName + ".pdf";
                } else {
                    fileName = (String) contextParamStore.getValue("fileName");
                    bookName = fileName.substring(0, fileName.indexOf(".pdf"));
                }
                String dirPath = "D:\\PDFDownload\\PDF电子书网\\电脑书籍\\Page" + index + "\\";
                String path = dirPath + fileName;
                File file = new File(path);
                contextParamStore.addParam("filePath", path);

                Map pdfListBookMap = (Map) contextParamStore.getValue("pdfListBookMap");
                Object o = pdfListBookMap.get(pdfDetail.getDetailUrl());
                if (o != null) {
                    String descJsonPath = dirPath + bookName + ".json";
                    File descFile = new File(descJsonPath);
                    if (!descFile.exists()) {
                        descFile.getParentFile().mkdirs();
                        try {
                            System.out.println("生成descJson文件" + descJsonPath);
                            FileUtil.write(descFile, JSONObject.toJSONString(o, true));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (file.exists()) {
                    System.out.println("文件" + fileName + "已存在，跳过下载");
                    return false;
                }
                return true;
            }

            @SeqRequest(index = 3, description = "下载文件")
            @Get(value = "${#downUrl}", urlInheritable = false)
            @AfterMethod
            public MoveAction downloadSingle(HttpResponse response, ContextParamStore contextParamStore) throws IOException {
                String filePath = (String) contextParamStore.getValue("filePath");
                if (filePath != null) {
                    HttpDownload.download(response, filePath);
                    System.out.println("成功下载至" + filePath);
                }
                return new ForwardAction();
            }
        }
    }

    public static void main(String[] args) {
        //Chain调用方式
        ContextExecutor executor = new ChainContextExecutor(PDFdzswChain.class);
        ChainResult chainResult = executor.exeucteRootChain();
        System.out.println();

//        //手动调用方式
//        ExecutionContext context = ExecutionContextFactory.createContext(LoginChain.class);
//
//        ContextExecutor contextExecutor = new ChainContextExecutor(context);
//        RequestEntity deviceCookie = context.getRequestEntity("deviceCookie");
//        deviceCookie.setAfterHandler(new AfterHandler() {
//            @Override
//            public MoveAction afterHandle(HttpResponse response, ExecutionContext context) {
//                FormParamStore paramStore = context.getParamStore();
//                paramStore.addParams(new CallbackParam().getExtraParams());
//                return MoveActions.FORWARD();
//            }
//        });
//        //exeucteRequest执行，不会调用处理器
//        contextExecutor.exeucteRequest(deviceCookie.getName(),true);
//
//        //execute执行，同时会调用前后处理器
//        contextExecutor.exeucteRequest("captchaImage");
    }
}
