package lv.lu.df.combopt.solver;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import ai.timefold.solver.core.api.score.stream.Joiners;
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
                vehicleUsage(constraintFactory),
                worktimeCost(constraintFactory),
                visitOutsideTw(constraintFactory),
                worktimeOverflow(constraintFactory),
                vehicleOutsideTw(constraintFactory),
                returnOutsideTw(constraintFactory),
        };
    }

    public Constraint totalDistance(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Vehicle.class)
                .filter(vehicle -> vehicle.getTotalDistance() > 0)
                .penalize(HardSoftScore.ONE_SOFT, vehicle -> (int) Math.round(vehicle.getTotalDistance()
                        * vehicle.getCostDistance() * 100))
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
                .join(Vehicle.class, Joiners.equal(Visit::getVehicle, v->v))
                .penalize(HardSoftScore.ONE_SOFT, (visit, vehicle) ->
                        (int) Math.round(visit.getLocation().distanceTo(visit.getNext().getLocation())
                                * vehicle.getCostDistance() * 100))
                .asConstraint("visit2visit");
    }

    public Constraint depot2visit(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Visit.class)
                .filter(visit -> visit.getPrev() == null)
                .join(Vehicle.class, equal(Visit::getVehicle, v -> v))
                .penalize(HardSoftScore.ONE_SOFT, (visit, vehicle) ->
                        (int) Math.round(vehicle.getDepot().distanceTo(visit.getLocation()) * vehicle.getCostDistance() * 100))
                .asConstraint("depot2visit");
    }

    public Constraint visit2depot(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Visit.class)
                .filter(visit -> visit.getNext() == null)
                .join(Vehicle.class, equal(Visit::getVehicle, v -> v))
                .penalize(HardSoftScore.ONE_SOFT, (visit, vehicle) ->
                        (int) Math.round(visit.getLocation().distanceTo(vehicle.getDepot()) * vehicle.getCostDistance() * 100))
                .asConstraint("visit2depot");
    }

    public Constraint capacityOverflow(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Visit.class)
                .join(Vehicle.class, equal(Visit::getVehicle, v->v))
                .filter((visit, vehicle) ->
                        visit.getVolumeUndelivered() + visit.getVolumePicked() > vehicle.getCapacity())
                .penalize(HardSoftScore.ONE_HARD, (vi,ve) -> 100)
                .asConstraint("capacityOverflow");
    }

    public Constraint notEnoughGoods(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Visit.class)
                .filter(visit -> visit.getVolumeUndelivered() < 0)
                .penalize(HardSoftScore.ONE_HARD, v -> 100)
                .asConstraint("notEnoughGoods");
    }

    public Constraint pickedNotInStock(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Visit.class)
                .filter(visit -> visit.getNext() == null && visit.getVolumePicked() > 0)
                .penalize(HardSoftScore.ONE_HARD, v -> 10)
                .asConstraint("pickedNotInStock");
    }

    public Constraint vehicleUsage(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Vehicle.class)
                .filter(vehicle -> !vehicle.getVisits().isEmpty())
                .penalize(HardSoftScore.ONE_SOFT, vehicle -> (int) Math.round(vehicle.getCostUsage() * 100))
                .asConstraint("vehicleUsage");
    }

    public Constraint worktimeCost(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Vehicle.class)
                .filter(vehicle -> !vehicle.getVisits().isEmpty())
                .join(Visit.class, Joiners.equal(v->v, Visit::getVehicle))
                .filter((vehicle, last) -> last.getNext() == null)
                .penalize(HardSoftScore.ONE_SOFT, (vehicle, last) ->
                {
                    return (int) Math.round((last.getDepartureTime() + last.getLocation().timeTo(vehicle.getDepot()) + vehicle.getSrvFTime() -
                            vehicle.getTwStart()) / 3600.0 * vehicle.getCostWorkTime() * 100);
                })
                .asConstraint("worktimeCost");
    }

    public Constraint visitOutsideTw(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Visit.class)
                .filter(visit -> visit.getDepartureTime() != null && visit.getDepartureTime() > visit.getTwFinish())
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("visitOutsideTw");
    }

    public Constraint worktimeOverflow(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Vehicle.class)
                .filter(vehicle -> !vehicle.getVisits().isEmpty())
                .join(Visit.class, Joiners.equal(v->v, Visit::getVehicle))
                .filter((vehicle, last) -> last.getNext() == null)
                .filter(((vehicle, last) ->
                        last.getDepartureTime() + last.getLocation().timeTo(vehicle.getDepot()) + vehicle.getSrvFTime() -
                                vehicle.getTwStart() > vehicle.getMaxWorkTime()))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("worktimeOverflow");
    }

    public Constraint vehicleOutsideTw(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Visit.class)
                .join(Vehicle.class, equal(Visit::getVehicle, v->v))
                .filter((visit, vehicle) -> visit.getDepartureTime() > vehicle.getTwFinish())
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("vehicleOutsideTw");
    }

    public Constraint returnOutsideTw(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Vehicle.class)
                .filter(vehicle -> !vehicle.getVisits().isEmpty())
                .join(Visit.class, Joiners.equal(v->v, Visit::getVehicle))
                .filter((vehicle, last) -> last.getNext() == null)
                .filter(((vehicle, last) -> last.getDepartureTime() +
                        last.getLocation().timeTo(vehicle.getDepot()) +
                        vehicle.getSrvFTime() > vehicle.getTwFinish()))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("returnOutsideTw");
    }
}
