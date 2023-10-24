package lv.lu.df.combopt.solver;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import lv.lu.df.combopt.domain.Vehicle;
import lv.lu.df.combopt.domain.Visit;

public class StreamCalculator implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                //everyVisit(constraintFactory),
                totalDistance(constraintFactory)
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
}
