package com.dianping.ssp.crawler.common.util;

import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.ssp.crawler.common.contants.CrawlerCommonConstants;
import us.codecraft.webmagic.Page;

import javax.script.*;
import java.util.Map;

public class JavaScriptExecutorUtils {
    private static final AvatarLogger LOGGER = AvatarLoggerFactory.getLogger(JavaScriptExecutorUtils.class);
    private static ScriptEngineManager engineManager = new ScriptEngineManager();
    private static ScriptEngine scriptEngine = engineManager.getEngineByName("javascript");


    /**
     * 通过js脚本将结果集做过滤
     * 
     * @param script
     * @param params
     * @param page
     * @return
     */
    public synchronized static Object eval(String script, Map<String, Object> params) {
        try {
            //ScriptContext context = new SimpleScriptContext();
            //context.setAttribute(CrawlerCommonConstants.JavaScriptConstant.PARAM_PAGE, page, ScriptContext.GLOBAL_SCOPE);
            scriptEngine.eval(script);
            if (scriptEngine instanceof Invocable) {
                Invocable invoke = (Invocable) scriptEngine; // 调用merge方法，并传入两个参数
                String methodName = (String) params.get(CrawlerCommonConstants.JavaScriptConstant.PARAM_METHODNAME);
                Object data = params.get(CrawlerCommonConstants.JavaScriptConstant.PARAM_DATA);
                Object result = invoke.invokeFunction(methodName, data);
                return result;
            }
        } catch (Exception e) {
            LOGGER.error("eval script error for script:" + script, e);
        }
        return null;
    }
}
