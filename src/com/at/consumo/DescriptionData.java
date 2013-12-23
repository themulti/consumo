package com.at.consumo;

import java.io.Serializable;

/**
* Created with IntelliJ IDEA.
* User: at
* Date: 4/18/13
* Time: 8:50 PM
*/
public class DescriptionData implements Serializable {

    public DescriptionData(String description, String value) {
        this.description = description;
        this.value = value;
    }

    private String description;
    private String value;

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
