function fnGetSourceCoinDetailBTCSwap(){
	$('#post-form input[name="qs"]').val('btc');
	$('#post-form input[name="rules"]').val('get_btc_details');
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
                console.log('data ', data);
				var porteCoinList=data.data;	
                var htmlOptions = '';
				$('#div_source_asset_swap_btc').html('');
				
				if(data.error="false"){
                    if(porteCoinList.length>0){
                     //  htmlOptions+=`<option disabled="disabled" value="-1" selected>Select Coin</option>`;
                        for (i=0; i<porteCoinList.length;i++){
                            
                                htmlOptions+=`<div class="col-3">
                                <label>
                                    <input  type="radio" name="coin_asset_swap_btc" disabled value="`+porteCoinList[i].assetCode+`">
                                    <img src="assets/images/crypto/bitcoin.svg" alt="`+porteCoinList[i].assetCode+`" height="40" width="40" >
                                    
                                </label>
                            </div>
                            <div class="col" style="margin-top: 7px;">
                                <p>`+porteCoinList[i].assetCode+`</p>
                            </div>`;
                            
                        }
                    }else{
                        htmlOptions+=`<p>No assets available</p>`;
                    }
            
                    $('#div_source_asset_swap_btc').append(htmlOptions);
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

function fnGetBTCxDetailsSwapBTC(){
	$('#post-form input[name="qs"]').val('porte');
	$('#post-form input[name="rules"]').val('get_btcx_details');
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
                console.log('data', data);
                var htmlOptions = '';
				$('#div_destination_asset_swap_btc').html('');
				
				if(data.error="false"){
                    if(porteCoinList.length>0){
                        for (i=0; i<porteCoinList.length;i++){
                            htmlOptions+=`<div class="col-3">
                            <label>
                                <input  type="radio" name="test" disabled value="`+porteCoinList[i].assetCode+`">
                                <img src="assets/images/crypto/bitcoin.svg" alt="`+porteCoinList[i].assetCode+`" height="40" width="40" >
                                
                            </label>
                            </div>
                            <div class="col" style="margin-top: 7px;">
                                <p>`+porteCoinList[i].assetCode+`</p>
                            </div>`;
                        }
                       
                    }else{
                        htmlOptions+=`<p>No assets available</p>`;
                    }
            
                    $('#div_destination_asset_swap_btc').append(htmlOptions);
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

function fnExChangeBTCToBTCx(security,hasMnemonic){
    $( "#exchange-btc-coin-form" ).validate( {
        rules: {
           
            destination_amount_swap_btc: {
                required: true
            }
           
        },
        messages: {
           
            destination_amount_swap_btc: {
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

    if($( "#exchange-btc-coin-form" ).valid()){
		$('#btn-swap-coins_swap_btc').addClass('btn-loading');
        var formData = new FormData($('#exchange-btc-coin-form')[0]);
        formData.append('qs', 'btc');
        formData.append('rules', 'exchange_btc_with_btcx'); 
        formData.append('source_asset', 'BTC'); 
        formData.append('destination_asset', 'BTCX'); 




		formData.append('security',security);
		formData.append('hasMnemonic',hasMnemonic);
        for (var pair of formData.entries()) {
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
                $('#btn-swap-coins_swap_btc').removeClass('btn-loading');
                var data = JSON.parse(result);
                // console.log('data ',data);
                if(data.error=='false'){
                    Swal.fire({
                        icon: 'success',
                        title: 'Transfer Complete',
                        text: data.message,
                        showConfirmButton: true,
                        confirmButtonText: "Ok",
                    }).then(function() {
                        $('#post-form').attr('action', 'ws');
                        $('input[name="qs"]').val('btc');
                        $('input[name="rules"]').val('view_pending_btc_swapping_txn');
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


/*
function cryptocoversion(){
    var conversion = 1;
    var assetcode = $("#coin_asset option:selected").val();
   
    if(assetcode ==="PPT"){
     conversion =  1.35;
    }else if(assetcode ==="VSL"){
      conversion = 1.5;
    }
    var amount = $("#amount").val();
    var equivalence = amount*conversion;

     $("#receivedamount").val(equivalence);
    
}*/

/*
function cryptocoversionBackwards(){
    var conversion = 1;
    var assetcode = $("#coin_asset option:selected").val();
 
    if(assetcode ==="PPT"){
     conversion = 0.74074;
    }else if(assetcode ==="VSL"){
      conversion = 0.6667;
    }
    var amount = $("#receivedamount").val();
    var equivalence = amount*conversion;

     $("#amount").val(equivalence);
    
}*/

$( document ).ready(function() {
    fnGetSourceCoinDetailBTCSwap();
    fnGetBTCxDetailsSwapBTC();
    $("#destination_amount_swap_btc").keyup(function() {
        
    cryptocoversionFromSourceBTCSwap();
  });
});

function cryptocoversionFromSourceBTCSwap(){
  //var sourceAssetcode = $("#source_asset option:selected").val();
  //var destAssetcode = $("#destination_asset option:selected").val();
  var amount = $("#destination_amount_swap_btc").val();
 // alert(amount);
  $("#source_amount_swap_btc").val(amount);
}


function checkIfUserHasMneonicCodeSwapBTC(){
	var formData = new FormData();
	formData.append('qs','porte')
	formData.append('rules','check_if_customer_has_bitcoin_mnemonic_code')
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
                    fnPasswordSwapBTC(data.hasmnemonic);
                }else{
                    fnPrivateKeySwapBTC(data.hasmnemonic)
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

function fnPasswordSwapBTC(hasMnemonic){
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
				var passwordVal = result.value;
				fnExChangeBTCToBTCx (passwordVal,hasMnemonic);
			}
		});
}

function fnPrivateKeySwapBTC(hasMnemonic){
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
				fnExChangeBTCToBTCx (privatekey,hasMnemonic);
			}
		});
}

function fnCallPendingTransactionPage(){
    $('#post-form').attr('action', 'ws');
    $('input[name="qs"]').val('btc');
    $('input[name="rules"]').val('view_pending_btc_swapping_txn');
    $("#post-form").submit();	
}



