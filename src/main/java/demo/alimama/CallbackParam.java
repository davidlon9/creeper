package demo.alimama;

import com.dlong.creeper.model.Param;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CallbackParam {
    private String expando="jQuery" + ("3.2.1"+String.valueOf(Math.random())).replaceAll("\\.","");
    private long ajaxNonce=new Date().getTime();

    private String callback;

    public CallbackParam() {
        this.callback = expando + "_" + (ajaxNonce++);
    }

    public String getExtraParamsStr(){
        return this.callback+"&_=" + (ajaxNonce++);
    }

    public List<Param> getExtraParams(){
        List<Param> params = new ArrayList<>();
        params.add(new Param("callback",callback));
        params.add(new Param("_",ajaxNonce));
        return params;
    }

    public List<Object> getExtraParamValues(){
        List<Object> params = new ArrayList<>();
        params.add(callback);
        params.add((ajaxNonce++));
        return params;
    }

    public String getCallback() {
        return callback;
    }

    public String getAjaxNonce() {
        return String.valueOf(ajaxNonce);
    }

    public static void main(String[] args) {
        List<Param> extraParams = new CallbackParam().getExtraParams();
        extraParams.stream().forEach(System.out::println);
    }
}
