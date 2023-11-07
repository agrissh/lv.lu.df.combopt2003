package lv.lu.df.combopt;

import ai.timefold.solver.test.api.score.stream.ConstraintVerifier;
import lv.lu.df.combopt.domain.Location;
import lv.lu.df.combopt.domain.RoutingSolution;
import lv.lu.df.combopt.domain.Vehicle;
import lv.lu.df.combopt.domain.Visit;
import lv.lu.df.combopt.solver.StreamCalculator;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ConstraintTest {
    Vehicle VEHICLE = new Vehicle();
    Visit VISIT1 = new Visit();
    Visit VISIT2 = new Visit();
    Location DEPOT = new Location(0.0,0.0);
    Location locVisit1 = new Location(4.0, 0.0);
    Location locVisit2 = new Location(4.0, 4.0);
    public ConstraintTest() {
        VISIT1.setVehicle(VEHICLE);
        VISIT1.setLocation(locVisit1);
        VISIT2.setVehicle(VEHICLE);
        VISIT2.setLocation(locVisit2);

        VISIT1.setNext(VISIT2);
        VISIT2.setPrev(VISIT1);
        VEHICLE.getVisits().addAll(List.of(VISIT1, VISIT2));
        VEHICLE.setDepot(DEPOT);
        VEHICLE.setCostDistance(1.0);
    }
    ConstraintVerifier<StreamCalculator, RoutingSolution> constraintVerifier = ConstraintVerifier.build(
            new StreamCalculator(), RoutingSolution.class, Vehicle.class, Visit.class);
    @Test
    void distanceTest1() {
        constraintVerifier.verifyThat(StreamCalculator::depot2visit)
                .given(VEHICLE, VISIT1)
                .penalizesBy(400);
    }
    @Test
    void distanceTest2() {
        constraintVerifier.verifyThat(StreamCalculator::visit2visit)
                .given(VISIT2, VISIT1, VEHICLE)
                .penalizesBy(400);
    }
}
