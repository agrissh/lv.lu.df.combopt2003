package lv.lu.df.combopt.solver;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.calculator.EasyScoreCalculator;
import lv.lu.df.combopt.domain.Location;
import lv.lu.df.combopt.domain.RoutingSolution;
import lv.lu.df.combopt.domain.Vehicle;
import lv.lu.df.combopt.domain.Visit;

public class ScoreCalculator implements EasyScoreCalculator<RoutingSolution, HardSoftScore> {
    @Override
    public HardSoftScore calculateScore(RoutingSolution routingSolution) {
        int hard = 0, soft = 0;

        Double totalDistance = 0.0;
        for (Vehicle vehicle: routingSolution.getVehicleList()) {
            Location prevLoc = vehicle.getDepot();
            for (Visit visit: vehicle.getVisits()) {
                totalDistance = totalDistance +
                        prevLoc.distanceTo(visit.getLocation());
                prevLoc = visit.getLocation();
            }
            totalDistance = totalDistance +
                    prevLoc.distanceTo(vehicle.getDepot());

        }
        soft = (int) Math.round(totalDistance * 1000);

        return HardSoftScore.of(- hard, - soft);
    }
}
