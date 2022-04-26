function fnStellarPresent(select){

	if(select ==='Yes'){
		 $('#stellar-present').show();
         $('#stellar-not-present').hide();
	}else{
		$('#stellar-present').hide();
		$('#stellar-not-present').show();
	}
}

function  fnShowBtcxIssuerInput(){
	var selectedAsset = $("#asset_value option:selected").val();
	
	if (selectedAsset==="BTC"){
		$("#btcissueridinput").removeClass("hidden");
	}else{
	    $("#btcissueridinput").addClass("hidden");
	}
	   

}

function fnRegisterStellarBTCxAccount(){
   
	$( "#register-stellar" ).validate( {
        ignore: [],
        rules: {
            has_account: {
                required: true
            },
            input_public_key: {
                required:function () {
                    return $("input[name='has_account']:checked").val()==="true";
                }
            },
            create_stellar: {
                required:function () {
                    return $("input[name='has_account']:checked").val()==="false";
                }
            },
           
        },
        messages: {
            has_account: {
                required: 'Select one of these',
            },
            input_public_key: {
                required: 'Please input the public key',
            },
          
            create_stellar: {
                required: 'Select the checkbox before you proceed',
            },
          
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

    if($( "#register-stellar" ).valid()){
        $('#btn-register-stellar-ac').addClass('btn-loading');
             $('#register-stellar input[name="qs"]').val('tdaacct');
      		  $('#register-stellar input[name="rules"]').val('create_stellar_btcx_account');
        var formData = new FormData($('#register-stellar')[0]);
          
        for (var pair of formData.entries()) {
         console.log(pair[0] + " - " + pair[1]);
        }	
        	
		     $.ajaxSetup({
		            beforeSend: function(xhr) {
		                xhr.setRequestHeader('x-api-key' , getAPIKey());
		            }
		        });
            $.ajax({
                url: "ms",
                data: formData,
                processData: false,
                contentType: false,
                type: 'POST',
                success: function (result) {
                    $('#btn-register-stellar-ac').removeClass('btn-loading');
                    var data = JSON.parse(result);
                    console.log('data ',data);
                    if(data.error=='false'){
                        Swal.fire({
                            icon: 'success',
                            text:  data.message,
                            showConfirmButton: true,
                            confirmButtonText: "Ok",
                        }).then(function() {
                           $('#register-stellar').attr('action', 'ws');
	                        $('input[name="qs"]').val('tdaacct');
	                        $('input[name="rules"]').val('Configuration');
	                        $("#register-stellar").submit();
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

function fnCreateTrustline(){
	 $( "#create-trustline-form" ).validate( {
        rules: {
            asset_value: {
                required: true
            },
            btcx_issuer: {
                required: true
            },
            input_private_key: {
                required: true
            },
        },
        messages: {
            btcx_issuer: {
                required: 'Please Enter BTC Issuer Public Key ',
            },
            asset_value: {
                required: 'Asset is required'
            },
            input_private_key: {
                required: 'Enter Private Key'
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
    if($( "#create-trustline-form" ).valid()){
		
		$('#btn_create_trustline').addClass('btn-loading');
        $('#create-trustline-form input[name="qs"]').val('tdaacct');
        $('#create-trustline-form input[name="rules"]').val('create_btcx_trustline');
        
        var formData = new FormData($('#create-trustline-form')[0]);
    
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
				$('#btn_create_trustline').removeClass('btn-loading');
                //console.log('data ',data);
                if(data.error=='false'){
                    Swal.fire({
                        icon: 'success',
                        text: data.message,
                        showConfirmButton: true,
                        confirmButtonText: "Ok",
                    }).then(function() {
                            $('#post-form').attr('action', 'ws');
	                        $('input[name="qs"]').val('tdaacct');
	                        $('input[name="rules"]').val('Configuration');
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
