package lv.lu.df.combopt;

import ai.timefold.solver.benchmark.api.PlannerBenchmark;
import ai.timefold.solver.benchmark.api.PlannerBenchmarkFactory;
import lv.lu.df.combopt.domain.RoutingSolution;
import lv.lu.df.combopt.domain.RoutingSolutionJsonIO;

import java.io.File;

public class BenchmarkerRunner {
    public static void main(String[] args) {
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory
                .createFromSolverConfigXmlResource("SolverConfig.xml");

        PlannerBenchmarkFactory benchmarkFactoryFromXML = PlannerBenchmarkFactory
                .createFromXmlResource("BenchmarkConfig.xml");

        //RoutingSolution problem = RoutingSolution.generateData();

        RoutingSolutionJsonIO routingSolutionJsonIO = new RoutingSolutionJsonIO();
        routingSolutionJsonIO.write(RoutingSolution.generateData(5),
                new File("data/classExample5.json"));

        PlannerBenchmark benchmark = benchmarkFactoryFromXML.buildPlannerBenchmark();

        benchmark.benchmarkAndShowReportInBrowser();

    }
}
