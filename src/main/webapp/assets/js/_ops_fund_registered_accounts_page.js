 $(window).on("load", function(e) {
    $("#global-loader").fadeOut("slow");
})
        
		function fnFundAccountModal(custname, publickey, status, createdon ) {
				$('#edittxnlimitModal').on('show.bs.modal', function() {
					$('#customername').val(custname);
					$('#publickey').val(publickey);
					$('#status').val(status);
					$('#createdon').val(createdon);
				});
			
				$('#edittxnlimitModal').modal('show');
			
			}
			
				$('#btn-edittxnlimits').click(function() {
					$("#fund_acc_form").validate({
				rules: {
					customername: {
						required: true,
					},
					publickey: {
						required: true,
					},
					dailylimit: {
						required: true,
					},
					status: {
						required: true
					},
					createdon: {
						required: true
					},businesspvtkey:{
						required: true	
					},amount:{
						required: true	
					}
				},
				messages: {
					customername: {
						required: 'Customer name is required',

					},
					publickey: {
						required: 'Customer public key is required',
					},
					dailylimit: {
						required: 'Please enter the Daily Limit',
					},
					status: {
						required: 'Please select status',
					},
					walletlimit: {
						required: 'Please select the Wallet limit',
					},
					createdon: {
						required: 'Date created is required',
					},
					businesspvtkey: {
						required: 'Business Private key required',
					},
					amount: {
						required: 'Please Enter Amount',
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
		
			if ($("#fund_acc_form").valid()) {
				$('#fund_acc_form').attr('action', 'ws');
				$('input[name="qs"]').val('opscrypto');
		  		$('input[name="rules"]').val('opsfundspecificacc');
				
				//$("#editmerchuser-form").submit();
				$('#btn-edittxnlimits').addClass('btn-loading');

				var formData = new FormData($('#fund_acc_form')[0]);
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
						$('#btn-edittxnlimits').removeClass('btn-loading');
						var data = JSON.parse(result);
						if (data.error == 'false') {
							Swal.fire({
								icon: 'success',
								title: 'Good',
								text: data.message,
								showConfirmButton: true,
								confirmButtonText: "Ok",
							}).then(function() {
								//location.reload();
								$('#fund_acc_form').attr('action', 'ws');
								$('#fund_acc_form input[name="qs"]').val('opscrypto');
								$('#fund_acc_form input[name="rules"]').val('View Funded Accounts');
								$("#fund_acc_form").submit();
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