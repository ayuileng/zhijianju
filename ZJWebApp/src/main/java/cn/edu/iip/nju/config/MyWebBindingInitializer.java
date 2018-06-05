package cn.edu.iip.nju.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
@Configuration
public class MyWebBindingInitializer implements WebBindingInitializer {
    @Override
    public void initBinder(WebDataBinder webDataBinder, WebRequest webRequest) {
        webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor());
    }
    @Autowired
    public void setWebBindingInitializer(RequestMappingHandlerAdapter requestMappingHandlerAdapter){
        requestMappingHandlerAdapter.setWebBindingInitializer(new MyWebBindingInitializer());
    }
}
