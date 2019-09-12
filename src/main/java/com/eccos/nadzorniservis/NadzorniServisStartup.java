package com.eccos.nadzorniservis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.eccos.nadzorniservis.configuration.AppConfig;


@Component
public class NadzorniServisStartup{
    
    @Autowired
    private AppConfig appConfig;
    
    @Autowired 
    ApplicationContext ctx;
    

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        for (int i = 0; i < appConfig.getHtmlFileName().size(); i++) {
            WatchThread watchThread = ctx.getBean(WatchThread.class);
            watchThread.setDirectory(appConfig.getFolder().get(i));
            watchThread.setFileName(appConfig.getHtmlFileName().get(i));
            watchThread.setFile(appConfig.getHtmlFile().get(i));
            watchThread.setLogLevel(appConfig.getLogLevel().get(i));
            watchThread.start();
        }  
    }   
    
}