package com.finago.interview.task.utils;

import com.finago.interview.task.model.ReceiverItem;
import com.finago.interview.task.model.Receivers;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Utils {

    private static final Logger logger = LoggerFactory.getLogger("path.utils");

    public static final String IN_FOLDER = System.getProperty("user.dir") + "/data/in/";
    public static final String OUT_FOLDER = System.getProperty("user.dir") + "/data/out/";
    public static final String ERROR_FOLDER = System.getProperty("user.dir") + "/data/error/";
    public static final String ARCHIVE_FOLDER = System.getProperty("user.dir") + "/data/archive/";

    public static final XStream xstream = new XStream(new StaxDriver(new NoNameCoder()));

    static {
        xstream.processAnnotations(Receivers.class);
        xstream.processAnnotations(ReceiverItem.class);
    }


    @FunctionalInterface
    public interface Action<T, U> {
        T apply(T t, T t1, U... options) throws IOException;
    }

    public static Receivers readXML(String fileName) {
        try {
            return (Receivers) xstream.fromXML(new File(fileName));
        } catch (RuntimeException e) {
            logger.error("can not read file {} ex {}", fileName, e.getMessage());
            return null;
        }
    }

    private static String makeDirectory(Integer receiverId, String folder) throws IOException {
        String path = folder + (receiverId % 100) + "/" + receiverId + "/";
        Files.createDirectories(Paths.get(path));
        return path;
    }

    public static String writeXML(ReceiverItem receiver, String folder) {
        try {
            String path = makeDirectory(receiver.getId(), folder);
            String xml = xstream.toXML(receiver);
            try (FileOutputStream outputStream = new FileOutputStream(path + receiver.getShortFileName() + ".xml")) {
                outputStream.write(xml.getBytes());
            }
            return path;
        } catch (Exception e) {
            logger.error("can not write xml file ex {}", e.getMessage());
        }
        return null;
    }

    public static boolean checkMd5Checksum(String fileName, String md5) {
        try (InputStream is = Files.newInputStream(Paths.get(fileName))) {
            String md5OfFile = DigestUtils.md5DigestAsHex(is);
            logger.info("check md5 {} {} {}", fileName, md5, md5OfFile);
            return md5.equals(md5OfFile);
        } catch (IOException ex) {
            logger.error("error {} {} ", fileName, md5);
            return false;
        }
    }

    private static void actionWithFile(Action<Path, CopyOption> action, String source, String target, String actionName) {
        try {
            action.apply(Paths.get(source), Paths.get(target), REPLACE_EXISTING);
        } catch (IOException e) {
            logger.error(" can not {}  {} -> {}", actionName, source, target);
        }
    }

    public static void move(String source, String target) {
        actionWithFile(Files::move, source, target, "move");
    }

    public static void copy(String source, String target) {
        actionWithFile(Files::copy, source, target, "copy");
    }


    public static void moveErrorXML(String fileName) {
        move(IN_FOLDER + fileName, ERROR_FOLDER + fileName);
    }

    public static void movePdfAndXML(ReceiverItem item, String folder) {
        String path = writeXML(item, folder);
        copy(IN_FOLDER + item.getFullFileName(), path + item.getFullFileName());
    }

    public static boolean checkExistFile(String fileName) {
        try (InputStream ignored = Files.newInputStream(Paths.get(fileName))) {
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

}
