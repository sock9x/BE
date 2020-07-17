package com.px.tool;

import java.util.Arrays;
import java.util.List;

public class TestCommon {
    public static void main(String[] args) {
        List<Long> ss = Arrays.asList(1L,2L,3L,4L);
        String s = "";

        for (Long cusReceiver : ss) {
            s += cusReceiver + ",";
        }
         
        System.out.println();
    }
}
