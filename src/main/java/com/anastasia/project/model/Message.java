package com.anastasia.project.model;

public class Message {

    private String name;
    private String data;

    public Message(String name, String data) {
        this.name = name;
        this.data = data;
    }

    public Message() {
    }

    public String getName() {
        return name;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Message{" +
                "name='" + name + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
