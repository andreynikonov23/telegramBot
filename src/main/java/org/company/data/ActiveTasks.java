package org.company.data;

import org.apache.log4j.Logger;
import org.company.bot.TelegramBot;
import org.company.service.Task;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ActiveTasks implements Serializable {
    private static final Logger logger = Logger.getLogger(ActiveTasks.class);

    private static final File DATA_FILE = new File("C:\\telegramBotConf\\file.dat");
    private static HashMap<Long, Set<Task>> savedTasks;
    private static HashMap<Long, Task> activeTasks;


    public static void saveTask(Long chatId, Task sectionManager) {
        logger.info(String.format("addActiveTask with Parameters (%d, %s)", chatId, sectionManager));
        if (savedTasks.containsKey(chatId)) {
            savedTasks.get(chatId).add(sectionManager);
            serialize();
            logger.info(String.format("saveTask add new key-value %d-%s", chatId, sectionManager));
        } else {
            Set<Task> set = new HashSet<>();
            set.add(sectionManager);
            savedTasks.put(chatId, set);
            serialize();
            logger.info(String.format("saveTask key=%s put=%s", chatId, sectionManager));
        }
    }

    public static void activateTask(Long chatId, Task sectionManager) {
        logger.debug(String.format("addActiveTask with Parameters (%d, %s)", chatId, sectionManager));
        activeTasks.put(chatId, sectionManager);
        serialize();
    }

    public static Task getActiveTask(long chatId) {
        return activeTasks.get(chatId);
    }

    public static void clear(Long chatId, String tag) {
        savedTasks.get(chatId).removeIf(section -> section.getTag().equals(tag));
        activeTasks.remove(chatId);
        serialize();
        logger.info(String.format("ChatId=%d task %s hashMaps clear", chatId, tag));
    }

    public static Task getIncompleteTask(long chatId, String tag) {
        logger.info(String.format("ChatId=%d getIncompleteTask()", chatId));
        Task incompleteTask = null;
        if (savedTasks.containsKey(chatId)) {
            for (Task task : savedTasks.get(chatId)) {
                if (task.getTag().equals(tag)) {
                    incompleteTask = task;
                    break;
                }
            }
        }
        return incompleteTask;
    }

    public static void serialize() {
        logger.debug("saving data");
        try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            stream.writeObject(savedTasks);
            stream.writeObject(activeTasks);
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
        if (!(DATA_FILE.exists())) {
            logger.debug("creation file.dat");
            Files.createFile(DATA_FILE.toPath());
            initSets();
        } else if (Files.size(DATA_FILE.toPath()) > 0) {
            logger.debug("deserialization data...");
            try (ObjectInputStream stream = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
                savedTasks = (HashMap<Long, Set<Task>>) stream.readObject();
                activeTasks = (HashMap<Long, Task>) stream.readObject();
                for (Map.Entry<Long, Set<Task>> entry : savedTasks.entrySet()) {
                    for (Task task : entry.getValue()) {
                        task.setBot(context.getBean(TelegramBot.class));
                        task.setQuestionsLoader(context.getBean(QuestionsLoader.class));
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                logger.error("deserialization data error!");
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else {
            initSets();
        }
    }

    private static void initSets() {
        savedTasks = new HashMap<>();
        activeTasks = new HashMap<>();
    }
}
