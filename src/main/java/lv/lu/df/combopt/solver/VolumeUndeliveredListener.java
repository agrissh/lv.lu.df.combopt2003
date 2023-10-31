package lv.lu.df.combopt.solver;

import ai.timefold.solver.core.api.domain.variable.VariableListener;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.director.ScoreDirector;
import lv.lu.df.combopt.domain.RoutingSolution;
import lv.lu.df.combopt.domain.Visit;

public class VolumeUndeliveredListener implements VariableListener<RoutingSolution, Visit> {
    @Override
    public void beforeVariableChanged(ScoreDirector<RoutingSolution> scoreDirector, Visit visit) {

    }

    @Override
    public void afterVariableChanged(ScoreDirector<RoutingSolution> scoreDirector, Visit visit) {
        if (visit.getVehicle() == null) {
            scoreDirector.beforeVariableChanged(visit, "volumeUndelivered");
            visit.setVolumeUndelivered(0);
            scoreDirector.afterVariableChanged(visit, "volumeUndelivered");
            scoreDirector.beforeVariableChanged(visit, "volumePicked");
            visit.setVolumePicked(0);
            scoreDirector.afterVariableChanged(visit, "volumePicked");
        } else {
            Integer undelivered = visit.getPrev() != null ? visit.getPrev().getVolumeUndelivered() : 0;
            Integer picked = visit.getPrev() != null ? visit.getPrev().getVolumePicked() : 0;

            Visit shadowVisit = visit;
            while (shadowVisit != null) {
                switch (shadowVisit.getVisitType()) {
                    case DELIVERY ->undelivered = undelivered - shadowVisit.getVolume();
                    case PICKUP -> picked = picked + shadowVisit.getVolume();
                    case STOCK -> {undelivered = shadowVisit.getVehicle().getCapacity();picked = 0;}
                }
                //if (undelivered.equals(shadowVisit.getVolumeUndelivered()) &&
                //        picked.equals(shadowVisit.getVolumePicked())) break;
                scoreDirector.beforeVariableChanged(shadowVisit, "volumeUndelivered");
                shadowVisit.setVolumeUndelivered(undelivered);
                scoreDirector.afterVariableChanged(shadowVisit, "volumeUndelivered");
                scoreDirector.beforeVariableChanged(shadowVisit, "volumePicked");
                shadowVisit.setVolumePicked(picked);
                scoreDirector.afterVariableChanged(shadowVisit, "volumePicked");
                shadowVisit = shadowVisit.getNext();
            }
        }
    }

    @Override
    public void beforeEntityAdded(ScoreDirector<RoutingSolution> scoreDirector, Visit visit) {

    }

    @Override
    public void afterEntityAdded(ScoreDirector<RoutingSolution> scoreDirector, Visit visit) {

    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<RoutingSolution> scoreDirector, Visit visit) {

    }

    @Override
    public void afterEntityRemoved(ScoreDirector<RoutingSolution> scoreDirector, Visit visit) {

    }
}
