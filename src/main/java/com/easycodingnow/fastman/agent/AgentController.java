package com.easycodingnow.fastman.agent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;

/**
 * @author lihao
 * @since 2020-01-16
 */
@RestController
public class AgentController implements ApplicationContextAware {

    private ApplicationContext applicationContext;


    @PostMapping("/fastman-agent")
    public Object agent(@RequestBody Request request) {
        try {
            Class<?> cls = Class.forName(request.getClassName());

            Object bean = applicationContext.getBean(cls);

            JSONArray paramsArr = JSON.parseArray(request.getParams());

            Object[] paramObjs = new Object[request.getParamTypes() != null && request.getParamTypes().size() > 0?request.getParamTypes().size():0];

            Method method;

            if (request.getParamTypes() != null && request.getParamTypes().size() > 0) {

                Class[] clsArr = new Class[request.getParamTypes().size()];

                for (int i = 0; i < request.getParamTypes().size(); i++) {
                    String clsStr = request.getParamTypes().get(i);
                    if ("long".equals(clsStr)) {
                        clsArr[i] = long.class;
                    } else if ("int".equals(clsStr)) {
                        clsArr[i] = int.class;
                    } else if ("short".equals(clsStr)) {
                        clsArr[i] = short.class;
                    } else if ("double".equals(clsStr)) {
                        clsArr[i] = double.class;
                    } else if ("float".equals(clsStr)) {
                        clsArr[i] = float.class;
                    } else if ("char".equals(clsStr)) {
                        clsArr[i] = char.class;
                    } else if ("byte".equals(clsStr)) {
                        clsArr[i] = byte.class;
                    } else  {
                        clsArr[i] = Class.forName(clsStr);
                    }

                    if (paramsArr.get(i) != null) {
                        paramObjs[i]= JSON.parseObject(paramsArr.get(i).toString(), clsArr[i]);
                    }
                }

                method = cls.getMethod(request.getMethodName(), clsArr);

            } else {

                method = cls.getMethod(request.getMethodName());

            }

            method.setAccessible(true);

            return method.invoke(bean, paramObjs);
        } catch (Exception e){
            e.printStackTrace();
            return "error message：" + e.getCause().toString() ;
        }

    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
