open module org.company {
    requires javafx.controls;
    requires javafx.fxml;
    requires log4j;
    requires spring.beans;
    requires spring.context;
    requires spring.aop;
    requires spring.core;
    requires spring.jcl;
    requires spring.expression;
    requires telegrambots.meta;
    requires telegrambots;
    requires com.opencsv;
    requires lombok;

    exports org.company;
}