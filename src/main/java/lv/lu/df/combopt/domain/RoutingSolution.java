package lv.lu.df.combopt.domain;

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.PlanningScore;
import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.lu.df.combopt.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@PlanningSolution
@Getter @Setter @NoArgsConstructor
public class RoutingSolution {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoutingSolution.class);

    private String solutionId;
    @PlanningScore
    private HardSoftScore score;

    @PlanningEntityCollectionProperty
    private List<Vehicle> vehicleList = new ArrayList<>();

    @ProblemFactCollectionProperty
    @ValueRangeProvider
    private List<Visit> visitList = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<Location> locationList = new ArrayList<>();

    public void print() {
        this.getVehicleList().forEach(vehicle -> {
            LOGGER.info(vehicle.getRegNr() + "("+ vehicle.getCapacity() +")");
            vehicle.getVisits().forEach(visit -> {
                LOGGER.info("     " + visit.getName() + " "
                        + visit.getVisitType() + " (" + visit.getVolume() + ")  " + visit.getVehicle().getRegNr()
                + " und=" + visit.getVolumeUndelivered()
                        + " pick=" + visit.getVolumePicked());
            });
        });
    }

    public static RoutingSolution generateData() {
        RoutingSolution problem = new RoutingSolution();
        problem.setSolutionId("P1");

        Vehicle v1 = new Vehicle();
        v1.setRegNr("AA0000");
        v1.setCapacity(5);

        Location depotLoc = new Location(0.0, 0.0);
        v1.setDepot(depotLoc);

        Vehicle v2 = new Vehicle();
        v2.setRegNr("BB1111");
        v2.setCapacity(5);
        v2.setDepot(depotLoc);

        Visit a1 = new Visit();
        a1.setName("Klients1");
        a1.setVolume(6);
        a1.setVisitType(Visit.VisitType.DELIVERY);
        Location a1Loc = new Location(0.0, 4.0);
        a1.setLocation(a1Loc);

        Visit a2 = new Visit();
        a2.setName("Klients2");
        a2.setVolume(5);
        a2.setVisitType(Visit.VisitType.DELIVERY);
        Location a2Loc = new Location(4.0, 4.0);
        a2.setLocation(a2Loc);

        Visit a3 = new Visit();
        a3.setName("Klients3");
        a3.setVolume(3);
        a3.setVisitType(Visit.VisitType.PICKUP);
        Location a3Loc = new Location(3.0, 1.0);
        a3.setLocation(a3Loc);

        Visit a4 = new Visit();
        a4.setName("Noliktava1");
        a4.setVolume(0);
        a4.setVisitType(Visit.VisitType.STOCK);
        Location stockLoc = new Location(4.0, 0.0);
        a4.setLocation(stockLoc);

        Visit a5 = new Visit();
        a5.setName("Noliktava2");
        a5.setVolume(0);
        a5.setVisitType(Visit.VisitType.STOCK);
        a5.setLocation(stockLoc);


        problem.getVehicleList().addAll(List.of(v1, v2));
        problem.getLocationList().addAll(List.of(depotLoc, a1Loc, a2Loc, a3Loc, stockLoc));
        problem.getVisitList().addAll(List.of(a1, a2, a3, a4, a5));

        return problem;
    }
}
