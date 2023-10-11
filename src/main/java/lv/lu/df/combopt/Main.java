package lv.lu.df.combopt;


import lv.lu.df.combopt.domain.RoutingSolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {

        System.out.println("Hello world!");
        LOGGER.info("Hello world from Logger");
        LOGGER.debug("Hello world from Looger 2");

        RoutingSolution problem = new RoutingSolution();
        problem.setSolutionId("P1");
    }
}