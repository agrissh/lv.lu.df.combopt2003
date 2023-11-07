package lv.lu.df.combopt.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Location {
    private Double lat;
    private Double lon;

    private static Integer SPEED = 200; // km/h

    public Double distanceTo(Location location) {
        return Math.sqrt(Math.pow(this.lat - location.lat, 2)
                + Math.pow(this.lon - location.lon,2));
    }

    public Integer timeTo(Location location) {
        return (int) Math.round((this.distanceTo(location) / SPEED) * 3600);
    }
}
