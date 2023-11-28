$(document).ready(function () {
    $.getJSON("/routes/list", function(routes) {
        var listofroutes = $("#listofroutes");
        $.each(routes, function(idx, value) {
              listofroutes.append($('<li><a href="route.html?id='+ value.solutionId + '">' +
               value.score +'</a> vehicles: ' + value.vehicleList.length + ', visits:' + value.visitList.length + '</li>'));
        });
    });
});