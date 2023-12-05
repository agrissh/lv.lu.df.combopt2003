package lv.lu.df.combopt.domain;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.ResponsePath;
import com.graphhopper.config.CHProfile;
import com.graphhopper.config.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Router {
    static public Router getDefaultRouterInstance() {
        return new Router("data/latvia.osm.pbf", ".ghtemp");
    }

    public GraphHopper router;

    public Router(String osmFile, String ghLocation) {
        router = new GraphHopper();
        router.setOSMFile(osmFile);
        router.setGraphHopperLocation(ghLocation);
        router.setProfiles(new Profile("car").setVehicle("car").setWeighting("shortest").setTurnCosts(false));
        router.getCHPreparationHandler().setCHProfiles(new CHProfile("car"));
        router.importOrLoad();
    }

    public void setDistanceTimeMap(List<Location> locationList) {
        for (Location location: locationList) {
            for (Location toLocation: locationList) {
                GHRequest req = new GHRequest(location.getLat(), location.getLon(), toLocation.getLat(), toLocation.getLon()).
                                setProfile("car").
                                setLocale(Locale.US);
                GHResponse rsp = router.route(req);
                if (rsp.hasErrors())
                    throw new RuntimeException(rsp.getErrors().toString());
                ResponsePath path = rsp.getBest();

                // distance in meters and time in millis in the response path
                location.getDistanceMap().put(toLocation, path.getDistance() / 1000);
                location.getTimeMap().put(toLocation,Math.toIntExact(path.getTime() / 1000));
            }
        }
    }
}