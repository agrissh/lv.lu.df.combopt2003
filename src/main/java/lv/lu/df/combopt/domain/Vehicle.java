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

    public Boolean isGoodsConstraintBroken() {
        Integer undelivered = 0, picked = 0;
        for (Visit visit : this.getVisits()) {
            switch (visit.getVisitType()) {
                case DELIVERY -> {
                    undelivered = undelivered - visit.getVolume();
                }
                case PICKUP -> {
                    picked = picked + visit.getVolume();
                }
                case STOCK -> {
                    undelivered = this.getCapacity();
                    picked = 0;
                }
                default -> throw new IllegalStateException("Unexpected value: " + visit.getVisitType());
            }
            if (undelivered + picked > this.getCapacity() || undelivered < 0) return true;
        }
        return (picked > 0);
    }

    @Override
    public String toString() {
        return this.getRegNr();
    }
}
