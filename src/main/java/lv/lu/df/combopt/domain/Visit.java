package lv.lu.df.combopt.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.variable.InverseRelationShadowVariable;
import ai.timefold.solver.core.api.domain.variable.NextElementShadowVariable;
import ai.timefold.solver.core.api.domain.variable.PreviousElementShadowVariable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@PlanningEntity
@Getter @Setter @NoArgsConstructor
public class Visit {

    public enum VisitType {DELIVERY, PICKUP, STOCK}

    private String name;
    private Integer volume;
    private VisitType visitType;

    private Location location;

    @InverseRelationShadowVariable(sourceVariableName = "visits")
    private Vehicle vehicle;

    @NextElementShadowVariable(sourceVariableName = "visits")
    private Visit next;

    @PreviousElementShadowVariable(sourceVariableName = "visits")
    private Visit prev;

    @Override
    public String toString() {
        return this.getName();
    }
}
