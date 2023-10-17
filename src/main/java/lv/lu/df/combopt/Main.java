package lv.lu.df.combopt;


import ai.timefold.solver.core.api.solver.Solver;
import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.config.solver.SolverConfig;
import ai.timefold.solver.core.config.solver.termination.TerminationConfig;
import lv.lu.df.combopt.domain.Location;
import lv.lu.df.combopt.domain.RoutingSolution;
import lv.lu.df.combopt.domain.Vehicle;
import lv.lu.df.combopt.domain.Visit;
import lv.lu.df.combopt.solver.ScoreCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {

        System.out.println("Hello world!");
        LOGGER.info("Hello world from Logger");
        LOGGER.debug("Hello world from Looger 2");
        RoutingSolution problem = RoutingSolution.generateData();
        problem.print();

        SolverFactory<RoutingSolution> solverFactory = SolverFactory.create(
                new SolverConfig()
                        .withSolutionClass(RoutingSolution.class)
                        .withEntityClasses(Vehicle.class)
                        .withEasyScoreCalculatorClass(ScoreCalculator.class)
                        .withTerminationConfig(new TerminationConfig()
                                .withSecondsSpentLimit(10L))
        );

        Solver<RoutingSolution> solver = solverFactory.buildSolver();
        RoutingSolution solution = solver.solve(problem);

        solution.print();

    }


}