package com.eccos.nadzorniservis.models;

import javax.persistence.*;

@Entity
@Table(name="exception_time")
public class ExceptionTime {
    @Id
    @GeneratedValue
    @Column(name="id_time")
    private int idTime;
    
    @Column(name="time")
    private String time;
    
    @ManyToOne(targetEntity = Exception.class) //vise puta ista izminka
    @JoinColumn(name = "exception", referencedColumnName = "id_exception")
    private Exception exception;
    
    //private int idIznimka;
    
    public ExceptionTime() {
        super();
    }
    
    public ExceptionTime(/*int idIznimka,*/ String time) {
        //this.idIznimka = idIznimka;
        this.time = time;
    }
    
    public int getIdTime() {
        return idTime;
    }


    public void setIdTime(int idTime) {
        this.idTime = idTime;
    }

    
    public String getTime() {
        return time;
    }

    
    public void setTime(String time) {
        this.time = time;
    }

    
    public Exception getException() {
        return exception;
    }
 
    
    public void setException(Exception exception) {
        this.exception = exception;
    }

}
