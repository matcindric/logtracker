package com.eccos.nadzorniservis.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eccos.nadzorniservis.models.ExceptionTime;
import com.eccos.nadzorniservis.services.ExceptionTimeService;

@Service
public class ExceptionTimeServiceImpl {
    
    @Autowired
    private ExceptionTimeService exceptionTimeService;
    
    /**
     * Metoda dovaca sva vremena za pojedinu iznimku
     * @param idException
     * @return
     */
    public List<ExceptionTime> getTime(int idException) {
        List<ExceptionTime> listTime = new ArrayList<>();
        List<ExceptionTime> listTimeOutput = new ArrayList<>();
        listTime = exceptionTimeService.findAll();
        for (int i = 0; i < listTime.size(); i++) {
            if (listTime.get(i).getException().getIdException() == idException) {
                listTimeOutput.add(listTime.get(i));
            }
        }
        return listTimeOutput;
    }

}
