package com.cheng.flowablebpmnbuilder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import javax.xml.parsers.DocumentBuilderFactory;

@SpringBootApplication
public class FlowableBpmnBuilderApplication {

    public static void main(String[] args) {
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        SpringApplication.run(FlowableBpmnBuilderApplication.class, args);
    }

}
