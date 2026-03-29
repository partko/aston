package org.example;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        MyHashMap<String, Integer> map = new MyHashMap<>();

        System.out.println("=== Empty map ===");
        System.out.println("size = " + map.size());
        System.out.println("isEmpty = " + map.isEmpty());
        System.out.println("map = " + map);
        System.out.println();

        System.out.println("=== put ===");
        System.out.println("put(one, 1) = " + map.put("one", 1));
        System.out.println("put(two, 2) = " + map.put("two", 2));
        System.out.println("put(three, 3) = " + map.put("three", 3));
        System.out.println("map = " + map);
        System.out.println("size = " + map.size());
        System.out.println();

        System.out.println("=== get ===");
        System.out.println("get(one) = " + map.get("one"));
        System.out.println("get(two) = " + map.get("two"));
        System.out.println("get(unknown) = " + map.get("unknown"));
        System.out.println();

        System.out.println("=== update existing key ===");
        System.out.println("put(one, 100) = " + map.put("one", 100));
        System.out.println("get(one) = " + map.get("one"));
        System.out.println("map = " + map);
        System.out.println();

        System.out.println("=== null key ===");
        System.out.println("put(null, 999) = " + map.put(null, 999));
        System.out.println("get(null) = " + map.get(null));
        System.out.println("containsKey(null) = " + map.containsKey(null));
        System.out.println("map = " + map);
        System.out.println();

        System.out.println("=== contains ===");
        System.out.println("containsKey(two) = " + map.containsKey("two"));
        System.out.println("containsKey(xxx) = " + map.containsKey("xxx"));
        System.out.println("containsValue(3) = " + map.containsValue(3));
        System.out.println("containsValue(1111) = " + map.containsValue(1111));
        System.out.println();

        System.out.println("=== remove ===");
        System.out.println("remove(two) = " + map.remove("two"));
        System.out.println("remove(xxx) = " + map.remove("xxx"));
        System.out.println("map = " + map);
        System.out.println("size = " + map.size());
        System.out.println();

        System.out.println("=== remove(key, value) ===");
        System.out.println("remove(one, 5) = " + map.remove("one", 5));
        System.out.println("remove(one, 100) = " + map.remove("one", 100));
        System.out.println("map = " + map);
        System.out.println();

        System.out.println("=== replace ===");
        map.put("aaa", 10);
        System.out.println("replace(aaa, 20) = " + map.replace("aaa", 20));
        System.out.println("get(aaa) = " + map.get("aaa"));
        System.out.println("replace(aaa, 20, 30) = " + map.replace("aaa", 20, 30));
        System.out.println("replace(aaa, 20, 40) = " + map.replace("aaa", 20, 40));
        System.out.println("get(aaa) = " + map.get("aaa"));
        System.out.println();

        System.out.println("=== putAll ===");
        Map<String, Integer> other = new HashMap<>();
        other.put("A", 1);
        other.put("B", 2);
        other.put("C", 3);
        map.putAll(other);
        System.out.println("map = " + map);
        System.out.println();

        System.out.println("=== keySet ===");
        for (String key : map.keySet()) {
            System.out.println("key = " + key);
        }
        System.out.println();

        System.out.println("=== values ===");
        for (Integer value : map.values()) {
            System.out.println("value = " + value);
        }
        System.out.println();

        System.out.println("=== entrySet ===");
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
        System.out.println();

        System.out.println("=== iterator remove ===");
        MyHashMap<String, Integer> iterMap = new MyHashMap<>();
        iterMap.put("x", 1);
        iterMap.put("y", 2);
        iterMap.put("z", 3);

        var it = iterMap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            if ("y".equals(key)) {
                it.remove();
            }
        }

        System.out.println("after iterator.remove(): " + iterMap);
        System.out.println("containsKey(y) = " + iterMap.containsKey("y"));
        System.out.println();

        System.out.println("=== clear ===");
        map.clear();
        System.out.println("map = " + map);
        System.out.println("size = " + map.size());
        System.out.println("isEmpty = " + map.isEmpty());

    }
}
