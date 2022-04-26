$(window).on("load", function(e) {
    $("#global-loader").fadeOut("slow");
})
$(document).ready(function() {
 // executes when HTML-Document is loaded and DOM is ready
 //alert("document is ready");
});
		$('#btn_sell_offer').click(function() {
			$("#buy_native_form").validate({
				rules: {
					sellingamount: {
						required: true
					},
					priceunit: {
						required: true,
					},
					selaccount: {
						required: true,
					},
					buyprivatekey: {
						required: true,
					}
										
				},
				messages: {
					sellingamount: {
						required: 'Please enter the amount',

					},
					priceunit: {
						required: 'Please enter the price unit',
					},
					selaccount: {
						required: 'Please select asset code',
					},
					buyprivatekey: {
						required: 'Please business private key',
					}
					
				},
				errorElement: "em",
				errorPlacement: function(error, element) {
					// Add the `invalid-feedback` class to the error element
					error.addClass("invalid-feedback");
					if (element.prop("type") === "checkbox") {
						error.insertAfter(element.next("label"));
					} else {
						error.insertAfter(element);
					}
				},
				highlight: function(element, errorClass, validClass) {
					$(element).addClass("is-invalid").removeClass("is-valid");
				},
				unhighlight: function(element, errorClass, validClass) {
					$(element).addClass("is-valid").removeClass("is-invalid");
				}
			});
		
			if ($("#buy_native_form").valid()) {
				$('#buy_native_form').attr('action', 'ws');
				$('input[name="qs"]').val('opscrypto');
				$('input[name="rules"]').val('opscreateselloffer');
				$('input[name="hdnusertype"]').val($('#selleditusertype :selected').val());
				$('input[name="hdnselaccount"]').val($('#selaccount :selected').val());
				
				//$("#editmerchuser-form").submit();
				    $('#btn_sell_offer').addClass('btn-loading');
				var formData = new FormData($('#buy_native_form')[0]);
				$.ajaxSetup({
					beforeSend: function(xhr) {
						xhr.setRequestHeader('x-api-key', getAPIKey());
					}
				});
			 
				$.ajax({
					url: 'ms',
					data: formData,
					processData: false,
					contentType: false,
					type: 'POST',
					success: function(result) {
					 $('#btn_sell_offer').removeClass('btn-loading');

						var data = JSON.parse(result);
						if (data.error == 'false') {
							//alert('lgtoken is '+data.token)
							Swal.fire({
								icon: 'success',
								title: 'Good',
								text: data.message,
								showConfirmButton: true,
								confirmButtonText: "Ok",
							}).then(function() {
								//location.reload();
								$('#buy_native_form').attr('action', 'ws');
								$('#buy_native_form input[name="qs"]').val('opscrypto');
								$('#buy_native_form input[name="rules"]').val('View Offers');
								$("#buy_native_form").submit();
							});
		
						} else {
							Swal.fire({
								icon: 'error',
								title: 'Oops',
								text: data.message,
								showConfirmButton: true,
								confirmButtonText: "Ok",
							}).then(function() {
								//Do Nothing
							});
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
							//Do Nothing
						});
					}
				});
		
			} else {
				Swal.fire({
					icon: 'error',
					title: 'Oops..',
					text: 'Please check your data'
					//footer: '<a href>Why do I have this issue?</a>'
				})
		
				return false;
			}
		});
		$('#btn_buy_offer').click(function() {
			$("#sel_porte_form").validate({
				rules: {
					sellbuyamount: {
						required: true
					},
					sellpriceunit: {
						required: true,
					},
					selectaccount:{
						required:true,
					}
					,
					sellprivatekey:{
						required:true,
					}
				},
				messages: {
					sellbuyamount: {
						required: 'Please enter the amount',

					},
					sellpriceunit: {
						required: 'Please enter the price unit',
					},
					selectaccount: {
						required: 'Please select asset code',
					},
					sellprivatekey: {
						required: 'Please business private key',
					}
				},
				errorElement: "em",
				errorPlacement: function(error, element) {
					// Add the `invalid-feedback` class to the error element
					error.addClass("invalid-feedback");
					if (element.prop("type") === "checkbox") {
						error.insertAfter(element.next("label"));
					} else {
						error.insertAfter(element);
					}
				},
				highlight: function(element, errorClass, validClass) {
					$(element).addClass("is-invalid").removeClass("is-valid");
				},
				unhighlight: function(element, errorClass, validClass) {
					$(element).addClass("is-valid").removeClass("is-invalid");
				}
			});
		
			if ($("#sel_porte_form").valid()) {
				$('#sel_porte_form').attr('action', 'ws');
				$('input[name="qs"]').val('opscrypto');
				$('input[name="rules"]').val('opscreatebuyoffer');
				$('input[name="hdnusertype"]').val($('#selleditusertype :selected').val());
				$('input[name="hdnselaccount"]').val($('#selectaccount :selected').val());
		
				//$("#editmerchuser-form").submit();
				$('#btn_buy_offer').addClass('btn-loading');

				var formData = new FormData($('#sel_porte_form')[0]);
				$.ajaxSetup({
					beforeSend: function(xhr) {
						xhr.setRequestHeader('x-api-key', getAPIKey());
					}
				});
				$.ajax({
					url: 'ms',
					data: formData,
					processData: false,
					contentType: false,
					type: 'POST',
					success: function(result) {
						//alert('result is '+result);
						$('#btn_buy_offer').removeClass('btn-loading');

						var data = JSON.parse(result);
						if (data.error == 'false') {
							//alert('lgtoken is '+data.token)
							Swal.fire({
								icon: 'success',
								title: 'Good',
								text: data.message,
								showConfirmButton: true,
								confirmButtonText: "Ok",
							}).then(function() {
								//location.reload();
								$('#sel_porte_form').attr('action', 'ws');
								$('#sel_porte_form input[name="qs"]').val('opscrypto');
								$('#sel_porte_form input[name="rules"]').val('View Offers');
								$("#sel_porte_form").submit();
							});
		
						} else {
							Swal.fire({
								icon: 'error',
								title: 'Oops',
								text: data.message,
								showConfirmButton: true,
								confirmButtonText: "Ok",
							}).then(function() {
								//Do Nothing
							});
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
							//Do Nothing
						});
					}
				});
		
			} else {
				Swal.fire({
					icon: 'error',
					title: 'Oops..',
					text: 'Please check your data'
					//footer: '<a href>Why do I have this issue?</a>'
				})
		
				return false;
			}
		});
		
		