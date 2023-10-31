package lv.lu.df.combopt.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.variable.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.lu.df.combopt.solver.VolumeUndeliveredListener;

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

    @Override
    public String toString() {
        return this.getName() + " und=" + this.getVolumeUndelivered();
    }
}
