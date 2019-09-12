package com.eccos.nadzorniservis.models;

import java.util.List;

import javax.persistence.*;

import com.eccos.nadzorniservis.models.Exception;

@Entity
@Table(name="exception")
public class Exception {
    
    @Id
    @GeneratedValue
    @Column(name="id_exception")
    private int idException;
    
    @Column(name="exception_name", length=65535) //512
    private String exceptionName;
    
    @Column(name="break_line", length=512)
    private String breakLine;
    
    @Column(name="stack_trace", length=65535)
    private String stackTrace;
    
    @Column(name="solved")
    private boolean solved;
    
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL) //jedna iznimka se može dogoditi više puta
    @JoinColumn(name = "exception")
    private List<ExceptionTime> listExceptionTime;    
    
    @ManyToOne(targetEntity = ExceptionFile.class)
    @JoinColumn(name = "file", referencedColumnName = "id_exception_file")
    private ExceptionFile exceptionFile;
    
    
    public Exception() {
        //super();
    }
    
    
    public Exception(String exceptionName, String breakLine, String stackTrace) {
        super();
        this.exceptionName = exceptionName;
        this.breakLine = breakLine;
        this.stackTrace = stackTrace;
    }
    
    
    public Exception(String exceptionTime, String breakLine, String stackTrace, List<ExceptionTime> listExceptionTime) {
        super();
        this.exceptionName = exceptionTime;
        this.breakLine = breakLine;
        this.stackTrace = stackTrace;
        this.listExceptionTime = listExceptionTime;
    }
       
    
    public int getIdException() {
        return idException;
    }

    
    public void setIdException(int idException) {
        this.idException = idException;
    }

    
    public String getExceptionName() {
        return exceptionName;
    }

    
    public void setExceptionName(String exceptionName) {
        this.exceptionName = exceptionName;
    }

    
    public String getBreakLine() {
        return breakLine;
    }

    
    public void setBreakLine(String breakLine) {
        this.breakLine = breakLine;
    }

    
    public String getStackTrace() {
        return stackTrace;
    }

    
    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }    
    
    
    public boolean isSolved() {
        return solved;
    }

    
    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    
    public List<ExceptionTime> getListExceptionTime() {
        return listExceptionTime;
    }

    
    public void setListExceptionTime(List<ExceptionTime> listExceptionTime) {
        this.listExceptionTime = listExceptionTime;
    }
    
    
    public ExceptionFile getExceptionFile() {
        return exceptionFile;
    }
    
    
    public void setExceptionFile(ExceptionFile exceptionFile) {
        this.exceptionFile = exceptionFile;
    }
}
