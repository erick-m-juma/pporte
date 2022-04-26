$(window).on("load", function(e) {
    $("#global-loader").fadeOut("slow");
})
function fnCreateOffers(){
		    $('#create_new_offer').attr('action', 'ws');
		    $('input[name="qs"]').val('opsmngeoffers');
		    $('input[name="rules"]').val('Create Offers');
			$("#create_new_offer").submit();
	  }