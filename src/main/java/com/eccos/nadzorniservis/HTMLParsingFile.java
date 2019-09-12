package com.eccos.nadzorniservis;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.eccos.nadzorniservis.configuration.AppConfig;
import com.eccos.nadzorniservis.models.Exception;
import com.eccos.nadzorniservis.models.ExceptionTime;

@Component
@Scope("prototype")
public class HTMLParsingFile {
    
    @Autowired
    AppConfig appConfig;
    
    
    String exceptionName = "";
    String breakLine = "";
    String stackTrace = "";  
    String exceptionTime;
    
    Date lastExceptionTime;
    String lastStackTrace;
    
    public List<ExceptionTime> listExceptionTime;
    
    private static final Logger logger = LogManager.getLogger(HTMLParsingFile.class);
    private String logLevel;
    
    public void parsingHTMLFile(String file) {
        try {
            File htmlFile = new File(file);
            Document doc = Jsoup.parse(htmlFile, "UTF-8");        
            
            listExceptionTime = new ArrayList<>();          
            
            Elements table = doc.getElementsByTag("tr");            
            for (Element row : table) {
                Elements elements = row.getElementsByTag("td");
                for (Element element : elements) {
                    switch(logLevel) {                        
                        case "INFO":
                            if (element.getElementsByClass("Level").text().equals("INFO") || 
                                    element.getElementsByClass("Level").text().equals("WARN") || 
                                    element.getElementsByClass("Level").text().equals("ERROR")) {                        
                                if (lastExceptionTime == null) {
                                    parsingException(row);
                                } 
                                else if (findNewExceptions(row.getElementsByClass("Date").text(), "[" + row.getElementsByClass("Level").text() + "]" + " " + row.getElementsByClass("Message").text())) { // dohvati tu liniju i od tuda zapocni s parsiranjem
                                    parsingException(row);
                                }  
                            }
                            break;
                        case "WARN":
                            if (element.getElementsByClass("Level").text().equals("WARN") || 
                                    element.getElementsByClass("Level").text().equals("ERROR")) {                        
                                if (lastExceptionTime == null) {
                                    parsingException(row);
                                }
                                else if (findNewExceptions(row.getElementsByClass("Date").text(), "[" + row.getElementsByClass("Level").text() + "]" + " " + row.getElementsByClass("Message").text())) { // dohvati tu liniju i od tuda zapocni s parsiranjem                              
                                    parsingException(row);
                                }   
                            }
                            break;
                        case "ERROR":
                            if (element.getElementsByClass("Level").text().equals("ERROR")) {                        
                                if (lastExceptionTime == null) {
                                    parsingException(row);
                                }
                                else if (findNewExceptions(row.getElementsByClass("Date").text(), "[" + row.getElementsByClass("Level").text() + "]" + " " + row.getElementsByClass("Message").text())) { // dohvati tu liniju i od tuda zapocni s parsiranjem                              
                                    parsingException(row);
                                }   
                            }
                            break;
                    }
                }
            }                      
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.toString());
        }        
    }
    
    
    private void parsingException(Element row) {
        //System.out.println(row);
        exceptionTime = row.getElementsByClass("Date").text();
        exceptionName = row.getElementsByClass("Logger").text();
        stackTrace = "[" + row.getElementsByClass("Level").text() + "]" + " " + row.getElementsByClass("Message").text();
        breakLine = getBreakLine(stackTrace);
        
        //System.out.println("Vrijeme: " + exceptionTime + "\nNaziv: " + exceptionName
          //      + "\nLinija događaja: " + breakLine + "\nStack trace: " + stackTrace + "\n");
        
        addNewException();
        
        int i = listExceptionTime.size()-1;
        String vrijemeZadnjeIznimke = listExceptionTime.get(i).getTime();
        lastExceptionTime = setDateFormat(vrijemeZadnjeIznimke);
        
        lastStackTrace = listExceptionTime.get(i).getException().getStackTrace();
    }
    
    
    private void addNewException() {
        Exception exception = new Exception();
        exception.setExceptionName(exceptionName);
        exception.setBreakLine(breakLine);
        exception.setStackTrace(stackTrace);

        ExceptionTime vi = new ExceptionTime();
        vi.setException(exception);
        vi.setTime(exceptionTime);
        listExceptionTime.add(vi);
    }
    
    
    /**
     * Metoda provjerava je li novi datum nakon veci od zadnjeg kad se dogodila neka promjena
     *      ako je datum veci, radi se o novoj iznimci
     *      ako je datum jednak, provjerava se je li stack trace jednak kao i kod zadnje iznimke
     *          ako je jednak, radi se o istoj iznimci, metoda vraca false
     *          ako je razlicit, radi se o drugoj iznimci koja se dogodila u isto vrijeme kad i prethodna, metoda vraca true
     * @param exceptionTime
     * @param stackTrace
     * @return
     */
    private boolean findNewExceptions(String exceptionTime, String stackTrace) {          
        // provjeri je li veci od zadnjeg spremljenog datuma
        Date newTime = setDateFormat(exceptionTime);
        /*if (newTime.after(dateLastException)) {
            return true;
        }*/
        if (newTime.after(lastExceptionTime)) {
            return true;
        }
        else if (newTime.equals(lastExceptionTime)) { //isti datum - razlicit stack trace -> nova iznimka
            if (!stackTrace.equals(lastStackTrace)) {
                return true;
            }
        }
        return false;
    }
    
    
    /**
     * Metoda parsira datum tipa string u datum tipa date
     * Prvo provjerava je li datum zapisan u zadanom obliku
     *      ako je, vraća njega
     *      ako nije, vraća zadnji datum koji je bio u ispravnom obliku
     * @param datum
     * @return
     */
    private Date setDateFormat(String datum) {   
        Date date = null;
        
        try {
            DateFormat dateFormat = new SimpleDateFormat(appConfig.getDateFormat(), Locale.ENGLISH);
            //date = dateFormat.parse(datum);
            if (datum.equals(dateFormat.format(dateFormat.parse(datum)))) {
                date = dateFormat.parse(datum);
                return date;
            }
            else {
                return lastExceptionTime;
            }
            
            //return date;          
        } catch (ParseException e) {
            //e.printStackTrace();
            //return date;
            //return null;
        }
        return lastExceptionTime;
    }
    
    
    
    private String getBreakLine(String stackTrace) { 
        breakLine = "";
        
        int counterAt = 0;
        int counterCausedBy = 0;
        
        if (stackTrace.contains("Caused by:")) {
            //prvi redak nakon caused by
            String[] parts = stackTrace.split(" ");
            for (String part : parts) {
                if (part.equals("Caused")) {
                    counterCausedBy++;
                }
                
                if (counterCausedBy == 1) {
                    if (part.equals("at")) {
                        counterAt++;
                    }                    
                }
                
                if (counterCausedBy == 1 && counterAt == 1) {
                    breakLine = breakLine + " " + part;
                }
                else if (counterAt > 1) {
                    return breakLine;
                }
            }
        }
        else if (!stackTrace.contains("Caused by:")) {
            //prvi redak koji sadrzi at...
            String[] parts = stackTrace.split(" ");
            for (String part : parts) {
                if (part.equals("at")) {
                    counterAt++;  
                }
                
                if (counterAt == 1) {
                    breakLine = breakLine + " " + part;
                }
                else if (counterAt > 1) {
                    return breakLine;
                }
            }
        }        
        return breakLine;
    }


    
    public String getLogLevel() {
        return logLevel;
    }

    
    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }
    
}
