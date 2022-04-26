(function($) {
	"use strict";
	
	//accordion-wizard
	var options = {
		mode: 'wizard',
		autoButtonsNextClass: 'btn btn-primary float-right',
		autoButtonsPrevClass: 'btn btn-info',
		stepNumberClass: 'badge badge-pill badge-primary mr-1',
		onSubmit: function() {
			checkIfUserHasMneonicCode();
		  return false;
		}
	}
	$( "#form" ).accWizard(options);
	$('#next_btn').click(function() {
		// alert('hohoho');
		getStellarOffers();
	}); 

		
})(jQuery);   


var offersData = '';


function getStellarOffers(){
    $( "#form" ).validate( {
        rules: {
            sel_currency: {
                required: true
            },
            source_coin: {
                required: true
            },
            amount_to_spend: {
                required: true
            }
        },
        messages: {
            sel_currency: {
                required: 'Please select Currency'
            },
            source_coin: {
                required: 'Please select Coin'
            },
            amount_to_spend: {
                required: 'Please enter amount'
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

    if($( "#form" ).valid()){
        var formData = new FormData($('#form')[0]);
        formData.append('qs', 'frx');
        formData.append('rules', 'get_offers_from_stellar'); 
        for (var pair of formData.entries()) {
			//alert(pair[0] + " - " + pair[1]);
        }	
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
                var data = JSON.parse(result);
				var offers = '';
				var size = 0;
				var counter =3;
				var htmlOptions = '';
				$('#div_offers_options').html('');
                if(data.error=='false'){
					offers	= data.data;
					offersData =offers;
					size = offers.length;
					if(size<counter){
						counter = size;
					}
					for (i=0; i<counter;i++){
						htmlOptions+=`<label class=" card custom-control custom-radio   justify-content-center" style="height: auro;padding: 40px;" >
						<input type="radio" class="custom-control-input" name="example-radios" value="`+offers[i].sourceAsset+","+offers[i].sourceAssetIssuer+","+offers[i].sourceAmount+","+offers[i].destinationAsset+","+offers[i].destinationIssuer+","+offers[i].destinationAmount+"-"+i+`" checked>
							<span class="custom-control-label">`+offers[i].destinationAsset+`</span>
							<div class="col-lg-12">
								<div class="row mt-3">
									<div class="col-8  card p-3 text-center shadow-md ">
										<h6  class="text-warning">Asset Issuer</h6>
										<p>`+offers[i].destinationIssuer+`</p>
									</div>
									<div class="col  card p-3 shadow-md ">
										<h6  class="text-warning">Expected Amount</h6>
										<p>`+offers[i].destinationAsset+": "+offers[i].destinationAmount+`</p>
									</div>
								</div>
							</div>
						</label>`
					}					
                }else{
					htmlOptions+='<div> No offers available </div>';
                }
				$('#div_offers_options').append(htmlOptions);
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

function checkTrustLine(){
	var radioValue = $("input[name='example-radios']:checked").val();
	var formData = new FormData($('#post-form')[0]);
	formData.append('qs', 'frx');
	formData.append('rules', 'check_trustline'); 
	formData.append('radio_value', radioValue); //Get this from radio button
	
	for (var pair of formData.entries()) {
	  //console.log(pair[0] + " - " + pair[1]);
	}	
	$.ajaxSetup({
		beforeSend: function(xhr) {
			xhr.setRequestHeader('x-api-key' , getAPIKey());
		}
	});
	//submit_btn
	$('#submit_btn').addClass('btn-loading');
	$.ajax({
		url: 'ms',
		data: formData,
		processData: false,
		contentType: false,
		type: 'POST',
		success: function (result) {
			var data = JSON.parse(result);
			$('#submit_btn').removeClass('btn-loading');
			if(data.error=='false'){
				if(!data.has_trustline){
					Swal.fire({
						title: 'You do not have a Trustline with this issuer',
						html: 'Do you want to create Trustline?',
						icon: 'info',
						showCancelButton: true,
						confirmButtonColor: '#3085d6',
						cancelButtonColor: '#808080',
						cancelButtonText:'No',
						confirmButtonText: 'Yes'
					  }).then((result) => {
						if (result.isConfirmed) {

							const { value: password   } = Swal.fire({
								title: 'Enter your Secret Key',
								input: 'password',
								inputLabel: 'Password',
								showCancelButton: true,
								inputAttributes: {
								autocapitalize: 'off',
								autocorrect: 'off'
								},
								inputValidator: (value) => {
								if (!value) {
									return 'Please input your Secret Key!';
								}
								}
							}).then((result) => {
									if (result.value) {
										var txnpin = result.value;
										//createTrustLine(radioValue, txnpin);
									}
								});

						}
					  })
				}else{
					fnPrivateKeyInput(radioValue);
				}
			}else{
			   
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

function createTrustLine(radioValue, password){
	var formData = new FormData($('#post-form')[0]);
	formData.append('qs', 'frx');
	formData.append('rules', 'create_trustline'); 
	formData.append('radio_value', radioValue); 
	formData.append('private_key', password); 
	
	$.ajaxSetup({
		beforeSend: function(xhr) {
			xhr.setRequestHeader('x-api-key' , getAPIKey());
		}
	});
	$('#submit_btn').addClass('btn-loading');
	$.ajax({
		url: 'ms',
		data: formData,
		processData: false,
		contentType: false,
		type: 'POST',
		success: function (result) {
			var data = JSON.parse(result);
			$('#submit_btn').removeClass('btn-loading');
			if(data.error=='false'){
				fnPrivateKeyInput(radioValue);
			}else{
				Swal.fire({
					icon: 'error',
					title: 'Oops',
					text: data.message,
					showConfirmButton: true,	
					confirmButtonText: "Ok",
					}).then(function() {
						
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
							
				});
		}
	}); 
}


function fnPrivateKeyInput(hasmnemonic){
	var radioValue = $("input[name='example-radios']:checked").val();
	const { value: password   } = Swal.fire({
		title: 'Enter your Secret Key',
		input: 'password',
		inputLabel: 'Password',
		showCancelButton: true,
		inputAttributes: {
		autocapitalize: 'off',
		autocorrect: 'off'
		},
		inputValidator: (value) => {
		if (!value) {
			return 'Please input your Secret Key!';
		}
		}
	}).then((result) => {
		if (result.value) {
				var txnpin3 = result.value;
				exchangeDigitalAssetToCurrency(radioValue, txnpin3, hasmnemonic);
			}
		});
}


function fnPasswordInput(hasmnemonic){
	var radioValue = $("input[name='example-radios']:checked").val();
	const { value: password   } = Swal.fire({
		title: 'Enter your Password',
		input: 'password',
		inputLabel: 'Password',
		showCancelButton: true,
		inputAttributes: {
			autocapitalize: 'off',
			autocorrect: 'off'
		},
		inputValidator: (value) => {
		if (!value) {
			return 'Please input your password!';
		}
		}
	}).then((result) => {
		if (result.value) {
				var txnpin3 = result.value;
				exchangeDigitalAssetToCurrency(radioValue, txnpin3, hasmnemonic);
			}
		});
}




function exchangeDigitalAssetToCurrency(radioValue, password, hasMnemonic){
	///alert('offersData '+offersData)
	var radioArray = radioValue.split("-");
	var selectedDataValue = radioArray[1];
	selectedObj = offersData[selectedDataValue];
	var pathObj = JSON.stringify(selectedObj);
	/* alert(selectedDataValue);
	alert(JSON.stringify(selectedObj)); */
	var formData = new FormData($('#form')[0]);
	formData.append('qs', 'frx');
	formData.append('rules', 'exchange_digital_asset_to_currency'); 
	formData.append('json_string', pathObj); 
	//formData.append('private_key', password); 
	
	formData.append('security',password);
	formData.append('hasMnemonic',hasMnemonic);
	
	/* for (var pair of formData.entries()) {
		alert(pair[0] + " - " + pair[1]);
	  }	 */

	$.ajaxSetup({
		beforeSend: function(xhr) {
			xhr.setRequestHeader('x-api-key' , getAPIKey());
		}
	});
	$('#submit_btn').addClass('btn-loading');
	$.ajax({
		url: 'ms',
		data: formData,
		processData: false,
		contentType: false,
		type: 'POST',
		success: function (result) {
			$('#submit_btn').removeClass('btn-loading');
			var data = JSON.parse(result);
			if(data.error=='false'){
				Swal.fire({
					icon: 'success',
					title: 'Currency Exchange Successfull',
					text: data.message,
					showConfirmButton: true,	
					confirmButtonText: "Ok",
					}).then(function() {
						$('#post-form-2').attr('action', 'ws');
						$('input[name="qs"]').val('frx');
						$('input[name="rules"]').val('get_pending_currency_trading_page');
						$("#post-form-2").submit();	
					});
			}else{
				Swal.fire({
					icon: 'error',
					title: 'Oops',
					text: data.message,
					showConfirmButton: true,	
					confirmButtonText: "Ok",
					}).then(function() {
						
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
							
				});
		}
	}); 
}

function fnCallRemittanceTransactionPage(){
	$('#post-form-2').attr('action', 'ws');
	$('input[name="qs"]').val('frx');
	$('input[name="rules"]').val('get_pending_currency_trading_page');
	$("#post-form-2").submit();	
}

function checkIfUserHasMneonicCode(){
	var formData = new FormData();
	formData.append('qs','porte')
	formData.append('rules','check_if_customer_has_mnemonic_code')
    for (var pair of formData.entries()) {
      //console.log(pair[0] + " - " + pair[1]);
    }	
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
            //wallet_txn
            var data = JSON.parse(result);
            var htmlData ='';
            var txnList = data.data;
            if(data.error=='false'){
                if(data.hasmnemonic=='true'){
                    fnPasswordInput(data.hasmnemonic);
                }else{
                    fnPrivateKeyInput(data.hasmnemonic)
                }
               
            }else{
                Swal.fire({
                    icon: 'error',
                    title: 'Oops',
                    text: data.message,
                    showConfirmButton: true,
                    confirmButtonText: "Ok",
                    closeOnConfirm: true,
                    }).then(function() {
                       
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
                            
                });
        }
    });             	
	
}
















