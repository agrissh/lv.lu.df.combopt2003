package lv.lu.df.combopt.solver;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.constraint.ConstraintMatch;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SimpleConstraintMatch {
    private String constraintName;
    private HardSoftScore score;

    public SimpleConstraintMatch(ConstraintMatch<HardSoftScore> constraintMatch) {
        this.constraintName = constraintMatch.getConstraintRef().constraintName();
        this.score = constraintMatch.getScore();
    }
}
