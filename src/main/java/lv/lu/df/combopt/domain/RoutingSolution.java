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

import java.util.ArrayList;
import java.util.List;

@PlanningSolution
@Getter @Setter @NoArgsConstructor
public class RoutingSolution {
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
}
