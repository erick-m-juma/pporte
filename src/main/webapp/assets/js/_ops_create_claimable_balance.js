$(window).on("load", function(e) {
    $("#global-loader").fadeOut("slow");
})

function fnUpdatesenderparams() {
    var assetcode = $("#sender_asset option:selected").val();
    if (assetcode == '') {
        swal.fire('Select Destination Coin');
        return false;
    } else {
        $("#spansendcode").text(assetcode);
        $("#receiver_asset").val(assetcode);
    }
}

function fnUpdatereceiveparams() {
    var assetcode = $("#sender_asset option:selected").val();
    console.log('assetcode ia ', assetcode);
    if (assetcode == '') {
        swal.fire('Select Destination Coin');
        return false;
    } else {
        $("#spanreceivedcode").text(assetcode);
    }		
}

$( document ).ready(function() {
    //fnGetlastFiveTxn ();
});

function fnGetCoinDetails(){
	$('#post-form input[name="qs"]').val('porte');
	$('#post-form input[name="rules"]').val('get_crypto_assets_details');
	var form = $('#post-form')[0];
    var formData = new FormData(form);	
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
				var porteCoinList=data.data;	
				console.log(porteCoinList);
                var htmlOptions = '';
				$('#sender_asset').html('');
				$('#receiver_asset').html('');
				if(data.error="false"){
                    if(porteCoinList.length>0){
                        htmlOptions+=`<option disabled="disabled" value="-1" selected>Select Coin</option>`;
                        for (i=0; i<porteCoinList.length;i++){
                            htmlOptions+=`<option class="icon-btcoin" value="`+porteCoinList[i].assetCode+`">`+porteCoinList[i].assetDescription+`</option>`;
                        }
                    }else{
                        htmlOptions+=`<option disabled="disabled" value="-1" selected>No assets available</option>`;
                    }
                    $('#sender_asset').append(htmlOptions);
                    $('#receiver_asset').append(htmlOptions);
				}else{
                        
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

function fnTransferCoin (security){
    $( "#transfer-coin-form" ).validate( {
        rules: {
            sender_asset: {
                required: true
            },
            input_receiver: {
                required: true
            },
            sendamount: {
                required: true
            }
            
        },
        messages: {
            sender_asset: {
                required: 'Please select coin to transfer'
            },
            input_receiver: {
                required: 'Please enter receiver'
            },
            sendamount: {
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
    if($( "#transfer-coin-form" ).valid()){
	 $('#btn-coinspayanyone').addClass('btn-loading');
        $('#transfer-coin-form input[name="qs"]').val('opscrypto');
        $('#transfer-coin-form input[name="rules"]').val('ops_create_claimable_balance');
        
        var formData = new FormData($('#transfer-coin-form')[0]);
		formData.append('security',security);
		
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
                $('#btn-coinspayanyone').removeClass('btn-loading');
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
                        $('input[name="qs"]').val('opscrypto');
                        $('input[name="rules"]').val('Create Claimable Balance');
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

function fnPrivateKey(){
	const { value: password   } = Swal.fire({
		title: 'Enter your Private Key',
		input: 'password',
		inputLabel: 'Password',
		showCancelButton: true,
		inputAttributes: {
			autocapitalize: 'off',
			autocorrect: 'off'
		},
		inputValidator: (value) => {
		if (!value) {
			return 'Please input your Private Key!';
		}
		}
	}).then((result) => {
			if (result.value) {
				var privatekey = result.value;
				fnTransferCoin(privatekey );
			}
		});
}
