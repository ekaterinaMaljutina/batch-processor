package com.finago.interview.task.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;


@XStreamAlias("receivers")
public class Receivers {

    @XStreamImplicit
    private List<ReceiverItem> receiver;

    public List<ReceiverItem> getReceivers() {
        return receiver;
    }

    public void addReceiver(ReceiverItem item) {
        if (receiver == null) {
            receiver = new ArrayList<>();
        }
        receiver.add(item);
    }

    @Override
    public String toString() {
        return "Receivers{" +
                "receiver=" + receiver +
                '}';
    }
}