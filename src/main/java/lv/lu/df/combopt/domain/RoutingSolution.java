package lv.lu.df.combopt.domain;

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.PlanningScore;
import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.lu.df.combopt.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@PlanningSolution
@Getter @Setter @NoArgsConstructor
public class RoutingSolution {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoutingSolution.class);

    private static final Integer HOUR = 3600;
    private static final Integer TIME8AM = 8 * HOUR;

    private String solutionId;
    @PlanningScore
    private HardSoftScore score;

    @PlanningEntityCollectionProperty
    private List<Vehicle> vehicleList = new ArrayList<>();

    @ProblemFactCollectionProperty
    @ValueRangeProvider
    @JsonIdentityReference(alwaysAsId = true)
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
                        + " pick=" + visit.getVolumePicked()
                                + " arrT=" + formatTime(visit.getArrivalTime())
                                + " depT=" + formatTime(visit.getDepartureTime())
                );
            });
        });
    }

    public static RoutingSolution generateData() {
        RoutingSolution problem = new RoutingSolution();
        problem.setSolutionId("P1");

        Vehicle v1 = new Vehicle();
        v1.setRegNr("AA0000");
        v1.setCapacity(10);
        v1.setTwStart(TIME8AM);
        v1.setTwFinish(TIME8AM + 8 * HOUR);
        v1.setSrvSTime(900);
        v1.setSrvFTime(900);
        v1.setMaxWorkTime(HOUR * 10);
        Location depotLoc = new Location(0.0, 0.0);
        v1.setDepot(depotLoc);
        v1.setCostDistance(0.1);
        v1.setCostWorkTime(7.0);
        v1.setCostUsage(30.0);

        Vehicle v2 = new Vehicle();
        v2.setRegNr("BB1111");
        v2.setCapacity(5);
        v2.setTwStart(TIME8AM);
        v2.setTwFinish(TIME8AM + 14 * HOUR);
        v2.setSrvSTime(900);
        v2.setSrvFTime(900);
        v2.setMaxWorkTime(HOUR * 10);
        v2.setDepot(depotLoc);
        v2.setCostDistance(0.1);
        v2.setCostWorkTime(7.0);
        v2.setCostUsage(25.0);


        Visit a1 = new Visit();
        a1.setName("Klients1");
        a1.setVolume(6);
        a1.setVisitType(Visit.VisitType.DELIVERY);
        Location a1Loc = new Location(0.0, 400.0);
        a1.setLocation(a1Loc);
        a1.setSrvTime(600);
        a1.setTwStart(TIME8AM);
        a1.setTwFinish(TIME8AM + 12 * HOUR);


        Visit a2 = new Visit();
        a2.setName("Klients2");
        a2.setVolume(5);
        a2.setVisitType(Visit.VisitType.DELIVERY);
        Location a2Loc = new Location(400.0, 400.0);
        a2.setLocation(a2Loc);
        a2.setSrvTime(600);
        a2.setTwStart(TIME8AM);
        a2.setTwFinish(TIME8AM + 12 * HOUR);

        Visit a3 = new Visit();
        a3.setName("Klients3");
        a3.setVolume(3);
        a3.setVisitType(Visit.VisitType.PICKUP);
        Location a3Loc = new Location(300.0, 100.0);
        a3.setLocation(a3Loc);
        a3.setSrvTime(600);
        a3.setTwStart(TIME8AM);
        a3.setTwFinish(TIME8AM + 12 * HOUR);

        Visit a4 = new Visit();
        a4.setName("Noliktava1");
        a4.setVolume(0);
        a4.setVisitType(Visit.VisitType.STOCK);
        Location stockLoc = new Location(400.0, 0.0);
        a4.setLocation(stockLoc);
        a4.setSrvTime(900);
        a4.setTwStart(TIME8AM - 2 * HOUR);
        a4.setTwFinish(TIME8AM + 16 * HOUR);

        Visit a5 = new Visit();
        a5.setName("Noliktava2");
        a5.setVolume(0);
        a5.setVisitType(Visit.VisitType.STOCK);
        a5.setLocation(stockLoc);
        a5.setSrvTime(900);
        a5.setTwStart(TIME8AM - 2 * HOUR);
        a5.setTwFinish(TIME8AM + 16 * HOUR);

        problem.getVehicleList().addAll(List.of(v1, v2));
        problem.getLocationList().addAll(List.of(depotLoc, a1Loc, a2Loc, a3Loc, stockLoc));
        problem.getVisitList().addAll(List.of(a1, a2, a3, a4, a5));

        return problem;
    }

    private static int problemId = 0;
    private static Integer getProblemId() { problemId++; return problemId;}


    public static RoutingSolution generateData(int scale) { // scale number of clients
        RoutingSolution problem = new RoutingSolution();
        problem.setSolutionId(RoutingSolution.getProblemId().toString());

        Random random = new Random();

        // vehicles: scale / 20 + 1
        for (int i = 1; i <= scale / 20 + 1; i++) {
            Vehicle v1 = new Vehicle();
            v1.setRegNr("AA" + i);
            v1.setCapacity(499 + random.nextInt(100) - 50);
            v1.setTwStart(TIME8AM);
            v1.setTwFinish(TIME8AM + 8 * HOUR);
            v1.setSrvSTime(900);
            v1.setSrvFTime(900);
            v1.setMaxWorkTime(HOUR * 8);
            Location depotLoc = new Location(random.nextDouble(100), random.nextDouble(100));
            v1.setDepot(depotLoc);
            v1.setCostDistance(0.1);
            v1.setCostWorkTime(7.0);
            v1.setCostUsage(30.0);

            problem.getVehicleList().add(v1);
            problem.getLocationList().add(depotLoc);
        }

        for (int i = 1; i <= scale; i++) {
            Visit a1 = new Visit();
            a1.setName("Client" + i);
            Location a1Loc = new Location(random.nextDouble(100), random.nextDouble(100));
            a1.setLocation(a1Loc);
            a1.setSrvTime(600);
            a1.setTwStart(TIME8AM + random.nextInt(HOUR * 2));
            a1.setTwFinish(TIME8AM + 8 * HOUR - random.nextInt(HOUR * 2));

            problem.getVisitList().add(a1);
            problem.getLocationList().add(a1Loc);

            if (i <= (0.1 * scale)) {
                // delivery
                a1.setVolume(100);
                a1.setVisitType(Visit.VisitType.DELIVERY);
            } else {
                // pickup
                a1.setVolume(1);
                a1.setVisitType(Visit.VisitType.PICKUP);
            }
        }

        for (int i = 1; i <= scale / 50 + 1; i++) {
            Location stockLoc = new Location(random.nextDouble(100), random.nextDouble(100));
            problem.getLocationList().add(stockLoc);
            for (int j = 1; j <= 10; j++) {
                Visit a5 = new Visit();
                a5.setLocation(stockLoc);
                a5.setName("Stock" + i + ":" + j);
                a5.setVolume(0);
                a5.setVisitType(Visit.VisitType.STOCK);
                a5.setLocation(stockLoc);
                a5.setSrvTime(900);
                a5.setTwStart(TIME8AM - 2 * HOUR);
                a5.setTwFinish(TIME8AM + 16 * HOUR);
                problem.getVisitList().add(a5);
            }
        }

        return problem;
    }

    public static String formatTime(Integer timeInSeconds) {
        if (timeInSeconds != null) {
            long HH = timeInSeconds / 3600;
            long MM = (timeInSeconds % 3600) / 60;
            long SS = timeInSeconds % 60;
            return String.format("%02d:%02d:%02d", HH, MM, SS);
        } else return "null";

    }
}
