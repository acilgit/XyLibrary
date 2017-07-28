package com.test.baserefreshview;

/**
 * Created by XY on 2017-6-29.
 */

public class HotItem {
    String name;
    int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "HotItem{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
