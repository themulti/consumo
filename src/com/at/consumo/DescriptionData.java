package com.at.consumo;

/**
* Created with IntelliJ IDEA.
* User: at
* Date: 4/18/13
* Time: 8:50 PM
* To change this template use File | Settings | File Templates.
*/
public class DescriptionData {

    public DescriptionData(String description, String value) {
        this.description = description;
        this.value = value;
    }

    String description;
    String value;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
