package com.eccos.nadzorniservis.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eccos.nadzorniservis.models.ExceptionFile;
import com.eccos.nadzorniservis.services.ExceptionFileService;

@Service
public class ExceptionFileServiceImpl {
    
    @Autowired
    ExceptionFileService exceptionFileService;
    
    
    /**
     * Metoda provjerava postoji li datoteka već u bazi
     * @param file
     * @return
     */
    public boolean isExistFile(String file) {
        boolean postoji = false;
        List<ExceptionFile> listExceptionFile = new ArrayList<ExceptionFile>();
        listExceptionFile = exceptionFileService.findAll();
        for(ExceptionFile exceptionFile : listExceptionFile) {
            if(exceptionFile.getFile().equals(file)) {
                postoji = true;
            }            
        }
        if(postoji) {
            return true;
        }
        else {
            return false;
        }
    }
    
    
    /**
     * Metoda dohvaca sve datoteke iz baze
     * @return
     */
    public List<String> getAllFiles() {
        List<String> files = new ArrayList<>();
        List<ExceptionFile> listExceptions = exceptionFileService.findAll();
        
        for (int i = 0; i < listExceptions.size(); i++) {
            files.add(listExceptions.get(i).getFile());
        }
        
        return files;
    }
}
