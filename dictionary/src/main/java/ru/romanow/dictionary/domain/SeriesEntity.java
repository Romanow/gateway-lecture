package ru.romanow.dictionary.domain;

import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import ru.romanow.dictionary.domain.enums.SeriesComplexity;
import ru.romanow.dictionary.domain.enums.SeriesType;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "series")
public class SeriesEntity
        implements Identity<String> {

    @Id
    @Column(name = "name", length = 80, nullable = false, updatable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private SeriesType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "complexity", nullable = false)
    private SeriesComplexity complexity;

    @Column(name = "age")
    private Integer age;

    @OneToMany(mappedBy = "series")
    private Set<LegoSetEntity> sets;

    @Override
    public String getId() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final SeriesEntity that = (SeriesEntity) o;

        return new EqualsBuilder().append(name, that.name).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(name).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("type", type)
                .append("complexity", complexity)
                .append("age", age)
                .toString();
    }

}
