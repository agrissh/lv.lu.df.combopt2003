package lv.lu.df.combopt.ortools;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import com.google.ortools.sat.*;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ORToolsExample {

    private static final Logger LOGGER = LoggerFactory.getLogger(ORToolsExample.class);

    static class BoardPrinter extends CpSolverSolutionCallback {
        @Getter
        private int solutionCount;
        private final IntVar[] queens;
        public BoardPrinter(IntVar[] queensIn) {
            solutionCount = 0;
            queens = queensIn;
        }

        @Override
        public void onSolutionCallback() {
            LOGGER.info("Solution " + solutionCount);
            for (int i = 0; i < queens.length; ++i) {
                for (int j = 0; j < queens.length; ++j) {
                    if (value(queens[j]) == i) {
                        System.out.print("Q");
                    } else {
                        System.out.print("_");
                    }
                    if (j != queens.length - 1) {
                        System.out.print(" ");
                    }
                }
                System.out.println();
            }
            solutionCount++;
        }
    }
    public static void main(String[] args) {
        Loader.loadNativeLibraries();
        //LinearProgramming();
        //ConstraintProgrammingSimple();
        ConstraintProgrammingNQueens();
    }

    private static void LinearProgramming() {
        MPSolver solver = MPSolver.createSolver("GLOP");
        MPVariable x1 = solver.makeNumVar(0.0, MPSolver.infinity(), "x1");
        MPVariable x2 = solver.makeNumVar(0.0, MPSolver.infinity(), "x2");
        MPVariable x3 = solver.makeNumVar(0.0, MPSolver.infinity(), "x3");
        LOGGER.info("Number of variables = " + solver.numVariables());
        MPConstraint p = solver.makeConstraint(6.0, MPSolver.infinity(), "p");
        p.setCoefficient(x1, 3);
        p.setCoefficient(x2, 1);
        p.setCoefficient(x3, 0.5);
        MPConstraint c = solver.makeConstraint(15.0, MPSolver.infinity(), "c");
        c.setCoefficient(x1, 3);
        c.setCoefficient(x2, 2);
        c.setCoefficient(x3, 4);
        MPConstraint f = solver.makeConstraint(5.0, MPSolver.infinity(), "f");
        f.setCoefficient(x1, 2);
        f.setCoefficient(x3, 1);
        MPConstraint v = solver.makeConstraint(7.0, MPSolver.infinity(), "v");
        v.setCoefficient(x1, 1);
        v.setCoefficient(x2, 4);
        LOGGER.info("Number of constraints = " + solver.numConstraints());
        MPObjective objective = solver.objective();
        objective.setCoefficient(x1, 5);
        objective.setCoefficient(x2, 1);
        objective.setCoefficient(x3, 2);
        objective.setMinimization();
        solver.solve();
        LOGGER.info("Solution:");
        LOGGER.info("Objective value = " + objective.value());
        LOGGER.info("x1 = " + x1.solutionValue());
        LOGGER.info("x2 = " + x2.solutionValue());
        LOGGER.info("x3 = " + x3.solutionValue());

    }

    private static void ConstraintProgrammingSimple() {
        CpModel model = new CpModel();
        // 0 <= x <= 100
        IntVar x = model.newIntVar(0, 100, "x");
        // 0 <= y <= 100
        IntVar y = model.newIntVar(0, 100, "y");

        // x + y <= 30
        model.addLessOrEqual(LinearExpr.newBuilder().add(x).add(y), 30);

        // maximize 30 * x + 50 * y
        model.maximize(LinearExpr.weightedSum(new IntVar[] {x, y}, new long[] {30, 50}));

        CpSolver cpSolver = new CpSolver();
        CpSolverStatus status = cpSolver.solve(model);

        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            LOGGER.info("x = " + cpSolver.value(x));
            LOGGER.info("y = " + cpSolver.value(y));
        } else {
            LOGGER.info("No solution found.");
        }
    }

    private static void ConstraintProgrammingNQueens() {
        CpModel model = new CpModel();

        int boardSize = 15;
        IntVar[] queens = new IntVar[boardSize];

        for (int i=0; i < boardSize; ++i) {
            queens[i] = model.newIntVar(0, boardSize - 1, "q" + i );
        }

        LinearExpr[] diag1 = new LinearExpr[boardSize];
        LinearExpr[] diag2 = new LinearExpr[boardSize];
        for (int i=0; i < boardSize; ++i) {
            diag1[i] = LinearExpr.newBuilder().add(queens[i]).add(i).build();
            diag2[i] = LinearExpr.newBuilder().add(queens[i]).add(-i).build();
        }

        model.addAllDifferent(diag1);
        model.addAllDifferent(diag2);

        model.addAllDifferent(queens);

        CpSolver solver = new CpSolver();
        solver.getParameters().setEnumerateAllSolutions(true);

        BoardPrinter bp = new BoardPrinter(queens);

        solver.solve(model, bp);

        // Statistics.
        LOGGER.info("Statistics");
        LOGGER.info("  conflicts : " + solver.numConflicts());
        LOGGER.info("  branches  : " + solver.numBranches());
        LOGGER.info("  wall time : " + solver.wallTime() + " s");
        LOGGER.info("  solutions : " + bp.getSolutionCount());
    }
}
