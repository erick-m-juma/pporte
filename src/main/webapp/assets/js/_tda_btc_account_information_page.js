	$("button[data-dismiss=modal]").click(function(){
		$(".modal").modal('hide');
	});
	          
	function fnAddAdress(){
		//Check for the data validation	
		$("#addbtc_address_form").validate({
			rules: {
				seladdassetcode: {
					required: true
				},
				btcaddress: {
					required: true,
				},
				seladdstatus: {
					required: true
				}
			},
			messages: {
				seladdassetcode: {
					required: 'Please select the  Asset',
				},
				addexhangerate: {
					required: 'Please enter the BTC Address',
				},
				seladdstatus: {
					required: 'Please  select the status',
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
	
		if ($("#addbtc_address_form").valid()) { 
			
			var formData = new FormData($('#addbtc_address_form')[0]);
				formData.append("qs","tdabtc");
				formData.append("rules","addbtcaddress");
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
					var data = JSON.parse(result);
					if (data.error == 'false') {
						//alert('lgtoken is '+data.token)
						Swal.fire({
							icon: 'success',
							title: 'Successful',
							text: data.message,
							showConfirmButton: true,
							confirmButtonText: "Ok",
						}).then(function() {
							$('#post_form').attr('action', 'ws');
							$('#post_form input[name="qs"]').val('tdabtc');
							$('#post_form input[name="rules"]').val('Account Information');
							$("#post_form").submit();
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
	}



	function fnOpenEditModal(assetcode, address, status, createdon ) {
		
		console.log(assetcode, address, status, createdon)
		$('#editBTCAddressModal').on('show.bs.modal', function() {
			
			$('#editassetcode').val(assetcode);
			$('#editbtcaddress').val(address);
			$('#editcreatedon').val(createdon);
			$('#selleditstatus').val(status);	
			
		});
		$('#editBTCAddressModal').modal('show');
	}
	
	function fnEditAddress() {
		//Check for the data validation	
		$("#edit_btcaddress_form").validate({
			rules: {
				editassetcode: {
					required: true
				},
				editbtcaddress: {
					required: true,
				},
				editcreatedon: {
					required: true
				},
				selleditstatus: {
					required: true
				}
			},
			messages: {
				editassetcode: {
					required: 'Asset Code is Required',
				},
				editbtcaddress: {
					required: 'Please enter the BTC Address',
				},
				editcreatedon: {
					required: 'Please enter date',
				},
				selleditstatus: {
					required: 'Please  select the status',
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
	
		if ($("#edit_btcaddress_form").valid()) {
			
			//$("#editassetrate-form").submit();
			var formData = new FormData($('#edit_btcaddress_form')[0]);
				formData.append("qs","tdabtc");
				formData.append("rules","editbtcaddress");
			
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
					var data = JSON.parse(result);
					if (data.error == 'false') {
						//alert('lgtoken is '+data.token)
						Swal.fire({
							icon: 'success',
							title: 'Successful',
							text: data.message,
							showConfirmButton: true,
							confirmButtonText: "Ok",
						}).then(function() {
							$('#post_form').attr('action', 'ws');
							$('#post_form input[name="qs"]').val('tdabtc');
							$('#post_form input[name="rules"]').val('Account Information');
							$("#post_form").submit();
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
	}



