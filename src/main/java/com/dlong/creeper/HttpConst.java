package com.dlong.creeper;

import com.dlong.creeper.annotation.control.*;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpConst {
    public static final String SCHEME_HTTP="http";
    public static final String SCHEME_HTTPS="https";

    public static final String URI_BEGIN_IDENTIFIER ="://";

    public static final String HEADER_SET_COOKIE="Set-Cookie";

    public static final List<Class> FAILED_STRATEGY_ANNOS= Arrays.asList(new Class[]{FailedForward.class,FailedBack.class, FailedTerminate.class, FailedRestart.class, FailedRetry.class, FailedJump.class});

    public static final Map<Class,FailedStrategy> FAILED_ANNO_MAPPING=new HashMap<>();

    public static final String CONTENT_TYPE_ATOM_XML = "application/atom+xml";
    public static final String CONTENT_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_OCTET_STREAM = "application/octet-stream";
    public static final String CONTENT_TYPE_SVG_XML = "application/svg+xml";
    public static final String CONTENT_TYPE_XHTML_XML = "application/xhtml+xml";
    public static final String CONTENT_TYPE_XML = "application/xml";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final String CONTENT_TYPE_TEXT_HTML = "text/html";
    public static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";
    public static final String CONTENT_TYPE_TEXT_XML = "text/xml";

    static {
        FAILED_ANNO_MAPPING.put(FailedForward.class, FailedStrategy.FORWARD);
        FAILED_ANNO_MAPPING.put(FailedBack.class, FailedStrategy.BACK);
        FAILED_ANNO_MAPPING.put(FailedRetry.class, FailedStrategy.RETRY);
        FAILED_ANNO_MAPPING.put(FailedRestart.class, FailedStrategy.RESTART);
        FAILED_ANNO_MAPPING.put(FailedTerminate.class, FailedStrategy.TERMINATE);
    }

    public static boolean isFailedStrategyAnno(Annotation annotation) {
        return HttpConst.FAILED_STRATEGY_ANNOS.contains(annotation.annotationType());
    }

}
