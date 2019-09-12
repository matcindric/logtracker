package com.eccos.nadzorniservis.view;

import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import com.eccos.nadzorniservis.services.impl.ShutdownServiceImpl;

@Scope("session")
@Named
public class ShutdownView {
    
    @Autowired
    ShutdownServiceImpl shutdownService;
    
    public void shutdownApplication() {
        shutdownService.shutdownContext();
    }

}
