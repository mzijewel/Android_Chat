package com.example.nayan.chatappupdated.model;

/**
 * Created by Dev on 1/17/2018.
 */

import java.util.ArrayList;


public class Consersation {
    private ArrayList<Message2> listMessageData;

    public Consersation() {
        listMessageData = new ArrayList<>();
    }

    public ArrayList<Message2> getListMessageData() {
        return listMessageData;
    }
}
