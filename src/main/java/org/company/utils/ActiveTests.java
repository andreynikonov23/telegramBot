package org.company.utils;

import org.apache.log4j.Logger;
import org.company.bot.TelegramBot;
import org.company.service.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ActiveTests implements Serializable {
    private static final Logger logger = Logger.getLogger(ActiveTests.class);

    private static final File DATA_FILE = new File("C:\\telegramBotConf\\file.dat");
    private static HashMap<Long, Set<Test>> savedTests;
    private static HashMap<Long, Test> activeTests;

    private ActiveTests(){}

    public static void saveTest(Long chatId, Test sectionManager){
        logger.info(String.format("addActiveTest with Parameters (%d, %s)", chatId, sectionManager));
        if (savedTests.containsKey(chatId)){
            savedTests.get(chatId).add(sectionManager);
            serialize();
            logger.info(String.format("saveTests add new key-value %d-%s", chatId, sectionManager));
        } else {
            Set<Test> set = new HashSet<>();
            set.add(sectionManager);
            savedTests.put(chatId, set);
            serialize();
            logger.info(String.format("saveTests key=%s put=%s", chatId, sectionManager));
        }
    }
    public static void activateTest(Long chatId, Test sectionManager){
        logger.debug(String.format("addActiveTest with Parameters (%d, %s)", chatId, sectionManager));
        activeTests.put(chatId, sectionManager);
        serialize();
    }
    public static Set<Test> getTestsSet(long chatId){
        return savedTests.get(chatId);
    }
    public static Test getActiveTest(long chatId){
        return activeTests.get(chatId);
    }
    public static void clear(Long chatId, String tag){
        savedTests.get(chatId).removeIf(section -> section.getTag().equals(tag));
        activeTests.remove(chatId);
        serialize();
        logger.info(String.format("ChatId=%d test %s hashMaps clear", chatId, tag));
    }
    public static Test getIncompleteTest(long chatId, String tag){
        logger.info(String.format("ChatId=%d getIncompleteTest()", chatId));
        Test incompleteTest = null;
        if (savedTests.containsKey(chatId)){
            for (Test test : savedTests.get(chatId)){
                if (test.getTag().equals(tag)){
                    incompleteTest = test;
                }
            }
        }
        return incompleteTest;
    }


    public static void serialize(){
        logger.debug("saving data");
        try(ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(DATA_FILE))){
            stream.writeObject(savedTests);
            stream.writeObject(activeTests);
        } catch (FileNotFoundException e) {
            logger.error("saving data error");
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("saving data error");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public static void deserialize(AnnotationConfigApplicationContext context) throws IOException {
        if (!(DATA_FILE.exists())){
            logger.debug("creation file.dat");
            Files.createFile(DATA_FILE.toPath());
            savedTests = new HashMap<>();
            activeTests = new HashMap<>();
        } else if (Files.size(DATA_FILE.toPath()) > 0){
            logger.debug("deserialization data...");
            try(ObjectInputStream stream = new ObjectInputStream(new FileInputStream(DATA_FILE))){
                savedTests = (HashMap<Long, Set<Test>>) stream.readObject();
                activeTests = (HashMap<Long, Test>) stream.readObject();
                for (Map.Entry<Long, Set<Test>> entry : savedTests.entrySet()){
                    for (Test manager : entry.getValue()){
                        manager.setBot(context.getBean(TelegramBot.class));
                    }
                }
            } catch (IOException e) {
                logger.error("deserialization data error!");
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                logger.error("deserialization data error!");
                throw new RuntimeException(e);
            }
        } else {
            savedTests = new HashMap<>();
            activeTests = new HashMap<>();
        }
    }
}
