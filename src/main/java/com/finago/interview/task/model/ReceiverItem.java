package com.finago.interview.task.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("receiver")
public class ReceiverItem {
    @XStreamAlias("receiver_id")
    private int id;

    @XStreamAlias("first_name")
    private String firstName;

    @XStreamAlias("last_name")
    private String lastName;

    @XStreamAlias("file")
    private String fileName;

    @XStreamAlias("file_md5")
    private String md5;

    public ReceiverItem() {
    }

    public ReceiverItem(int id, String firstName, String lastName, String fileName, String md5) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fileName = fileName;
        this.md5 = md5;
    }

    public Integer getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullFileName() {
        return fileName;
    }

    public String getShortFileName() {
        return fileName.substring(0, fileName.indexOf("."));
    }

    public String getMd5() {
        return md5;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", md5='" + md5 + '\'' +
                '}';
    }
}

