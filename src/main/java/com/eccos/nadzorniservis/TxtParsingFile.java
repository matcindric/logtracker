package com.eccos.nadzorniservis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eccos.nadzorniservis.configuration.AppConfig;

//import org.apache.log4j.Logger;

import com.eccos.nadzorniservis.models.Exception;
import com.eccos.nadzorniservis.models.ExceptionTime;

@Component
public class TxtParsingFile {
    
    @Autowired
    AppConfig appConfig;
    
    
    String exceptionName = "";
    String breakLine = "";
    String stackTrace = "";
    
    String exceptionTime;
    
    int counterCausedBy = 0;
    int counterException = 0;
    
    Date dateLastException;
    
    public List<ExceptionTime> listExceptionTime;
    
    private static final Logger logger = LogManager.getLogger(TxtParsingFile.class);
    
    private TxtParsingFile() {
        
    }
    
    
    /**
     * Metoda zapocinje s parsiranjem iznimki u datoteci od prve iznimke koja jos nije parsirana,
     * vec parsirane iznimke preskace
     * @param parametar
     * @throws IOException
     */
    public void parsingFile(String parametar) { 
        try {
            File file = new File(parametar);
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));

            listExceptionTime = new ArrayList<>();
            
            String st;
            boolean pocetak = false;
            
            while ((st = br.readLine()) != null) {                
                
                if (dateLastException == null) {
                    parsingException(st);
                }
                else if (findNewExceptions(st)) { // dohvati tu liniju i od tuda zapocni s parsiranjem                              
                    pocetak = true;               
                    parsingException(st);
                }
                else if (pocetak) { //kad jednom uđe u pocetak, parsira dok ne dođe do kraja datoteke
                    //nastavi parsirati red po red
                    parsingException(st);
                }
            }
            
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.toString());
        }            
    }
    
    
    /**
     * Metoda parsira iznimku i dodaje ju u listuVremenaIznimki koja sadrzi vrijeme kad se iznimka dogodila
     * i objekt iznimku na koju se vrijeme odnosi
     * @param st
     * @param listaIznimki
     */
    private void parsingException(String st) {
 
        if (provjeriPocetakStacka(st) && counterException == 0) {
            dohvatiPrvuLinijuStacka(st);
            exceptionTime = getTimeFromStack(st);
        } 
        else if (!provjeriPocetakStacka(st) && counterException == 1 && !st.isEmpty()) {
            stackTrace = stackTrace + st + "\n";
            breakLine = getBreakLine(st);             
        }
        else if ((provjeriPocetakStacka(st) && counterException == 1) || (st.isEmpty() && counterException == 1)) { //provjera za prazan red potreban za zadnji exception                          
  
            Exception exception = new Exception();
            exception.setExceptionName(exceptionName);
            exception.setBreakLine(breakLine);
            exception.setStackTrace(stackTrace);

            ExceptionTime vi = new ExceptionTime();
            vi.setException(exception);
            vi.setTime(exceptionTime);
            listExceptionTime.add(vi);

            exceptionName = "";
            breakLine = "";
            stackTrace = "";
            counterCausedBy = 0;
            counterException = 0;
            exceptionTime = "";
            
            dohvatiPrvuLinijuStacka(st);
            exceptionTime = getTimeFromStack(st);
            
            int i = listExceptionTime.size()-1;
            String vrijemeZadnjeIznimke = listExceptionTime.get(i).getTime();
            dateLastException = setDateFormat(vrijemeZadnjeIznimke);
        }       
    }
    
    
    /**
     * Metoda provjerava pocinje li red u datoteci sa datumom
     * ako pocinje onda dohvaca to vrijeme i parsira ga u date format
     * i provjerava je li taj datum veci od datuma zadnje iznimke
     * ako je, radi se o novoj iznimci
     * @param st
     */
    private boolean findNewExceptions(String st) {
        if (st.startsWith(getTimeFromStack(st))) {          
            //provjeri je li veci od zadnjeg spremljenog datuma
            Date newTime = setDateFormat(getTimeFromStack(st));
            if (newTime.after(dateLastException)) {
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
                return dateLastException;
            }
            //return date;          
        } catch (ParseException e) {
            //e.printStackTrace();
            //return date;
            //return null;
        }
        return dateLastException;
    }
    
    
    private String getExceptionName(String st) {

        String[] parts;
        parts = st.split(" ");
        for (int i = 4; i < parts.length; i++) {
            exceptionName = exceptionName + " " + parts[i];
        }
        return exceptionName;
    }
    
    
    private boolean provjeriPocetakStacka(String st) {

        String[] parts;
        String dateTime;
        
        if (st.length() > 0) {
            parts = st.split(" ");
            if (parts.length > 1) {
                dateTime = parts[0] + " " + parts[1];

                String regTime = "[0-9]{1,2}-[a-zA-z]{1,3}-[0-9]{1,4} [0-9]{1,2}:[0-9]{1,2}:[0-9]{1,2}.[0-9]{1,3}";

                Pattern pattern = Pattern.compile(regTime);
                Matcher m = pattern.matcher(dateTime);
                boolean status = m.matches();

                if (status) {
                    return true;
                } 
                else {
                    return false;
                }
            }            
        }
        return false;
    }
    
    
    private void dohvatiPrvuLinijuStacka(String st) {
        if (st.contains("SEVERE") || st.contains("WARNING")) {
            getExceptionName(st);
            String[] parts;
            parts = st.split(" ");
            for (int i = 2; i < parts.length; i++) {
                stackTrace = stackTrace + " " + parts[i];
            }
            stackTrace = stackTrace + "\n";
            //stackTrace = stackTrace + " " + st + "\n";
            counterException++;                   
        }
    }
    
    
    private String getTimeFromStack(String st) {
        if (st.contains("SEVERE") || st.contains("WARNING")) {
            String[] parts;
            parts = st.split(" ");
            exceptionTime = parts[0] + " " + parts[1];
        }       
        return exceptionTime;
    }

    
    private String getBreakLine(String st) {       
        
        String[] parts;
        parts = st.split(" ");
        
        if (st.startsWith("\tat")) {
            if (breakLine.equals("")) {
                for (int i = 1; i < parts.length; i++) {
                    breakLine = breakLine + " " + parts[i];
                }
            }
        }
        else if (st.startsWith("Internal Exception:")) {
            if (breakLine.equals("")) {
                breakLine = st;
            }
        }
        else if (st.startsWith("Caused by:")) {
            if (counterCausedBy == 0) {
                breakLine = "";
            }
            else if (counterCausedBy == 1) {
                return breakLine;
            }
            counterCausedBy++;
        }
        else if (st.startsWith("importPackage")) {
            if (breakLine.equals("")) {
                breakLine = "";
            }
        }
        else {
            return breakLine;
        }
        
        return breakLine;
    } 
    
}
