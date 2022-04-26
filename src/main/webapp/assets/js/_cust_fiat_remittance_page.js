(function($) {
	"use strict";
	
	//accordion-wizard
	var options = {
		mode: 'wizard',
		autoButtonsNextClass: 'btn btn-primary float-right',
		autoButtonsPrevClass: 'btn btn-info',
		stepNumberClass: 'badge badge-pill badge-primary mr-1',
		onSubmit: function() {
			//checkIfUserHasMneonicCode();
            alert('hohoho');
		  return false;
		}
	}
	$( "#form" ).accWizard(options);
	$('#next_btn').click(function() {
		// alert('hohoho');
		//getStellarOffers();
	}); 

		
})(jQuery);   




function fnSubmitPayment (){
    $( "#buy-btcx-coin-form" ).validate( {
         rules: {
            sel_fiat_currency: {
                required: true
            },
            sel_digital_currency: {
                required: true
            },
            source_amount: {
                required: true
            },
            
            receiver_name: {
                required: true
            },
            receiver_email: {
                required: true,
				email: true
            },
            receiver_bank_name: {
                required: true
            },
            receiver_bank_code: {
                required: true
            },
            receiver_account_no: {
                required: true
            }
        },
        messages: {
	
	
			sel_fiat_currency: {
                required: 'Please select Currency'
            },
            sel_digital_currency: {
                required: 'Please select Currency'
            },
            source_amount: {
                required: 'Please enter source amount'
            },
            
            receiver_name: {
                required: 'Please enter receivers name'
            },
            receiver_email: {
                required: 'Please enter receivers email',
				email: 'This field has to be email'
            },
            receiver_bank_name: {
                required: 'Please enter receivers bank name'
            },
            receiver_bank_code: {
                required: 'Please enter receivers bank code'
            },
            receiver_account_no: {
                required: 'Please enter account no'
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

    if($( "#buy-btcx-coin-form" ).valid()){
        var formData = new FormData($('#buy-btcx-coin-form')[0]);
        formData.append('qs', 'porte');
        formData.append('rules', 'cust_fiat_remmittance'); 
        for (var pair of formData.entries()) {
          console.log(pair[0] + " - " + pair[1]);
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
                console.log('data ',data);
                if(data.error=='false'){
                    Swal.fire({
                        icon: 'success',
                        text: data.message,
                        showConfirmButton: true,
                        confirmButtonText: "Ok",
                    }).then(function() {
                         $('#post-form').attr('action', 'ws');
						   $('input[name="qs"]').val('btcx');
						    $('input[name="rules"]').val('view_pending_btcx_fiat_txn');
						    $("#post-form").submit();	
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


function fnUpdateDestinationParams() {
    var assetcode = $("#sel_digital_currency option:selected").val();
    if (assetcode == '') {
        swal.fire('Select Destination Coin');
        return false;
    } else {
        //UpdateConversionRate();
        $("#span_expected_code").text(assetcode);
        // $("#span_expected_code").val(assetcode);
    }
    
}

function getExpectedAmount(){
    $( "#form" ).validate( {
        rules: {
           sel_fiat_currency: {
               required: true
           },
           sel_digital_currency: {
               required: true
           },
           source_amount: {
               required: true
           }
       },
       messages: {
           sel_fiat_currency: {
               required: 'Please select Currency'
           },
           sel_digital_currency: {
               required: 'Please select Currency'
           },
           source_amount: {
               required: 'Please enter source amount'
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
       formData.append('rules', 'get_expected_amount_fiat_remittance'); 
       for (var pair of formData.entries()) {
         console.log(pair[0] + " - " + pair[1]);
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
               console.log('data ',data);
               var expectedAmount = '';
               if(data.error=='false'){
                expectedAmount = data.expectedAmount;
                $("#expected_amount").val(expectedAmount);
                $("#expected_amount_warning_text").show();
               }else{
                   Swal.fire({
                       text: data.message, 
                       icon: "error",
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
}

function fnCallRemittanceTransactionPage(){
	$('#post-form-2').attr('action', 'ws');
	$('input[name="qs"]').val('frx');
	$('input[name="rules"]').val('get_pending_currency_trading_page');
	$("#post-form-2").submit();	
}
