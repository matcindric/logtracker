package com.eccos.nadzorniservis;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

import com.eccos.nadzorniservis.configuration.AppConfig;
import com.eccos.nadzorniservis.data.Email;
import com.eccos.nadzorniservis.models.Exception;
import com.eccos.nadzorniservis.models.ExceptionFile;
import com.eccos.nadzorniservis.models.ExceptionTime;
import com.eccos.nadzorniservis.services.ExceptionFileService;
import com.eccos.nadzorniservis.services.ExceptionService;
import com.eccos.nadzorniservis.services.ExceptionTimeService;
import com.eccos.nadzorniservis.services.impl.EmailServiceImpl;

@Component
@Scope("prototype")
public class WatchThread extends Thread {
    
    @Autowired
    private ExceptionService exceptionService;
    
    @Autowired
    private ExceptionTimeService exceptionTimeService;
    
    @Autowired
    private EmailServiceImpl emailService;
    
    @Autowired
    private AppConfig appConfig;
    
    @Autowired
    TxtParsingFile txtParsingFile;
    
    @Autowired
    HTMLParsingFile htmlParsingFile;
    
    @Autowired
    ExceptionFileService exceptionFileService;
    
    
    private String directory;
    private String fileName;
    private String file;
    private String logLevel;
    
    private Exception exception;
    private ExceptionTime exceptionTime;
    private ExceptionFile exceptionFile;
    
    
    //lista svih vremena iznimki
    List<ExceptionTime> listExceptionTimeAll = new ArrayList<>();   
    
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    
    private static final Logger logger = LogManager.getLogger(NadzorniServisStartup.class);
    
    @Override
    public void run() {
        startWatching();
    }
    
    
    public void startWatching() {

        /*
         * nadgleda se direktorij - > određen u path
         * kada dođe do promjene u nekoj od datoteka u direktoriju, watch service registrira promjenu
         * parsira se datoteka, promjene se spremaju u bazu i salje se mail u slucaju novog exceptiona
         * zatim se ceka period određen unutar Thread.sleep()
         * ako unutar tog vremena dođe do neke promjene na datoteci, WS ce se aktivirati
         * ako ne dođe do promjene, WS ce cekati promjenu i tek onda se aktivirati
         */      
        final Path path = FileSystems.getDefault().getPath(directory);
        try (final WatchService watchService = FileSystems.getDefault().newWatchService()) {
            path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
  
            while (true) {      
                try {
                    final WatchKey wk = watchService.take(); 
                    Thread.sleep(50);
                    for (WatchEvent<?> event : wk.pollEvents()) {                  
                                              
                        final Path changed = (Path) event.context();
                        if (changed.endsWith(fileName)) {
                            System.out.println("The time is now " + dateFormat.format(new Date()));
                            System.out.println(file);
                            logger.info("The time is now " + dateFormat.format(new Date()) + " - new exceptions from " + file);
                            saveDB(file);                            
                            System.out.println();
                            Thread.sleep(appConfig.getTimeThreadSleep());
                        }    
                    }
                    boolean valid = wk.reset();
                    if (!valid) {
                        System.out.println("Key has been unregisterede");
                        logger.info("Key has been unregisterede");
                    }
                } catch (InterruptedException ex) {                            
                    Thread.currentThread().interrupt(); 
                    logger.error("NS je prekinuo s radom!");
                    return;
                }
                
            }
        } catch (IOException e) {
            logger.error("Aplikacija nije uspješno pokrenuta", e.getMessage());
            e.printStackTrace();
        }   
    }
    
    
    /**
     * Dohvaca se listaSvihVremenaIznimki koja sadrži sve nove iznimke koje su se dogodile od zadnjeg puta
     * kad je watch service dojavio promjenu
     * Na pocetku, baza je prazna i sprema prvu iznimku koja se dogodila i salje mail
     * Dalje nastavlja uspoređivati nove iznimke iz listaSvihVremenaIznimki sa vec poostojecim iznimkama iz baze
     * ako se dogodila nova pogreska, sprema pogresku, vrijeme i salje mail programeru
     * ako se dogodila vec postojeca pogreska, samo sprema vrijeme u bazu
     */
    public void saveDB(String file) {
        List<Exception> listFromDB = new ArrayList<>();
        List<ExceptionTime> listTimeFromDB = new ArrayList<>();

        ExceptionTime exceptionTime;
        Exception exception;
        
        //ZA PARSIRANJE TXT FILE-A
        //txtParsingFile.parsingFile(appConfig.getFile());
        //listExceptionTimeAll = txtParsingFile.listExceptionTime;
        
        //ZA PARSIRANJE HTML FILE-A
        htmlParsingFile.setLogLevel(logLevel);
        htmlParsingFile.parsingHTMLFile(file);
        listExceptionTimeAll = htmlParsingFile.listExceptionTime;
        
        
        System.out.println(listExceptionTimeAll.size());
        
        listFromDB = exceptionService.findAll();
        
        if (listFromDB.isEmpty() && !listExceptionTimeAll.isEmpty()) {
            //spremi vrijeme i iznimku 
            
            saveNewException(0);           
            sendMail(0);
            
            boolean iste = false; //ista iznimka, novo vrijeme
            boolean ponovljena = false; // ista iznimka, isto vrijeme
            //dalje usporedi
            for (int j = 1; j < listExceptionTimeAll.size(); j++) {
                listFromDB = exceptionService.findAll();
                for (int k = 0; k < listFromDB.size(); k++) {
                    if (listFromDB.get(k).getStackTrace().equals(listExceptionTimeAll.get(j).getException().getStackTrace())) {     
                        
                        //provjera postoji li za neku iznimku vec to vrijeme -> potrebno zbog perzistentnosti da se ne ponavljaju unosi ako dođe do restarta servisa
                        listTimeFromDB = listFromDB.get(k).getListExceptionTime();
                        boolean ponovljenaIznimka = false;                        
                        for (int l = 0; l < listTimeFromDB.size(); l++) {
                            if (listTimeFromDB.get(l).getTime().equals(listExceptionTimeAll.get(j).getTime())) {
                                ponovljenaIznimka = true;
                                ponovljena = true;
                            }
                        }
                        
                        if(!ponovljenaIznimka) {
                          //dohvati iznimku(objekt) za to vrijeme
                            exception = new Exception();
                            exception.setIdException(listFromDB.get(k).getIdException());
                            
                            //samo vrijeme spremi
                            exceptionTime = new ExceptionTime();
                            exceptionTime.setTime(listExceptionTimeAll.get(j).getTime());
                            exceptionTime.setException(exception);
                            exceptionTimeService.save(exceptionTime);
                            
                            iste = true;
                        }   
                    }
                }
                if (!iste && !ponovljena) {                   
                    saveNewException(j);                                       
                    sendMail(j);
                } 
                iste = false;
                ponovljena = false;
            }
        }
        else {
            boolean isto = false; //ista iznimka, novo vrijeme
            boolean ponovljena = false; //ista iznimka, isto vrijeme
            
            for (int j = 0; j < listExceptionTimeAll.size(); j++) {
                listFromDB = exceptionService.findAll();
                for (int k = 0; k < listFromDB.size(); k++) {                    
                    if (listFromDB.get(k).getStackTrace().equals(listExceptionTimeAll.get(j).getException().getStackTrace())) {
                        
                        //ista iznimka - ista datoteka (i isto vrijeme) -> spremi samo vrijeme
                        if (listFromDB.get(k).getExceptionFile().getFile().equals(file)) {                            
                            //provjera postoji li za neku iznimku vec to vrijeme -> potrebno zbog perzistentnosti da se ne ponavljaju unosi ako dođe do restarta servisa
                            listTimeFromDB = listFromDB.get(k).getListExceptionTime();
                            boolean ponovljenaIznimka = false;                        
                            for (int l = 0; l < listTimeFromDB.size(); l++) {
                                if (listTimeFromDB.get(l).getTime().equals(listExceptionTimeAll.get(j).getTime())) {
                                    ponovljenaIznimka = true;
                                    ponovljena = true;
                                }
                            }
                            
                            if (!ponovljenaIznimka) {
                                /*
                                 * provjeri je li iznimka koja se opet dogodila oznacena kao rijesena
                                 * ako je oznacena kao rijesena:
                                 *      odznaci ju
                                 *      posalji mail da se iznimka opet dogodila
                                 *      spremi vrijeme
                                 * ako nije oznacena:
                                 *      samo spremi vrijeme kad se dogodila
                                 */                       
                                if (listFromDB.get(k).isSolved()) {
                                    //dohvati iznimku(objekt) za to vrijeme
                                      exception = new Exception();
                                      exception.setIdException(listFromDB.get(k).getIdException());
                                      exception.setExceptionName(listFromDB.get(k).getExceptionName());
                                      exception.setBreakLine(listFromDB.get(k).getBreakLine());
                                      exception.setStackTrace(listFromDB.get(k).getStackTrace());
                                      exception.setListExceptionTime(listFromDB.get(k).getListExceptionTime());
                                      exception.setSolved(false);
                                      exception.setExceptionFile(listFromDB.get(k).getExceptionFile());
                                      exceptionService.save(exception);                              
                                      
                                      //samo vrijeme spremi
                                      exceptionTime = new ExceptionTime();
                                      exceptionTime.setTime(listExceptionTimeAll.get(j).getTime());
                                      exceptionTime.setException(exception);
                                      exceptionTimeService.save(exceptionTime);
                                      
                                      isto = true;
                                      
                                      sendMail(j);
                                  }
                                  else {
                                      //dohvati iznimku(objekt) za to vrijeme
                                      exception = new Exception();
                                      exception.setIdException(listFromDB.get(k).getIdException());
                                      
                                      //samo vrijeme spremi
                                      exceptionTime = new ExceptionTime();
                                      exceptionTime.setTime(listExceptionTimeAll.get(j).getTime());
                                      exceptionTime.setException(exception);
                                      exceptionTimeService.save(exceptionTime);
                                      
                                      isto = true;
                                  }
                            }
                        }
                    }
                }             
                
                if (!isto && !ponovljena) {                    
                    saveNewException(j);                                                        
                    sendMail(j);
                } 
                isto = false;  
                ponovljena = false;
            }         
        }
    }
    
    
    private void saveNewException(int i) {
        
        if(!exceptionFileService.isExistFile(file)) { 
            exceptionFile = new ExceptionFile();
            exceptionFile.setFile(file);
            exceptionFile.setFileName(fileName);
            exceptionFileService.save(exceptionFile);
        }
        else {
            exceptionFile = exceptionFileService.findByFile(file);
        }
        
        exception = new Exception();
        exception.setExceptionName(listExceptionTimeAll.get(i).getException().getExceptionName());
        exception.setBreakLine(listExceptionTimeAll.get(i).getException().getBreakLine());
        exception.setStackTrace(listExceptionTimeAll.get(i).getException().getStackTrace());
        exception.setExceptionFile(exceptionFile);
        exceptionService.save(exception);  
        
        exceptionTime = new ExceptionTime();
        exceptionTime.setTime(listExceptionTimeAll.get(i).getTime());
        exceptionTime.setException(exception);
        exceptionTimeService.save(exceptionTime);
    }
    
    
    private void sendMail(int i) {       
        String exceptionTime = listExceptionTimeAll.get(i).getTime();
        String exceptionName = listExceptionTimeAll.get(i).getException().getExceptionName();
        String breakLine = listExceptionTimeAll.get(i).getException().getBreakLine();
        String stackTrace = listExceptionTimeAll.get(i).getException().getStackTrace();
        String file = getFile();
        
        String subject = "[ " + appConfig.getNameNS() + "] New exception " + " - " + exceptionTime;
        String title = "New exception - " + exceptionTime + " - " + exceptionName;       
        
        Context context = new Context();
        context.setVariable("title", title);
        context.setVariable("exceptionTime", exceptionTime);
        context.setVariable("exceptionName", exceptionName);
        context.setVariable("breakLine", breakLine);
        context.setVariable("stackTrace", stackTrace);
        context.setVariable("file", file);
        
        for (int j = 0; j < appConfig.getEmailReceipients().size(); j++) {
            Email email = new Email();
            email.setTo(appConfig.getEmailReceipients().get(j));
            email.setSubject(subject);
            email.setMessage(emailService.constructHTMLTemplateEmailBody("email/template-1", context));
            emailService.sendHtmlMail(email);
        }
    }
       
    
    public String getDirectory() {
        return directory;
    }

    
    public void setDirectory(String directory) {
        this.directory = directory;
    }


    public String getFileName() {
        return fileName;
    }

    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    
    public String getFile() {
        return file;
    }

    
    public void setFile(String file) {
        this.file = file;
    }

    
    public String getLogLevel() {
        return logLevel;
    }


    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    } 
        
}