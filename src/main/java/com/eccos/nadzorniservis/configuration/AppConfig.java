package com.eccos.nadzorniservis.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    
    @Value("#{'${time.main.thread.sleep}'}")
    private int timeThreadSleep;
    
    @Value("#{'${email.receipients}'.split(',')}")
    private List<String> emailReceipients;
    
    @Value("#{'${file.parse.html}'.split(',')}")
    private List<String> htmlFile;
    
    @Value("#{'${file.parse.name.html}'.split(',')}")
    private List<String> htmlFileName;
    
    @Value("#{'${directory.parse}'.split(',')}")
    private List<String> folder;
    
    @Value("#{'${service.name}'}")
    private String nameNS;
    
    @Value("#{'${email.receipients}'.split(',')}")
    private String emailAddresses;
    
    @Value("#{'${log.level}'.split(',')}")
    private List<String> logLevel;
    
    @Value("#{'${date.format}'}")
    private String dateFormat;
    
    
    @Value("#{'${server.port}'}")
    private int serverHttpsPort;
    
    @Value("#{'${server.http.port}'}")
    private int serverHttpPort;
    
    @Value("#{'${server.http.scheme}'}")
    private String serverHttpScheme;
      
    
    
    public int getTimeThreadSleep() {
        return timeThreadSleep;
    }

    
    public void setTimeThreadSleep(int timeThreadSleep) {
        this.timeThreadSleep = timeThreadSleep;
    }


    public List<String> getEmailReceipients() {
        return emailReceipients;
    }

    
    public void setEmailReceipients(List<String> emailReceipients) {
        this.emailReceipients = emailReceipients;
    }
    
    
    public List<String> getHtmlFile() {
        return htmlFile;
    }

    
    public void setHtmlFile(List<String> htmlFile) {
        this.htmlFile = htmlFile;
    }
    
    
    public List<String> getHtmlFileName() {
        return htmlFileName;
    }


    public void setHtmlFileName(List<String> htmlFileName) {
        this.htmlFileName = htmlFileName;
    }

    
    public List<String> getFolder() {
        return folder;
    }

    
    public void setFolder(List<String> folder) {
        this.folder = folder;
    }


    public String getNameNS() {
        return nameNS;
    }

    
    public void setNameNS(String nameNS) {
        this.nameNS = nameNS;
    }

    public String getEmailAddresses() {
        return emailAddresses;
    }

    
    public void setEmailAddresses(String emailAddresses) {
        this.emailAddresses = emailAddresses;
    }

    
    public List<String> getLogLevel() {
        return logLevel;
    }

    
    public void setLogLevel(List<String> logLevel) {
        this.logLevel = logLevel;
    }

    
    public String getDateFormat() {
        return dateFormat;
    }

    
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }


    public int getServerHttpPort() {
        return serverHttpPort;
    }


    public void setServerHttpPort(int serverHttpPort) {
        this.serverHttpPort = serverHttpPort;
    }

    
    public int getServerHttpsPort() {
        return serverHttpsPort;
    }

    
    public void setServerHttpsPort(int serverHttpsPort) {
        this.serverHttpsPort = serverHttpsPort;
    }

    
    public String getServerHttpScheme() {
        return serverHttpScheme;
    }


    public void setServerHttpScheme(String serverHttpScheme) {
        this.serverHttpScheme = serverHttpScheme;
    }
}
