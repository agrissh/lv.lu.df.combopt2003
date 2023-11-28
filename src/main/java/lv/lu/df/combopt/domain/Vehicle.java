package lv.lu.df.combopt.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.variable.PlanningListVariable;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@PlanningEntity
@Getter @Setter @NoArgsConstructor
@JsonIdentityInfo(scope = Vehicle.class,
        property = "regNr",
        generator = ObjectIdGenerators.PropertyGenerator.class)
public class Vehicle {

    private String regNr;
    private Integer capacity;

    @PlanningListVariable
    private List<Visit> visits = new ArrayList<>();

    private Location depot;

    private Integer twStart;
    private Integer twFinish;

    private Integer srvSTime;
    private Integer srvFTime;

    private Integer maxWorkTime;

    private Double costWorkTime;
    private Double costDistance;
    private Double costUsage;

    @JsonIgnore
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

    @JsonIgnore
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
