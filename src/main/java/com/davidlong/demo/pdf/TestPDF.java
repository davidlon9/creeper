package com.davidlong.demo.pdf;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.exceptions.CryptographyException;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdfwriter.ContentStreamWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.util.PDFOperator;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.Splitter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class TestPDF {
    public static void main(String[] args){
        // 结束提取页数
        int endPage;
        PDDocument document=null;
        //pdf文本路径
        String path = "D:\\PDFDownload\\PDF电子书网\\电脑书籍\\Page2\\极简算法史.pdf";
        File file = new File(path);
        try {
            document = PDDocument.load(file);
            boolean encrypted = document.isEncrypted();
            if(encrypted){
                document.decrypt("");
                document.setAllSecurityToBeRemoved(true);
                System.out.println("成功解密："+file.getName());
                document.save(file);
            }
            endPage=document.getNumberOfPages();
            Splitter splitter = new Splitter();
            List<PDDocument> split = splitter.split(document);
            PDFTextStripper pdfTextStripper = new PDFTextStripper();

            PDPage page = (PDPage) document.getDocumentCatalog().getAllPages().get(0);
            //PDF流对象剖析器(这将解析一个PDF字节流并提取操作数,等等)
            PDFStreamParser parser =new PDFStreamParser(page.getContents());
            parser.parse();
            List tokens = parser.getTokens();
            for (int i = 0; i < tokens.size(); i++) {
                Object token = tokens.get(i);
                if (token instanceof PDFOperator) {
                    PDFOperator op= (PDFOperator) token;
                    String name = op.getOperation();
                    if(name.equals("Tj")){
                        COSString previous = (COSString)tokens.get(i-1);
                        //将此字符串的内容作为PDF文本字符串返回。
                        String string=previous.getString();
//                        //replaceFirst>>替换第一个字符
//                        string = string.replaceFirst( vo.getStrtofind(), vo.getMessage() );
                        System.out.println(string);
                        byte[] gbks = string.getBytes("GBK");
                        String s = new String(gbks);
                        previous.reset();
                        System.out.println(s);
                    }else if(name.equals("TJ")){
                        //COSArray是pdfbase对象数组,作为PDF文档的一部分
                        COSArray previous  =(COSArray)tokens.get(i-1);
                        //循环previous
                        for (int k = 0; k < previous.size(); k++) {
                            //这将从数组中获取一个对象,这将取消引用该对象
                            //如果对象为cosnull，则返回null
                            Object arrElement = previous.getObject( k );
                            if( arrElement instanceof COSString ){
                                //COSString对象>>创建java字符串的一个新的文本字符串。
                                COSString cosString =(COSString)arrElement;
                                //将此字符串的内容作为PDF文本字符串返回。
                                String string =cosString.getString();
                                cosString.reset();
                                System.out.print(new String(string.getBytes("GBK")));
                            }
                        }
                        System.out.println();
                    }
                }
            }
                 PDStream updatedStream = new PDStream(document);
                 OutputStream out = updatedStream.createOutputStream();
                 ContentStreamWriter tokenWriter = new ContentStreamWriter(out);
                 tokenWriter.writeTokens( tokens );
                 page.setContents( updatedStream );

//            for (PDDocument pdDocument : split) {
//
//                String articleEnd = pdfTextStripper.getArticleEnd();
//                String text = pdfTextStripper.getText(pdDocument);
////                System.out.println(text);
//            }
//            String text = getPageOneText(document);
//            if(text.contains("PDF 电子书网")){
////                document.removePage(0);
////                document.save(file);
//            }
            document.save(file);
            System.out.println("Total Page: " + endPage);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CryptographyException e) {
            e.printStackTrace();
        } catch (COSVisitorException e) {
            e.printStackTrace();
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private static String getPageOneText(PDDocument document) throws IOException {
        Splitter splitter = new Splitter();
        splitter.setEndPage(1);
        List<PDDocument> split = splitter.split(document);
        PDDocument pdDocument = split.get(0);

        PDFTextStripper pdfStripper = new PDFTextStripper();
        return pdfStripper.getText(pdDocument);
    }

}
