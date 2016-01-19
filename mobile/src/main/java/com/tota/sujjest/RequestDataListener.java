package com.tota.sujjest;

import com.tota.sujjest.Entity.Restaurant;

import java.util.ArrayList;

/**
 * Created by aprabhakar on 1/18/16.
 */
public interface RequestDataListener {

    ArrayList<Restaurant> onRequestData(String Tag);
}
