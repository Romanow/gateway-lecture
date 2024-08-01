package ru.romanow.dictionary.models;

import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LegoSet {

    private String number;
    private String name;
    private Integer age;
    private Integer partsCount;
    private BigDecimal suggestedPrice;
    private String seriesName;

}
