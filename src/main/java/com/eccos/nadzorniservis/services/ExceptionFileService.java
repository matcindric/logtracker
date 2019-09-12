package com.eccos.nadzorniservis.services;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eccos.nadzorniservis.models.ExceptionFile;


@Repository
public interface ExceptionFileService extends JpaRepository<ExceptionFile, Integer> {
    boolean isExistFile(String fileName);
    List<String> getAllFiles();
    ExceptionFile findByFile(String file);
}
