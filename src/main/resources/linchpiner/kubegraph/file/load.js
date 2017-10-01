$(document).ready(function labsWorker() {
    $.ajax({
        type: "GET",
        url: "js/updates.js",
        success: function(data) {
            //console.log("Received: " + data);
        },
        complete: function() {
            setTimeout(labsWorker, 1000);
        }
    });
});
