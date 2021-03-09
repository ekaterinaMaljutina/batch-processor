package com.finago.interview.task.service;


import com.finago.interview.task.event.FindFilesEvent;
import com.finago.interview.task.model.ReceiverItem;
import com.finago.interview.task.model.Receivers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationListener;

import static com.finago.interview.task.utils.Utils.*;

public class FilesChecker implements DisposableBean, ApplicationListener<FindFilesEvent> {

    private static final Logger logger = LoggerFactory.getLogger("task.process");

    private volatile boolean destroy = false;

    @Override
    public void onApplicationEvent(FindFilesEvent event) {
        if (destroy) return;
        event.getFiles().forEach(this::proceed);
    }

    private void proceed(String xmlFile) {
        logger.info(" current file {}", xmlFile);
        Receivers scheme = readXML(IN_FOLDER + xmlFile);
        if (scheme == null) {
            moveErrorXML(xmlFile);
            return;
        }
        scheme.getReceivers().forEach(this::processOneItem);
        move(IN_FOLDER + xmlFile, ARCHIVE_FOLDER + xmlFile);
    }

    private void processOneItem(ReceiverItem item) {
        if (!checkMd5Checksum(IN_FOLDER + item.getFullFileName(), item.getMd5())) {
            movePdfAndXML(item, ERROR_FOLDER);
            return;
        }
        movePdfAndXML(item, OUT_FOLDER);
    }

    @Override
    public void destroy() {
        destroy = true;
    }
}
