package lv.lu.df.combopt.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.variable.PlanningListVariable;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@PlanningEntity
@Getter @Setter @NoArgsConstructor
public class Vehicle {

    private String regNr;
    private Integer capacity;

    @PlanningListVariable
    private List<Visit> visits = new ArrayList<>();

    private Location depot;

    public Double getTotalDistance() {
        Double totalDistance = 0.0;
        Location prevLoc = this.getDepot();
        for (Visit visit: this.getVisits()) {
            totalDistance = totalDistance +
                    prevLoc.distanceTo(visit.getLocation());
            prevLoc = visit.getLocation();
        }
        totalDistance = totalDistance +
                prevLoc.distanceTo(this.getDepot());
        return totalDistance;
    }

    @Override
    public String toString() {
        return this.getRegNr();
    }
}
