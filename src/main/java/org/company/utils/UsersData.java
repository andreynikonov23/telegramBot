package org.company.utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class UsersData {
    private static final Logger logger = Logger.getLogger(UsersData.class);
    private final String FILE_PATH = "C:/telegramBotConf/users.csv";
    private final List<String> USERS = new ArrayList<>();

    public UsersData(){
        createFileIfItDoesNotExists();
        loadUsers();
    }

    public void createFileIfItDoesNotExists(){
        logger.debug("Creating users.csv");
        File file = new File(FILE_PATH);
        if (!(file.exists())){
            try {
                Files.createFile(Path.of(FILE_PATH));
            } catch (IOException e) {
                logger.error("Creating users.csv");
                logger.error(e.getStackTrace());
                throw new RuntimeException(e);
            }
        }
    }
    public void loadUsers(){
        logger.debug("Reading users.csv");
        List<String[]> strings;
        try (CSVReader csvReader = new CSVReader(new FileReader(FILE_PATH))) {
            strings = csvReader.readAll();
        } catch (CsvException | IOException e) {
            throw new RuntimeException(e);
        }

        for (String[] str : strings){
            USERS.add(str[1]);
        }
    }
    public void setNewUser(String username){
        logger.debug("Set new user " + username);
        USERS.add(username);
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(FILE_PATH))) {
            List<String[]> allLines = new ArrayList<>();
            for (int i=0; i<USERS.size(); i++){
                String[] line = {String.valueOf(i+1), USERS.get(i)};
                allLines.add(line);
            }
            csvWriter.writeAll(allLines);
        } catch (IOException e) {
            logger.error("Set new user " + username);
            logger.error(e.getStackTrace());
            throw new RuntimeException(e);
        }
    }
    public boolean isNewUser(String username){
        boolean isTrue = false;
        if (!(USERS.contains(username))){
            isTrue = true;
            setNewUser(username);
        }
        return isTrue;
    }
}
