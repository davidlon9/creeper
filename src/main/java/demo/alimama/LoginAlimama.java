package demo.alimama;

import com.dlong.creeper.annotation.Host;
import com.dlong.creeper.annotation.Parameter;
import com.dlong.creeper.annotation.Parameters;
import com.dlong.creeper.annotation.RequestHeader;
import com.dlong.creeper.annotation.http.Get;
import com.dlong.creeper.annotation.http.Post;
import com.dlong.creeper.annotation.seq.RequestChain;
import com.dlong.creeper.annotation.seq.SeqRequest;
import com.dlong.creeper.control.MoveAction;
import com.dlong.creeper.control.MoveActions;
import com.dlong.creeper.execution.ChainContextExecutor;
import com.dlong.creeper.execution.context.FormParamStore;
import com.dlong.creeper.model.Param;
import com.dlong.creeper.model.result.ChainResult;
import com.dlong.creeper.model.seq.RequestChainEntity;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;

import java.util.List;

@RequestChain
@Host("login.taobao.com")
public class LoginAlimama {
    @SeqRequest(index = 1,description = "获取登陆必要Cookie")
    @Get("/member/login.jhtml?redirectURL=https%3A%2F%2Fwww.alimama.com%2Findex.htm&style=mini&full_redirect=true&newMini2=true&enup=0&qrlogin=1&keyLogin=true&sub=true&css_style=dingxiang&from=alimama&disableQuickLogin=true")
    public MoveAction getCookies(String result, BasicCookieStore cookieStore){
        System.out.println(result);
        return MoveActions.FORWARD();
    }

    @SeqRequest(index = 2,description = "获取登陆必要Cookie")
    @Post("/newlogin/account/check.do?appName=taobao&fromSite=0")
    @Parameters({
            @Parameter(name="loginId",value="fuckutaobao111222333"),
            @Parameter(name="password2",value="5c33eab3664f616cd57c570720f18116d42255529f56cf3f6fd8e5d3c18dd06a92044e76401482e0342823cfac0984c93e132afc3d437d6511ed8732cdb7dd6426b6a12927c92fa534053d72ed6e9ad5311806a29ff41ce9b9676777c3ff1f09b5a5f76c43fd5a20f70c180ce83b4384c9d61c7bf52c845fd24e4c01ef0bc0dc"),
            @Parameter(name="keepLogin",value="true"),
            @Parameter(name="ua",value="134#o074CXXwXGErbEc2JgPUdX0D3QROwKO9sE/w4/PLr3cepzP1c/gLr8han0vXUV21ml/MH7P2C/zUUvlf6fEXZUabrdOGkcczGFy894cQdoiNGJowqqX3IkuU+4qAqVtu12nxzsefqc77rg2QvfD8AdPdXXeyXTl1ap6++QJIAHnHrgowqcp7K4gB+X2KqcqqZIUPoUn5uXGTGZU7/l/1Jxi0DR+tdbgtpaWYNzBrtwlQH+/SnAZgX3Uxb7Lw9/QANUQFBRGGuI5tfKUoVe7V4ARAxeaW0jW62K49sEWFG8lTuuMEjructoH8bThlsDIbkPtPOi02omwoY+HA//1Zh7IYeMkKjNNYLW1ErjTbjXenJGcE4B2C4HNANKxzba7/xfL0hM99pzrqXmcddCDkycQbjcJrXsCPPl1Lcba42hcKlVW5WXTTelF/8Mz9so910IrUCDSmOMMLgGQ7qNt3V0MRzZ9pwG3xlj9Bxzt4mum1H/9Oob+nLG+Ou9X2N2aZIQ+o4+1yffSq4eiToPlp4YcJ1pRqbyPOgonQZLiw4lrJLX47HrBoLgB4CyZLd/B7xbY+JOogP//L/D6g2xrKpNlkrOo+yMwaqjnTvyUtdcoM6FiGd6wsmZOhegApUhwMs9dc/Fu7BhppLAEICROdWufs1dnF/lVMCANDeHDIqWD6kP4Q9644uqPkMPdh9kG748MtCViXCVJl2YLy1HTbDHc8kcnNjHF9NMh+PdxLnzzUnFj81lOveG6bN+UMt2T/Bj5YIz1aq64E6VncM7Syt2aN2v1j3ulPc3yVBDPXXZQLayTh4iZB8jywS2uUM2Hvolj9lhPvZ+U7DGBAJAGAdBPUE+PFBn8w1P1rr5VpYKGTebaIVO0In7Z414NgnbYOd3yg3ckTp+kgbIlwxYAOyJjv0XMdTAwPLI+Y7yktFVEUGxOy+38PNhQktRBuLd5IoyuNIslTNgaC23UahQ5Jb1wD8Ya6hEeI1AYGcyXzuNZc/UtnSYEsulyylG4B1h7+R7d+Inc2VVST/YEIjnyIFcSJroXYc6JIe9V7X4mmu9/xYOBHpaUeAlAoPR+KkMhMRftcpIjC91JXi86eNKJ9yNyRG1ZxXEVSHQAyNLzK8YddYlKasqE+Rz1wvqSXW1C70tWhh445N3w11VcPRFNrH/00ZQTulWjaqh0P+DewuvYEBOJIjJiX5A5Isf//SuBt5veMIHNnsZZLvmgc4KKzsohtmjG="),
            @Parameter(name="umidGetStatusVal",value="255"),
            @Parameter(name="screenPixel",value="1536x864"),
            @Parameter(name="navlanguage",value="zh-CN"),
            @Parameter(name="navUserAgent",value="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.83 Safari/537.36"),
            @Parameter(name="navPlatform",value="Win32"),
            @Parameter(name="sub",value="true"),
            @Parameter(name="appName",value="taobao"),
            @Parameter(name="css_style",value="dingxiang"),
            @Parameter(name="appEntrance",value="taobao_pc"),
            @Parameter(name="_csrf_token",value="xrh76mHUr2K0hiVMHfCJ07"),
            @Parameter(name="umidToken",value="79c8f2366c4d4934f03ba8b2294d77924a1e3dda"),
            @Parameter(name="hsiz",value="14e42463804ea9b1c6224a1bd55cfac1"),
            @Parameter(name="newMini2",value="true"),
            @Parameter(name="bizParams",value=""),
            @Parameter(name="full_redirect",value="true"),
            @Parameter(name="style",value="mini"),
            @Parameter(name="appkey",value="00000000"),
            @Parameter(name="from",value="alimama"),
            @Parameter(name="isMobile",value="false"),
            @Parameter(name="lang",value="zh_CN"),
            @Parameter(name="returnUrl",value="https://www.alimama.com/index.htm"),
            @Parameter(name="fromSite",value="0"),
    })
    public MoveAction check(String result, BasicCookieStore cookieStore){
        System.out.println(result);
        return MoveActions.FORWARD();
    }

    @SeqRequest(index = 3,description = "登陆Taobao")
    @RequestHeader(name= "User-Agent",value="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36")
    @Post("/newlogin/login.do?appName=taobao&fromSite=0")
    @Parameters({
            @Parameter(name="loginId",value="fuckutaobao111222333"),
            @Parameter(name="password2",value="5c33eab3664f616cd57c570720f18116d42255529f56cf3f6fd8e5d3c18dd06a92044e76401482e0342823cfac0984c93e132afc3d437d6511ed8732cdb7dd6426b6a12927c92fa534053d72ed6e9ad5311806a29ff41ce9b9676777c3ff1f09b5a5f76c43fd5a20f70c180ce83b4384c9d61c7bf52c845fd24e4c01ef0bc0dc"),
            @Parameter(name="keepLogin",value="true"),
            @Parameter(name="ua",value="134#o074CXXwXGErbEc2JgPUdX0D3QROwKO9sE/w4/PLr3cepzP1c/gLr8han0vXUV21ml/MH7P2C/zUUvlf6fEXZUabrdOGkcczGFy894cQdoiNGJowqqX3IkuU+4qAqVtu12nxzsefqc77rg2QvfD8AdPdXXeyXTl1ap6++QJIAHnHrgowqcp7K4gB+X2KqcqqZIUPoUn5uXGTGZU7/l/1Jxi0DR+tdbgtpaWYNzBrtwlQH+/SnAZgX3Uxb7Lw9/QANUQFBRGGuI5tfKUoVe7V4ARAxeaW0jW62K49sEWFG8lTuuMEjructoH8bThlsDIbkPtPOi02omwoY+HA//1Zh7IYeMkKjNNYLW1ErjTbjXenJGcE4B2C4HNANKxzba7/xfL0hM99pzrqXmcddCDkycQbjcJrXsCPPl1Lcba42hcKlVW5WXTTelF/8Mz9so910IrUCDSmOMMLgGQ7qNt3V0MRzZ9pwG3xlj9Bxzt4mum1H/9Oob+nLG+Ou9X2N2aZIQ+o4+1yffSq4eiToPlp4YcJ1pRqbyPOgonQZLiw4lrJLX47HrBoLgB4CyZLd/B7xbY+JOogP//L/D6g2xrKpNlkrOo+yMwaqjnTvyUtdcoM6FiGd6wsmZOhegApUhwMs9dc/Fu7BhppLAEICROdWufs1dnF/lVMCANDeHDIqWD6kP4Q9644uqPkMPdh9kG748MtCViXCVJl2YLy1HTbDHc8kcnNjHF9NMh+PdxLnzzUnFj81lOveG6bN+UMt2T/Bj5YIz1aq64E6VncM7Syt2aN2v1j3ulPc3yVBDPXXZQLayTh4iZB8jywS2uUM2Hvolj9lhPvZ+U7DGBAJAGAdBPUE+PFBn8w1P1rr5VpYKGTebaIVO0In7Z414NgnbYOd3yg3ckTp+kgbIlwxYAOyJjv0XMdTAwPLI+Y7yktFVEUGxOy+38PNhQktRBuLd5IoyuNIslTNgaC23UahQ5Jb1wD8Ya6hEeI1AYGcyXzuNZc/UtnSYEsulyylG4B1h7+R7d+Inc2VVST/YEIjnyIFcSJroXYc6JIe9V7X4mmu9/xYOBHpaUeAlAoPR+KkMhMRftcpIjC91JXi86eNKJ9yNyRG1ZxXEVSHQAyNLzK8YddYlKasqE+Rz1wvqSXW1C70tWhh445N3w11VcPRFNrH/00ZQTulWjaqh0P+DewuvYEBOJIjJiX5A5Isf//SuBt5veMIHNnsZZLvmgc4KKzsohtmjG="),
            @Parameter(name="umidGetStatusVal",value="255"),
            @Parameter(name="screenPixel",value="1536x864"),
            @Parameter(name="navlanguage",value="zh-CN"),
            @Parameter(name="navUserAgent",value="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.83 Safari/537.36"),
            @Parameter(name="navPlatform",value="Win32"),
            @Parameter(name="sub",value="true"),
            @Parameter(name="appName",value="taobao"),
            @Parameter(name="css_style",value="dingxiang"),
            @Parameter(name="appEntrance",value="taobao_pc"),
            @Parameter(name="_csrf_token",value="xrh76mHUr2K0hiVMHfCJ07"),
            @Parameter(name="umidToken",value="79c8f2366c4d4934f03ba8b2294d77924a1e3dda"),
            @Parameter(name="hsiz",value="14e42463804ea9b1c6224a1bd55cfac1"),
            @Parameter(name="newMini2",value="true"),
            @Parameter(name="bizParams",value=""),
            @Parameter(name="full_redirect",value="true"),
            @Parameter(name="style",value="mini"),
            @Parameter(name="appkey",value="00000000"),
            @Parameter(name="from",value="alimama"),
            @Parameter(name="isMobile",value="false"),
            @Parameter(name="lang",value="zh_CN"),
            @Parameter(name="returnUrl",value="https://www.alimama.com/index.htm"),
            @Parameter(name="fromSite",value="0"),
    })
    public MoveAction login(String result, FormParamStore formParamStore,BasicCookieStore cookieStore){
        System.out.println(result);
        return MoveActions.FORWARD();
    }

    @SeqRequest(index = 4,description = "主页")
    @Host(value = "passport.alibaba.com",scheme = "https")
    @Get("https://passport.alibaba.com/mini_apply_st.js?callback=callback&site=0")
    @Parameters({ @Parameter(name="token")})
    public MoveAction passport(String result, BasicCookieStore cookieStore){
        System.out.println(result);
        return MoveActions.FORWARD();
    }



    @SeqRequest(index = 5,description = "主页")
    @Host(value = "pub.alimama.com",scheme = "https")
    @Get("/manage/overview/index.htm?spm=a219t.11816995.0.d60a03e63.55ac6a15x1ZMLD")
    public MoveAction mainPage(String result, BasicCookieStore cookieStore){
        System.out.println(result);
        return MoveActions.FORWARD();
    }


    public static void main(String[] args) {
        ChainContextExecutor chainContextExecutor = new ChainContextExecutor(LoginAlimama.class);
        RequestChainEntity rootChain = chainContextExecutor.getChainContext().getRootChain();
        chainContextExecutor.exeucteRootChain();
    }
}
