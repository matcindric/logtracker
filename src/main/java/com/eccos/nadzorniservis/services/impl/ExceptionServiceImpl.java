package com.eccos.nadzorniservis.services.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eccos.nadzorniservis.configuration.AppConfig;
import com.eccos.nadzorniservis.models.Exception;
import com.eccos.nadzorniservis.models.ExceptionTime;
import com.eccos.nadzorniservis.services.ExceptionService;
import com.eccos.nadzorniservis.services.ExceptionTimeService;
import com.eccos.nadzorniservis.validators.SearchValidator;

@Component
public class ExceptionServiceImpl {
    
    @Autowired
    private ExceptionService exceptionService;
    
    @Autowired
    private ExceptionTimeService exceptionTimeService;
    
    @Autowired
    private AppConfig appConfig;
    
    
    SearchValidator validator = new SearchValidator();
    
    
    public void setSolved(int id) {
        Exception iznimka = exceptionService.findById(id).orElse(null); 
        if (iznimka.isSolved()) {
            iznimka.setSolved(false);
        }
        else {
            iznimka.setSolved(true);
        }
        exceptionService.save(iznimka);
    }
    
    
    /**
     * Metoda dohvaca sve iznimke koje su riješene i zadovoljavaju upisane
     * kriterija pretraživanja
     * @param name
     * @param stack
     * @param file
     * @return
     */
    public List<Exception> getSolved(Date timeFrom, Date timeTo, String name, String stack, String file) {
        List<Exception> exceptions = new ArrayList<>();
        List<Exception> listExceptions = exceptionService.findAll();
        
        if (timeFrom == null && timeTo == null) {
            listExceptions = exceptionService.findAll();
            getSolvedExceptions(listExceptions, exceptions, name, stack, file);
        }
        else if (timeFrom == null) {
            listExceptions = getToDate(timeTo);
            getSolvedExceptions(listExceptions, exceptions, name, stack, file);
        }
        else if (timeTo == null) {
            listExceptions = getFromDate(timeFrom);
            getSolvedExceptions(listExceptions, exceptions, name, stack, file);
        }
        else {
            validator.checkDate(timeFrom, timeTo);
            listExceptions = getWithinInterval(timeFrom, timeTo);
            getSolvedExceptions(listExceptions, exceptions, name, stack, file);
        }
        return exceptions;
    }
    
    
    /**
     * Metoda dohvaca sve iznimke koje nisu riješene i zadovoljavaju upisane
     * kriterije pretraživanja
     * @param name
     * @param stack
     * @param file
     * @return
     */
    public List<Exception> getUnsolved(Date timeFrom, Date timeTo, String name, String stack, String file) {
        List<Exception> exceptions = new ArrayList<>();
        List<Exception> listExceptions = new ArrayList<>();
        
        if (timeFrom == null && timeTo == null) {
            listExceptions = exceptionService.findAll();
            getUnsolvedExceptions(listExceptions, exceptions, name, stack, file);
        }
        else if (timeFrom == null) {
            listExceptions = getToDate(timeTo);
            getUnsolvedExceptions(listExceptions, exceptions, name, stack, file);
        }
        else if (timeTo == null) {
            listExceptions = getFromDate(timeFrom);
            getUnsolvedExceptions(listExceptions, exceptions, name, stack, file);
        }
        else {
            validator.checkDate(timeFrom, timeTo);
            listExceptions = getWithinInterval(timeFrom, timeTo);
            getUnsolvedExceptions(listExceptions, exceptions, name, stack, file);
        }
        return exceptions;
    }
    
    
    /**
     * Metoda dohvaca sve iznimke koje ne ukljucuju kriterij rijeseno
     * @param name
     * @param stack
     * @param file
     * @return
     */
    public List<Exception> getSolvedUnsolved(Date timeFrom, Date timeTo, String name, String stack, String file) {
        List<Exception> exceptions = new ArrayList<>();
        List<Exception> listExceptions = new ArrayList<>();
        
        if (timeFrom == null && timeTo == null) {
            listExceptions = exceptionService.findAll();
            getExceptions(listExceptions, exceptions, name, stack, file);
        }
        else if (timeFrom == null) {
            listExceptions = getToDate(timeTo);
            getExceptions(listExceptions, exceptions, name, stack, file);
        }
        else if (timeTo == null) {
            listExceptions = getFromDate(timeFrom);
            getExceptions(listExceptions, exceptions, name, stack, file);
        }
        else {
            validator.checkDate(timeFrom, timeTo);
            listExceptions = getWithinInterval(timeFrom, timeTo);
            getExceptions(listExceptions, exceptions, name, stack, file);
        }
        return exceptions;
    }
    
    
    /**
     * Metoda dohvaca sve iznimke koje odgovaraju kriteriju naziv, stack, datoteka i koje su rijesene
     * @param listExceptions
     * @param exceptions
     * @param name
     * @param stack
     * @param file
     */
    private void getSolvedExceptions(List<Exception> listExceptions, List<Exception> exceptions, String name, String stack, String file) {
        for (int i = 0; i < listExceptions.size(); i++) {
            if (listExceptions.get(i).isSolved() && 
                    listExceptions.get(i).getExceptionName().contains(name) && 
                    listExceptions.get(i).getStackTrace().contains(stack) &&
                    listExceptions.get(i).getExceptionFile().getFile().contains(file)) {
                exceptions.add(listExceptions.get(i));
            }
        }
    }
    
    
    /**
     * Metoda dohvaca sve iznimke koje odgovaraju kriteriju naziv, stack, datoteka i koje nisu rijesene
     * @param listExceptions
     * @param exceptions
     * @param name
     * @param stack
     * @param file
     */
    private void getUnsolvedExceptions(List<Exception> listExceptions, List<Exception> exceptions, String name, String stack, String file) {
        for (int i = 0; i < listExceptions.size(); i++) {
            if (!listExceptions.get(i).isSolved() && 
                    listExceptions.get(i).getExceptionName().contains(name) && 
                    listExceptions.get(i).getStackTrace().contains(stack) &&
                    listExceptions.get(i).getExceptionFile().getFile().contains(file)) {
                exceptions.add(listExceptions.get(i));
            }
        }
    }
    
    
    /**
     * Metoda dohvaca iznimke prema nazivu, stacku i datoteci iz liste iznimki koje zadovoljavaju
     * kriterij datum i sprema u konacnu listu
     * @param listExceptions
     * @param exceptions
     * @param name
     * @param stack
     * @param file
     */
    private void getExceptions(List<Exception> listExceptions, List<Exception> exceptions, String name, String stack, String file) {
        for (int i = 0; i < listExceptions.size(); i++) {
            if (listExceptions.get(i).getExceptionName().contains(name) && 
                    listExceptions.get(i).getStackTrace().contains(stack) &&
                    listExceptions.get(i).getExceptionFile().getFile().contains(file)) {
                exceptions.add(listExceptions.get(i));
            }
        }
    }
    
    
    /**
     * Metoda dohvaca iznimke koja se nalaze unutar nekog intervala
     *  -potrebno je provjeriti je li se neka iznimka dogodila vise puta, ako je, iznimka se samo jednom doda u listu
     * @param timeFrom
     * @param timeTo
     * @return
     */
    private List<Exception> getWithinInterval(Date timeFrom, Date timeTo) {
        List<Exception> exceptions = new ArrayList<>();
        List<ExceptionTime> exceptionTime = new ArrayList<>();
        List<ExceptionTime> listExceptionsTime = exceptionTimeService.findAll();
        
        //pronadi vremena iz odabranog intervala
        for (int i = 0; i < listExceptionsTime.size(); i++) {
            if (setDateFormat(listExceptionsTime.get(i).getTime()).after(timeFrom) &&
                    setDateFormat(listExceptionsTime.get(i).getTime()).before(timeTo)) {
                exceptionTime.add(listExceptionsTime.get(i));
            }
        }
        //Iz odabranog intervala izvuci iznimke
        for (int j = 0; j < exceptionTime.size(); j++) {
            //exceptions.add(exceptionsTime.get(j).getException());
            
            boolean isto = false;
            if(exceptions.isEmpty()) {
                exceptions.add(exceptionTime.get(j).getException());
            }
            else {
                for (int l = 0; l < exceptions.size(); l++) {
                    if(exceptionTime.get(j).getException().getIdException() == exceptions.get(l).getIdException()) {
                        //exceptions.add(exceptionTime.get(j).getException());
                        isto = true;
                    }      
                }
                if (!isto) {
                    exceptions.add(exceptionTime.get(j).getException());
                }
            } 
        }
        return exceptions;
    }
    
    
    /**
     * Metoda dohvaca iznimke koje su se dogodile od određenog datuma
     *  -potrebno je provjeriti je li se neka iznimka dogodila vise puta, ako je, iznimka se samo jednom doda u listu
     * @param timeFrom
     * @return
     */
    private List<Exception> getFromDate(Date timeFrom) {
        List<Exception> exceptions = new ArrayList<>();
        List<ExceptionTime> exceptionTime = new ArrayList<>();
        List<ExceptionTime> listExceptionTime = exceptionTimeService.findAll();
        
        //pronadi vremena iz odabranog intervala
        for (int i = 0; i < listExceptionTime.size(); i++) {
            if (setDateFormat(listExceptionTime.get(i).getTime()).after(timeFrom)) {
                exceptionTime.add(listExceptionTime.get(i));
            }
        }
        //Iz odabranog intervala izvuci iznimke
        for (int j = 0; j < exceptionTime.size(); j++) {
            //exceptions.add(exceptionTime.get(j).getException());
            
            boolean isto = false;
            if(exceptions.isEmpty()) {
                exceptions.add(exceptionTime.get(j).getException());
            }
            else {
                for (int l = 0; l < exceptions.size(); l++) {
                    if(exceptionTime.get(j).getException().getIdException() == exceptions.get(l).getIdException()) {
                        isto = true;
                    }      
                }
                if (!isto) {
                    exceptions.add(exceptionTime.get(j).getException());
                }
            }
        }
        return exceptions;
    }
    
    
    /**
     * Metoda dohvaca iznimke koje su se dogodile do određenog datuma
     *  -potrebno je provjeriti je li se neka iznimka dogodila vise puta, ako je, iznimka se samo jednom doda u listu
     *  (da se kod ispisa ne ponavljaju iste iznimke -> svaka iznimka sadrzi sve datume, dovoljno ju je jednom ispisati)
     * @param timeTo
     * @return
     */
    private List<Exception> getToDate(Date timeTo) {
        List<Exception> exceptions = new ArrayList<>();
        List<ExceptionTime> exceptionTime = new ArrayList<>();
        List<ExceptionTime> listExceptionTime = exceptionTimeService.findAll();
        
        //pronadi vremena iz odabranog intervala
        for (int i = 0; i < listExceptionTime.size(); i++) {
            if (setDateFormat(listExceptionTime.get(i).getTime()).before(timeTo)) {
                exceptionTime.add(listExceptionTime.get(i));
            }
        }
        
        //Iz odabranog intervala izvuci iznimke
        for (int j = 0; j < exceptionTime.size(); j++) {
            //exceptions.add(exceptionTime.get(j).getException());
            
            boolean isto = false;
            if(exceptions.isEmpty()) {
                exceptions.add(exceptionTime.get(j).getException());
            }
            else {
                for (int l = 0; l < exceptions.size(); l++) {
                    if(exceptionTime.get(j).getException().getIdException() == exceptions.get(l).getIdException()) {
                        isto = true;
                    }      
                }
                if (!isto) {
                    exceptions.add(exceptionTime.get(j).getException());
                }
            }
        }
        
        return exceptions;
    }
    
    
    /**
     * Metoda parsira datum tipa string u datum tipa date
     * @param datum
     * @return
     */
    private Date setDateFormat(String datum) {   
        Date date = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat(appConfig.getDateFormat(), Locale.ENGLISH);
            date = dateFormat.parse(datum);
            return date;         
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
