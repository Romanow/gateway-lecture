package ru.romanow.gateway.models;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Series {

    private String name;
    private String type;
    private String complexity;
    private Integer age;

}
