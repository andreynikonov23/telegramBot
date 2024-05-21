module org.company {
    requires  javafx.controls;
    requires  javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires  log4j;
    requires  spring.beans;
    requires  spring.context;
    requires  spring.core;
    requires  spring.expression;
    requires  telegrambots.meta;
    requires  telegrambots;
    requires  com.opencsv;
    requires  lombok;


    opens org.company.controller to javafx.fxml;
    opens org.company to javafx.controls, javafx.fxml, javafx.graphics, javafx.base;
    opens org.company.config to spring.core;
    opens org.company.bot to spring.core;
    opens org.company.service to org.company.test;
    exports org.company;
    exports org.company.controller to javafx.fxml, javafx.controls;
    exports org.company.bot to spring.core, spring.beans, spring.context, org.company.test;
    exports org.company.config to spring.core, spring.beans, spring.context, org.company.test;
    exports org.company.service to spring.core, spring.beans, spring.context, org.company.test;
    exports org.company.data to spring.core, spring.beans, spring.context, org.company.test;
    exports org.company.model;
}