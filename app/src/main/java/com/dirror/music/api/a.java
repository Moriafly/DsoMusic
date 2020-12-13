package com.dirror.music.api;

import java.util.ArrayList;

public class a {
    public static void main(String[] args) {

    }

    public static void r() {
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        for (int i = 0; i < 10; i++) {
            arrayList.add(i);
        }
        int sum = 0;
        for(int i:arrayList){
            sum += i;
        }
        System.out.println(sum);
    }
}
