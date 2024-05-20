module org.company.test {
    requires org.company;
    requires  org.junit.jupiter.api;
    requires  spring.context;
    requires  org.mockito;
    requires  org.mockito.junit.jupiter;
    requires  telegrambots.meta;
    requires  org.junit.platform.launcher;
    requires java.xml.bind;
    requires jakarta.activation;

    opens org.company.test.service to org.junit.platform.commons;
    opens org.company.test.data to org.junit.platform.commons;
}