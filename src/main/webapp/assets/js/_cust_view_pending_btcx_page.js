$(window).on("load", function(e) {
    $("#global-loader").fadeOut("slow");
})

$(document).ready(function () {
	$('#pendingtxntable').dataTable( {
			    "order": []
			} );
});