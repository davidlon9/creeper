package demo.pdf.model;

public class PDFDetail {
//    书名：硬核运营
//    作者：计育韬/田晶晶/梁逸帆/孟诗琦/赵国梁
//    出版社：人民邮电出版社
//    副标题：技术流新媒体养成
//    出版年：2018-12
//    页数：400
//    类别：市场营销
//    格式：pdf
//    ISBN：9787115497178
    private String detailUrl;
    private String bookName;
    private String author;
    private String publisher;
    private String subTitle;
    private String publishTime;
    private String pageNum;
    private String category;
    private String format;
    private String isbn;
    private String authorDetail;
    private String contentDetail;
    private String translater;
    private String downloadUrl;
    private String realDownUrl;

    public String getRealDownUrl() {
        return realDownUrl;
    }

    public void setRealDownUrl(String realDownUrl) {
        this.realDownUrl = realDownUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public String getTranslater() {
        return translater;
    }

    public void setTranslater(String translater) {
        this.translater = translater;
    }

    public String getAuthorDetail() {
        return authorDetail;
    }

    public void setAuthorDetail(String authorDetail) {
        this.authorDetail = authorDetail;
    }

    public String getContentDetail() {
        return contentDetail;
    }

    public void setContentDetail(String contentDetail) {
        this.contentDetail = contentDetail;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getPageNum() {
        return pageNum;
    }

    public void setPageNum(String pageNum) {
        this.pageNum = pageNum;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
}
