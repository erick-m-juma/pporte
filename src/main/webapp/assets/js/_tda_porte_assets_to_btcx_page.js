$(document).ready(function () {
	$('#addsellrate').attr('disabled','disabled');
	/*$('#viewassetpricingtable').dataTable( {
			    "order": []
			} );*/
		
			$('#viewbtcxpricingtable').dataTable({ "bSort" : false } );

});

$('#btn-addassetpricing').click(function() {
		//Check for the data validation	
		$("#addassetpricing-from").validate({
			rules: {
				seladdassetcode: {
					required: true
				},
				addexhangerate: {
					required: true,
				},
				selladdstatus: {
					required: true
				}
			},
			messages: {
				seladdassetcode: {
					required: 'Please select the selling Asset',
				},
				addexhangerate: {
					required: 'Please enter the markup rate',
				},
				selladdstatus: {
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
	
		if ($("#addassetpricing-from").valid()) { 
			$('#addassetpricing-from').attr('action', 'ms');
			$('input[name="qs"]').val('tdaprcing');
			$('input[name="rules"]').val('addporteassetmarkuprate');
			$('input[name="hdnsellratetystatus"]').val($('#selladdstatus :selected').val());
			$('input[name="hdnaddassetcode"]').val($('#seladdassetcode :selected').val());
		
			//$("#addassetpricing-from").submit();
			var formData = new FormData($('#addassetpricing-from')[0]);
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
							title: 'Good',
							text: data.message,
							showConfirmButton: true,
							confirmButtonText: "Ok",
						}).then(function() {
							$('#addassetpricing-from').attr('action', 'ws');
							$('#addassetpricing-from input[name="qs"]').val('tdaprcing');
							$('#addassetpricing-from input[name="rules"]').val('Porte Asset to BTCx');
							$("#addassetpricing-from").submit();
						});
	
					} else {
						Swal.fire({
							icon: 'error',
							title: 'Oops',
							text: data.error,
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

	$("button[data-dismiss=modal]").click(function(){
		$(".modal").modal('hide');
	});

	function fnEditAssetPrice(assetcode, markuprate, status, createdon,sequenceno ) {
		$('#editBTCxPricingModal').on('show.bs.modal', function() {
		$('#editassetcode').val(assetcode);
		$('#editsellrate').val(markuprate);
		$('#selleditstatus').val(status);
		$('#editcreatedon').val(createdon);	
		$('#hdnsequenceno').val(sequenceno);	
		});
	
		$('#editBTCxPricingModal').modal('show');
	}
	
	$('#btn-editeditassetpricing').click(function() {
		//Check for the data validation	
		$("#editassetrate-form").validate({
			rules: {
				editassetcode: {
					required: true
				},
				
				editsellrate: {
					required: true,
				},
				selleditstatus: {
					required: true
				}
			},
			messages: {
				editassetcode: {
					required: 'Asset Code is Required',
				},
				editsellrate: {
					required: 'Please enter the markup rate',
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
	
		if ($("#editassetrate-form").valid()) {
			$('#editassetrate-form').attr('action', 'ms');
			$('input[name="qs"]').val('tdaprcing');
			$('input[name="rules"]').val('editporteassetmarkuprate');
			$('input[name="hdnsellratetystatus"]').val($('#selleditstatus :selected').val());
		
			//$("#editassetrate-form").submit();
			var formData = new FormData($('#editassetrate-form')[0]);
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
							title: 'Good',
							text: data.message,
							showConfirmButton: true,
							confirmButtonText: "Ok",
						}).then(function() {
							$('#addassetpricing-from').attr('action', 'ws');
							$('#addassetpricing-from input[name="qs"]').val('tdaprcing');
							$('#addassetpricing-from input[name="rules"]').val('Porte Asset to BTCx');
							$("#addassetpricing-from").submit();
						});
	
					} else {
						Swal.fire({
							icon: 'error',
							title: 'Oops',
							text: data.error,
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
		
		function UpdateassetDescParam(){
			var asset_desc =  $('#seladdassetcode :selected').val();
			var arrasset = asset_desc.split(",");
			$("#addassetdesc").val(arrasset[1]);
			$("#spansellassetcode").text(arrasset[0]);	
			
			if(asset_desc !==''){
			  $("#exchangerate").hide();
			    $('#addexhangerate').removeAttr('disabled');
			}else{
				$('#addexhangerate').attr('disabled','disabled');
			}		
		}
		
		function UpdateSellamount(){
			$("#exchangerate").show();
			
			var amount = $("#addexhangerate").val();
			$("#spansexchangerate").text(amount);
			
			if($("#addexhangerate").val() ===''){
				$("#exchangerate").hide();
			}


		}


