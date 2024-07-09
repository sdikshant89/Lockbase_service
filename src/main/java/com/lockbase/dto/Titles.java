package com.lockbase.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Titles {

    // This attribute is used to rename the class variable in request and response
    @JsonProperty("title_list")
    private List<String> title;

    public List<String> getTitle() {
        return title;
    }
    public void setTitle(List<String> title) {
        this.title = title;
    }
}
