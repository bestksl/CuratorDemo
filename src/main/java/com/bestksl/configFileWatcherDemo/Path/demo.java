package com.bestksl.configFileWatcherDemo.Path;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

public class demo {
    static ArrayList<Integer> repo = new ArrayList<>();

    public static void main(String[] args) {
        repo.add(5);
        repo.add(5);
        repo.add(10);
        repo.add(10);
        int[] input = {10, 10, 5, 50, 5, 20, 5, 10, 20};
        System.out.println(exchange(input));
    }

    public static int exchange(int[] bills) {

        if (bills == null || bills.length == 0) {
            return 0;
        }

        for (int i = 0; i < bills.length; i++) {
            Collections.sort(repo);
            Collections.reverse(repo);
            ArrayList<Integer> temp = repo;
            for (int j = 0; j < repo.size(); j++) {
                int money = repo.get(j);
                if (bills[i] > 0 && bills[i] >= money) {
                    temp.remove(j);
                    bills[i] -= money;
                } else if (bills[i] == 0) {
                    temp.add(bills[i]);
                    repo = temp;
                    break;
                } else if (bills[i] < 0) {
                    return i + 1;
                }
            }
        }
        return 0;
    }

}
