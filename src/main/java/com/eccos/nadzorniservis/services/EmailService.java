package com.eccos.nadzorniservis.services;

import com.eccos.nadzorniservis.data.Email;

public interface EmailService {
    
    void sendMail(Email email);
    void sendHtmlMail(Email email);

}
