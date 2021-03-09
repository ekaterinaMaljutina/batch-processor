package com.finago.interview.task.event;

import org.springframework.context.ApplicationEvent;

import java.util.Set;

public class FindFilesEvent extends ApplicationEvent {

    private final Set<String> files;

    public FindFilesEvent(Object source, Set<String> files) {
        super(source);
        this.files = files;
    }

    public Set<String> getFiles() {
        return files;
    }

    @Override
    public String toString() {
        return "FindFileEvent{" +
                "files='" + files + '\'' +
                '}';
    }
}
