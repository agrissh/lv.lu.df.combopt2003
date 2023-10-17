package lv.lu.df.combopt.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class Visit {

    public enum VisitType {DELIVERY, PICKUP, STOCK}

    private String name;
    private Integer volume;
    private VisitType visitType;

    private Location location;
}
