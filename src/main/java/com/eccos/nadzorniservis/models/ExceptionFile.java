package com.eccos.nadzorniservis.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="exception_file")
public class ExceptionFile {
    
    @Id
    @GeneratedValue
    @Column(name="id_exception_file")
    private int idExceptionFile;
    
    @Column(name="file")
    private String file;
    
    @Column(name="file_name")
    private String fileName;
    
    
    public int getIdExceptionFile() {
        return idExceptionFile;
    }

    
    public void setIdExceptionFile(int idExceptionFile) {
        this.idExceptionFile = idExceptionFile;
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
    
}
