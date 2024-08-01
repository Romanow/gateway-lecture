package ru.romanow.gateway.models;

import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LegoSetWithSeries {

    private String number;
    private String name;
    private Integer age;
    private Integer partsCount;
    private BigDecimal suggestedPrice;
    private Series series;

}
