package ru.romanow.dictionary.domain;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "lego_set")
public class LegoSetEntity
        implements Identity<String> {

    @Id
    @Column(name = "number", length = 20, nullable = false, updatable = false, unique = true)
    private String number;

    @Column(name = "name", length = 120, nullable = false)
    private String name;

    @Column(name = "age")
    private Integer age;

    @Column(name = "parts_count", nullable = false)
    private Integer partsCount;

    @Column(name = "suggested_price", precision = 8, scale = 2, nullable = false)
    private BigDecimal suggestedPrice;

    @Column(name = "series_id", updatable = false, insertable = false)
    private String seriesName;

    @ManyToOne
    @JoinColumn(name = "series_id", nullable = false, foreignKey = @ForeignKey(name = "fk_lego_set_series_id"))
    private SeriesEntity series;

    @Override
    public String getId() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final LegoSetEntity that = (LegoSetEntity) o;

        return new EqualsBuilder().append(number, that.number)
                                  .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(number).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("number", number)
                .append("name", name)
                .append("age", age)
                .append("partsCount", partsCount)
                .append("suggestedPrice", suggestedPrice)
                .append("seriesName", seriesName)
                .toString();
    }

}
