package com.eccos.nadzorniservis.services;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eccos.nadzorniservis.models.ExceptionTime;

@Repository
public interface ExceptionTimeService extends JpaRepository<ExceptionTime, Integer> {
    
    List<ExceptionTime> getTime(int idException);
}
