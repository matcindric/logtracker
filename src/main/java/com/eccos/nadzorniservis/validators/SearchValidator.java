package com.eccos.nadzorniservis.validators;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class SearchValidator {
    
    public SearchValidator() {
        
    }
    
    public void checkDate(Date timeFrom, Date timeTo) {
        if (timeFrom.after(timeTo)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, 
                    "Upozorenje!", "Datum od kojeg pretražujete trebao bi biti manji od datuma do kojeg pretražujete!"));
            System.out.println("Ovo se dogodilo");
        }       
    }

}
