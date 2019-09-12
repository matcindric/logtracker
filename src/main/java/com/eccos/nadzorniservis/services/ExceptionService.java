package com.eccos.nadzorniservis.services;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eccos.nadzorniservis.models.Exception;

@Repository
public interface ExceptionService extends JpaRepository<Exception, Integer> {

    void setSolved(int id);
    List<Exception> getSolved(Date timeFrom, Date timeTo, String name, String stack, String file);
    List<Exception> getUnsolved(Date timeFrom, Date timeTo, String name, String stack, String file);
    List<Exception> getSolvedUnsolved(Date timeFrom, Date timeTo, String name, String stack, String file);
}
