package com.xycode.xylibrary.unit;

        import java.io.Serializable;
        import java.util.List;

/**
 * Created by XY on 2016-10-11.
 */

public class ContactUnit implements Serializable {
    private String name;
    private String phone;
    private String detail;
    private List<String> phoneList;

    public ContactUnit() {
    }

    public ContactUnit(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public List<String> getPhoneList() {
        return phoneList;
    }

    public void setPhoneList(List<String> phoneList) {
        this.phoneList = phoneList;
    }
}
