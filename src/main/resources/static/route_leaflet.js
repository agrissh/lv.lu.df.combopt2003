var map = L.map('map').setView([56.9947, 24.0309], 11);
var color_idx = 0;
const colors = ["#f44336","#e81e63","#9c27b0","#673ab7","#3f51b5","#2196f3","#03a9f4","#00bcd4","#009688",
                                                "#4caf50","#8bc34a","#cddc39","#ffeb3b","#ffc107","#ff9800","#ff5722"];
                       ;
const defaultIcon = new L.Icon.Default();
const vehicleIcon = L.divIcon({
    html: '<i class="fas fa-truck"></i>'
});
const pickupIcon = L.divIcon({
    html: '<i class="fas fa-building"></i>'
});
const deliveryIcon = L.divIcon({
    html: '<i class="far fa-building"></i>'
});
const stockIcon = L.divIcon({
    html: '<i class="fas fa-warehouse"></i>'
});
const pickupIcon_red = L.divIcon({
    html: '<i class="fas fa-building" style="color: #ff0000"></i>'
});
const deliveryIcon_red = L.divIcon({
    html: '<i class="far fa-building" style="color: #ff0000"></i>'
});
const stockIcon_red = L.divIcon({
    html: '<i class="fas fa-warehouse" style="color: #ff0000"></i>'
});

$(document).ready(function () {
    const urlParams = new URLSearchParams(window.location.search);
    const solutionId = urlParams.get('id');

    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }).addTo(map);

    $.getJSON("/routes/score?id=" + solutionId, function(analysis) {
                    var badge = "badge bg-danger";
                    if (getHardScore(analysis.score)==0) { badge = "badge bg-success"; }
                    $("#score_a").attr({"title":"Score Brakedown","data-bs-content":"" + getScorePopoverContent(analysis.constraints) + "","data-bs-html":"true"});
                    $("#score_text").text(analysis.score);
                    $("#score_text").attr({"class":badge});

                    $(function () {
                       $('[data-toggle="popover"]').popover()
                    })
    });

    $.getJSON("/routes/solution?id=" + solutionId, function(solution) {
            $.getJSON("/routes/indictments?id=" + solutionId, function(indictments) {
                            renderRoutes(solution, indictments);
                            $(function () {
                              $('[data-toggle="popover"]').popover()
                            })
            })
        });
});

function renderRoutes(solution, indictments) {
    $("#solutionTitle").text("Version 04/Dec/2023 solutionId: " + solution.solutionId);

    var indictmentMap = {};
    indictments.forEach((indictment) => {
        indictmentMap[indictment.indictedObjectID] = indictment;
    })

    solution.vehicleList.forEach((vehicle) => {
        let previous_location = [vehicle.depot.lat, vehicle.depot.lon];
        let nr = 1;
        const vcolor = getColor();
        const vmarker = L.marker(previous_location).addTo(map);
        vmarker.setIcon(vehicleIcon);
        vehicle.visits.forEach((visit) => {
            const location = [visit.location.lat, visit.location.lon];
            const marker = L.marker(location).addTo(map);
            marker.setIcon(getVisitIcon(visit.visitType, indictmentMap[visit.name]));
            marker.bindPopup("<b>#"+nr+"</b><br>id="+visit.name+"<br>"+visit.visitType+"<br>"+visit.volume + "<br>arrival="
            + formatTime(visit.arrivalTime) + "<br>undeliverd=" + visit.volumeUndelivered +                                                                                                   '<br>picked=' + visit.volumePicked +
            "<hr>" + getEntityPopoverContent(visit.name, indictmentMap));
            const line = L.polyline([previous_location, location], {color: vcolor}).addTo(map);
            previous_location = location;
            nr = nr + 1;
        });
        const line_back = L.polyline([previous_location, [vehicle.depot.lat, vehicle.depot.lon]],{color: vcolor}).addTo(map);
    });
}

function getEntityPopoverContent(entityId, indictmentMap) {
    var popover_content = "";
    const indictment = indictmentMap[entityId];
    if (indictment != null) {
        popover_content = popover_content + "Total score: <b>" + indictment.score + "</b> (" + indictment.matchCount + ")<br>";
        indictment.constraintMatches.forEach((match) => {
                  if (getHardScore(match.score) == 0) {
                     popover_content = popover_content + match.constraintName + " : " + match.score + "<br>";
                  } else {
                     popover_content = popover_content + "<b>" + match.constraintName + " : " + match.score + "</b><br>";
                  }
            })
    }
    return popover_content;
}

function getVisitIcon(v_type, indictment) {
    if (indictment==undefined || getHardScore(indictment.score) == 0) {
        return v_type == "STOCK" ? stockIcon : v_type == "PICKUP" ? pickupIcon : deliveryIcon;
    } else {
        return v_type == "STOCK" ? stockIcon_red : v_type == "PICKUP" ? pickupIcon_red : deliveryIcon_red;
    }

}

function getColor() {
   color_idx = (color_idx + 1) % colors.length;
   return colors[color_idx];
}

function getScorePopoverContent(constraint_list) {
    var popover_content = "";
    constraint_list.forEach((constraint) => {
          if (getHardScore(constraint.score) == 0) {
             popover_content = popover_content + constraint.name + " : " + constraint.score + "<br>";
          } else {
             popover_content = popover_content + "<b>" + constraint.name + " : " + constraint.score + "</b><br>";
          }
    })
    return popover_content;
}

function getHardScore(score) {
   return score.slice(0,score.indexOf("hard"))
}

function getSoftScore(score) {
   return score.slice(score.indexOf("hard/"),score.indexOf("soft"))
}

function formatTime(timeInSeconds) {
        if (timeInSeconds != null) {
            const HH = Math.floor(timeInSeconds / 3600);
            const MM = Math.floor((timeInSeconds % 3600) / 60);
            const SS = Math.floor(timeInSeconds % 60);
            return HH + ":" + MM + ":" + SS;
        } else return "null";
}