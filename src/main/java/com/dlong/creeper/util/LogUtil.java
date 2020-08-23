package com.dlong.creeper.util;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;

public class LogUtil {
    public boolean showStatus=true;
    public boolean showResponseHeader=true;
    public boolean showResponseBody=true;

    public final static LogUtil DEFAULT=new LogUtil();

    public final static Logger logger= Logger.getLogger(LogUtil.class);

    public LogUtil() {
    }

    public LogUtil(boolean showStatus, boolean showResponseHeader, boolean showResponseBody) {
        this.showStatus = showStatus;
        this.showResponseBody = showResponseBody;
        this.showResponseHeader = showResponseHeader;
    }

    public void debug(HttpResponse httpResponse) throws IOException {
        logger.debug("*************** httpResponse begin ***************");

        if (showStatus){
            logger.debug("-------- status --------");
            logger.debug(httpResponse.getStatusLine());
        }

        if (showResponseHeader){
            logger.debug("-------- header --------");
            Header[] allHeaders = httpResponse.getAllHeaders();
            for (Header header : allHeaders) {
                logger.debug(header.getName() + ":" + header.getValue());
            }
        }

        if (showResponseBody){
            logger.debug("-------- body --------");
            HttpEntity entity = httpResponse.getEntity();
            String s = EntityUtils.toString(entity);
            logger.debug(entity.toString());
            logger.debug(s);
        }

        logger.debug("*************** httpResponse end ***************");
    }

    public void info(HttpResponse httpResponse) throws IOException {
        logger.info("*************** httpResponse begin ***************");

        if (showStatus){
            logger.info("-------- status --------");
            logger.info(httpResponse.getStatusLine());
        }

        if (showResponseHeader){
            logger.info("-------- header --------");
            Header[] allHeaders = httpResponse.getAllHeaders();
            for (Header header : allHeaders) {
                logger.info(header.getName() + ":" + header.getValue());
            }
        }

        if (showResponseBody){
            logger.info("-------- body --------");
            HttpEntity entity = httpResponse.getEntity();
            String s = EntityUtils.toString(entity,"utf-8");
            logger.info(entity.toString());
            logger.info(s);
        }

        logger.info("*************** httpResponse end ***************");
    }

    public void setShowStatus(boolean showStatus) {
        this.showStatus = showStatus;
    }

    public void setShowResponseBody(boolean showResponseBody) {
        this.showResponseBody = showResponseBody;
    }

    public void setShowResponseHeader(boolean showResponseHeader) {
        this.showResponseHeader = showResponseHeader;
    }
}
