package demo.simple;

import com.davidlong.creeper.annotation.*;
import com.davidlong.creeper.annotation.http.Get;
import org.apache.http.client.fluent.Request;

@Host(value = "www.xgv5.com", scheme = "https")
public interface SimpleDemo {

    @Get("/category-30.html")
    String getMaxPageNum(@Parameter(name="age") Integer age,@Parameter(name="sex") String sex);

    @Get("/category-30${#index==1?'':'_'+#index}.html")
    Request handlePDFListBook();

    @Get(value = "${#detailUrl}", urlInheritable = false)
    Request handlePDFBookDetial();

    @Host(value = "webapi.400gb.com", scheme = "https")
    @Get(value = "${#getFileUrl}")
    @RequestHeader(name = "Origin", value = "https://sn9.us")
    Request getFileInfo();

    @Host(value = "webapi.400gb.com", scheme = "https")
    @Get(value = "/get_file_url.php")
    @Parameters({@Parameter(name = "uid"), @Parameter(name = "fid"), @Parameter(name = "file_chk"),})
    @RequestHeader(name = "Origin", value = "https://sn9.us")
    Request getPDFDownloadUrl();

    @Host(value = "webapi.400gb.com", scheme = "https")
    @Get(value = "${#downUrl}", urlInheritable = false)
    Request downloadSingle();
}
