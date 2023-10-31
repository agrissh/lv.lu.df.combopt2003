package lv.lu.df.combopt.solver;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import lv.lu.df.combopt.domain.Vehicle;
import lv.lu.df.combopt.domain.Visit;

import static ai.timefold.solver.core.api.score.stream.Joiners.equal;

public class StreamCalculator implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                //everyVisit(constraintFactory),
                //totalDistance(constraintFactory),
                //isGoodsConstraintBroken(constraintFactory),
                visit2visit(constraintFactory),
                depot2visit(constraintFactory),
                visit2depot(constraintFactory),
                capacityOverflow(constraintFactory),
                notEnoughGoods(constraintFactory),
                pickedNotInStock(constraintFactory),
        };
    }

    public Constraint everyVisit(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Visit.class)
                .penalize(HardSoftScore.ONE_SOFT, visit -> 1)
                .asConstraint("everyVisit");
    }

    public Constraint totalDistance(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Vehicle.class)
                .filter(vehicle -> vehicle.getTotalDistance() > 0)
                .penalize(HardSoftScore.ONE_SOFT, vehicle -> (int) Math.round(vehicle.getTotalDistance() * 1000))
                .asConstraint("totalDistance");
    }

    public Constraint isGoodsConstraintBroken(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Vehicle.class)
                .filter(Vehicle::isGoodsConstraintBroken)
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("isGoodsConstraintBroken");
    }

    public Constraint visit2visit(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Visit.class)
                .filter(visit -> visit.getNext() != null)
                .penalize(HardSoftScore.ONE_SOFT, visit ->
                        (int) Math.round(visit.getLocation().distanceTo(visit.getNext().getLocation()) * 1000))
                .asConstraint("visit2visit");
    }

    public Constraint depot2visit(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Visit.class)
                .filter(visit -> visit.getPrev() == null)
                .join(Vehicle.class, equal(Visit::getVehicle, v -> v))
                .penalize(HardSoftScore.ONE_SOFT, (visit, vehicle) ->
                        (int) Math.round(vehicle.getDepot().distanceTo(visit.getLocation()) * 1000))
                .asConstraint("depot2visit");
    }

    public Constraint visit2depot(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Visit.class)
                .filter(visit -> visit.getNext() == null)
                .join(Vehicle.class, equal(Visit::getVehicle, v -> v))
                .penalize(HardSoftScore.ONE_SOFT, (visit, vehicle) ->
                        (int) Math.round(visit.getLocation().distanceTo(vehicle.getDepot()) * 1000))
                .asConstraint("visit2depot");
    }

    public Constraint capacityOverflow(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Visit.class)
                .join(Vehicle.class, equal(Visit::getVehicle, v->v))
                .filter((visit, vehicle) ->
                        visit.getVolumeUndelivered() + visit.getVolumePicked() > vehicle.getCapacity())
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("capacityOverflow");
    }

    public Constraint notEnoughGoods(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Visit.class)
                .filter(visit -> visit.getVolumeUndelivered() < 0)
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("notEnoughGoods");
    }

    public Constraint pickedNotInStock(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Visit.class)
                .filter(visit -> visit.getNext() == null && visit.getVolumePicked() > 0)
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("pickedNotInStock");
    }
}
