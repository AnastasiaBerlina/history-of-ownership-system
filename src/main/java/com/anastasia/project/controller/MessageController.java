package com.anastasia.project.controller;

import com.anastasia.project.service.ProduceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
public class MessageController {

    private final ProduceInfo produceInfo;


    @Autowired
    public MessageController(ProduceInfo produceInfo) {
        this.produceInfo = produceInfo;
    }

    @GetMapping
    public void getMessages() {
        produceInfo.produceInfo();
    }


}
