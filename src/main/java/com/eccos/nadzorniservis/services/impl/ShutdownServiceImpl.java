package com.eccos.nadzorniservis.services.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.eccos.nadzorniservis.services.ShutdownService;


@Service
public class ShutdownServiceImpl implements ShutdownService {
    
    @Autowired
    private ApplicationContext context;
    
    private static final Logger logger = LogManager.getLogger(ShutdownServiceImpl.class);
    
    
    public void shutdownContext() {
        logger.warn("Admin je isključio app");
        int exitCode = SpringApplication.exit(context);
        System.exit(exitCode);
        
    }
    
}
