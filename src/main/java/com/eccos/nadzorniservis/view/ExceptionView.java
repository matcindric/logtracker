package com.eccos.nadzorniservis.view;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import com.eccos.nadzorniservis.models.Exception;
import com.eccos.nadzorniservis.services.ExceptionFileService;
import com.eccos.nadzorniservis.services.ExceptionService;
import com.eccos.nadzorniservis.validators.SearchValidator;

//@Component
@Scope("session")
@Named
public class ExceptionView {
    
    private List<Exception> exceptions;
    private Exception oznacenaIznimka;
    private int solvedSearch;
    
    private String exceptionName;
    private String stackTrace;
    private String file;
    private List<String> files;
    private Date timeFrom;
    private Date timeTo;
    
    @Autowired
    private ExceptionService exceptionService;
    
    @Autowired
    private ExceptionFileService exceptionFileService;
    
    SearchValidator validator = new SearchValidator();
    
    
    public ExceptionView() {
        
    }
    
    
    @PostConstruct
    public void init() {
       this.exceptions = exceptionService.findAll();
       this.files = exceptionFileService.getAllFiles();
    }

    
    public void saveState(int idException) {
        exceptionService.setSolved(idException);
    }
    
    
    public void search() {
        
        if (solvedSearch == 1) {
            exceptions = exceptionService.getSolved(timeFrom, timeTo, exceptionName, stackTrace, file);
        } 
        else if (solvedSearch == -1) {
            exceptions = exceptionService.getUnsolved(timeFrom, timeTo, exceptionName, stackTrace, file);
        } 
        else if (solvedSearch == 0) {            
            exceptions = exceptionService.getSolvedUnsolved(timeFrom, timeTo, exceptionName, stackTrace, file);
        }  
 
    }
    
    
    public void cleanInput() {
        timeFrom = null;
        timeTo = null;
        exceptionName = "";
        stackTrace = "";
        solvedSearch = 0;
        file = null;
        
        exceptions = exceptionService.findAll();
        //files = exceptionFileService.getAllFiles();
    }

    
    public List<Exception> getExceptions() {
        return exceptions;
    }
  
    public void setExceptions(List<Exception> exceptions) {
        this.exceptions = exceptions;
    }

    
    public int getSolvedSearch() {
        return solvedSearch;
    }

    
    public void setSolvedSearch(int solvedSearch) {
        this.solvedSearch = solvedSearch;
    }

    
    public String getExceptionName() {
        return exceptionName;
    }

    
    public void setExceptionName(String exceptionName) {
        this.exceptionName = exceptionName;
    }
    
    
    public String getFile() {
        return file;
    }

    
    public void setFile(String file) {
        this.file = file;
    }
    
    
    public List<String> getFiles() {
        files = exceptionFileService.getAllFiles();
        return files;
    }

    
    public void setFiles(List<String> files) {
        this.files = files;
    }
    

    public Date getTimeFrom() {
        return timeFrom;
    }

    
    public void setTimeFrom(Date timeFrom) {
        this.timeFrom = timeFrom;
    }

    
    public Date getTimeTo() {
        return timeTo;
    }

    
    public void setTimeTo(Date timeTo) {
        this.timeTo = timeTo;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    
    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    
    public Exception getOznacenaIznimka() {
        return oznacenaIznimka;
    }

    
    public void setOznacenaIznimka(Exception oznacenaIznimka) {
        this.oznacenaIznimka = oznacenaIznimka;
    }
 
}
