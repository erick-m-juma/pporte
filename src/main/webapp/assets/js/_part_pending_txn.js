$("button[data-dismiss=modal]").click(function(){
	$(".modal").modal('hide');
});

$(window).on("load", function(e) {
	$("#global-loader").fadeOut("slow");
})

function fnViewMoreRow(txnCode, systemRef, txnUserCode, relationshipNo, payMode, sourceAssetCode, sourceAmount, destinationAssetCode, destinationAmount, publicKey,
    txnHash, senderComment, partnerComment, receiverName, receiverBankName,receiverBankCode, receiverAccountNo, receiverEmail, status   ){
	$('#largeModal').on('show.bs.modal', function () {
        $("#sp_txn_ref").text(systemRef);
        $("#sp_source_amount").text(sourceAmount +" "+sourceAssetCode);
        $("#sp_txn_hash").text(txnHash);
        $("#sp_des_amount").text(destinationAmount +" "+destinationAssetCode);
        $("#sp_bank_name").text(receiverBankName);
        $("#sp_receiver_name").text(receiverName);
        $("#sp_bank_code").text(receiverBankCode);
        $("#sp_receiver_email").text(receiverEmail);
        $("#sp_bank_acount_no").text(receiverAccountNo);
		$('#select_status').val(status).prop('selected', true); 
	});
	$('#largeModal').modal('show');
}
function fnSubmitBtn(){
	$( "#txn-status-form" ).validate( {
		rules: {
			select_status: {
				required: true
			}
		},
		messages: {
			select_status: {
				required: 'Please select status'
			}
		},
		errorElement: "em",
		errorPlacement: function ( error, element ) {
			// Add the `invalid-feedback` class to the error element
			error.addClass( "invalid-feedback" );
			if ( element.prop( "type" ) === "checkbox" ) {
				error.insertAfter( element.next( "label" ) );
			} else {
				error.insertAfter( element );
			}
		},
		highlight: function ( element, errorClass, validClass ) {
			$( element ).addClass( "is-invalid" ).removeClass( "is-valid" );
		},
		unhighlight: function (element, errorClass, validClass) {
			$( element ).addClass( "is-valid" ).removeClass( "is-invalid" );
		}
	});
	if($( "#txn-status-form" ).valid()){
		
		
		var form = $('#txn-status-form')[0];
		var formData = new FormData(form);
        formData.append("qs", 'partdash');
        formData.append("rules", 'partner_change_txn_status');
        formData.append("system_ref", $("#sp_txn_ref").text());
		formData.append("sp_source_amount", $("#sp_source_amount").text());
		formData.append("sp_txn_hash", $("#sp_txn_hash").text());
		formData.append("sp_des_amount", $("#sp_des_amount").text());
		formData.append("sp_bank_name", $("#sp_bank_name").text());
		formData.append("sp_bank_acount_no", $("#sp_bank_acount_no").text());
		formData.append("sp_receiver_email", $("#sp_receiver_email").text());
		formData.append("sp_bank_code", $("#sp_bank_code").text());
		formData.append("sp_receiver_name", $("#sp_receiver_name").text());
		/*for (var pair of formData.entries()) {
			alert(pair[0] + " - " + pair[1]);
		}*/	
		$.ajaxSetup({
			beforeSend: function(xhr) {
				xhr.setRequestHeader('x-api-key' , getAPIKey());
			}
		});
		$.ajax({
			url: 'ms',
			data: formData,
			processData: false,
			contentType: false,
			type: 'POST',
			success: function (result) {
				if (result) {
					var data = JSON.parse(result);
					if(data.error=='false'){
						Swal.fire({
									icon: 'success',
									text: data.message,
									showConfirmButton: true,
									confirmButtonText: "Ok",
								}).then(function() {
									$('#get-page-form').attr('action', 'ws');
									$('input[name="qs"]').val('partdash');
									$('input[name="rules"]').val('Completed Transactions');
									$("#get-page-form").submit();
								});	
						}else{
							Swal.fire({
							text: data.message, 
							icon: "error",
							showConfirmButton: true,
							confirmButtonText: "Ok",
							}).then(function() {
								
							});	
						} 
				}
				},
				error: function() {
					Swal.fire({
						icon: 'error',
						title: 'Oops',
						text: 'Problem with connection',
						showConfirmButton: true,
						confirmButtonText: "Ok",
						}).then(function() {
							
				});
			}
		}); 
	}
}


