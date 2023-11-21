package lv.lu.df.combopt.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.variable.*;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.lu.df.combopt.solver.VolumeUndeliveredListener;

@PlanningEntity
@Getter @Setter @NoArgsConstructor
@JsonIdentityInfo(scope = Visit.class,
        property = "name",
        generator = ObjectIdGenerators.PropertyGenerator.class)
public class Visit {

    public enum VisitType {DELIVERY, PICKUP, STOCK}

    private String name;
    private Integer volume;
    private VisitType visitType;

    private Location location;

    @InverseRelationShadowVariable(sourceVariableName = "visits")
    @JsonIdentityReference
    private Vehicle vehicle;

    @NextElementShadowVariable(sourceVariableName = "visits")
    @JsonIdentityReference
    private Visit next;

    @PreviousElementShadowVariable(sourceVariableName = "visits")
    @JsonIdentityReference
    private Visit prev;

    @JsonIgnore
    public Integer getUndelivered() {
        Integer undelivered = 0;
        if (this.getPrev() != null) {
            undelivered = this.getPrev().getUndelivered();
        }
        if (this.getVehicle() == null) return 0;

        switch (this.getVisitType()) {
            case DELIVERY -> {
                undelivered = undelivered - this.getVolume();
            }
            case PICKUP -> {
                // nothing
            }
            case STOCK -> {
                undelivered = this.getVehicle().getCapacity();
            }
        }
        return undelivered;
    }
    @JsonIgnore
    public Integer getPicked() {
        Integer picked = 0;
        if (this.getPrev() != null) {
            picked = this.getPrev().getPicked();
        }
        if (this.getVehicle() == null) return 0;

        switch (this.getVisitType()) {
            case DELIVERY -> {
                // nothing
            }
            case PICKUP -> {
                picked = picked + this.getVolume();
            }
            case STOCK -> {
                picked = 0;
            }
        }
        return picked;
    }

    @ShadowVariable(variableListenerClass = VolumeUndeliveredListener.class,
    sourceVariableName = "vehicle")
    @ShadowVariable(variableListenerClass = VolumeUndeliveredListener.class,
            sourceVariableName = "prev")
    private Integer volumeUndelivered = 0;

    @PiggybackShadowVariable(shadowVariableName = "volumeUndelivered")
    private Integer volumePicked = 0;

    private Integer srvTime;
    private Integer twStart;
    private Integer twFinish;

    @PiggybackShadowVariable(shadowVariableName = "volumeUndelivered")
    private Integer arrivalTime = null;

    @JsonIgnore
    public Integer getDepartureTime() {
        return this.getArrivalTime() != null ?
                Math.max(this.getArrivalTime(), this.getTwStart()) + this.getSrvTime() :
                null;
    }

    @Override
    public String toString() {
        return this.getName() + " arrT=" + RoutingSolution.formatTime(this.getArrivalTime())
                + " depT=" + RoutingSolution.formatTime(this.getDepartureTime());
    }
}
