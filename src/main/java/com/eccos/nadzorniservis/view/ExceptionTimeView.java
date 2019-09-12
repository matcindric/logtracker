package com.eccos.nadzorniservis.view;

import java.util.List;

import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import com.eccos.nadzorniservis.models.Exception;
import com.eccos.nadzorniservis.models.ExceptionTime;
import com.eccos.nadzorniservis.services.ExceptionTimeService;

@Scope("session")
@Named
public class ExceptionTimeView {
    
    private List<ExceptionTime> exceptionTime;
    private Exception oznacenaIznimka;
    
    @Autowired
    private ExceptionTimeService exceptionTimeService;
    
    public ExceptionTimeView() {
        
    }
    
    public void prikaziVrijemeIznimke(int idException) {
        exceptionTime = exceptionTimeService.getTime(idException);
        oznacenaIznimka.setListExceptionTime(exceptionTime);
    }

    
    public List<ExceptionTime> getExceptionTime() {
        return exceptionTime;
    }

    
    public void setExceptionTime(List<ExceptionTime> exceptionTime) {
        this.exceptionTime = exceptionTime;
    }

    
    public Exception getOznacenaIznimka() {
        return oznacenaIznimka;
    }

    
    public void setOznacenaIznimka(Exception oznacenaIznimka) {
        this.oznacenaIznimka = oznacenaIznimka;
    }
    
}
