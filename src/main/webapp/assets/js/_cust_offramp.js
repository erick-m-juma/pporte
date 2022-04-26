$(window).on("load", function(e) {
    $("#global-loader").fadeOut("slow");
})

function fnUpdatesenderparams() {
    var assetcode = $("#coin_asset option:selected").val();
    if (assetcode == '') {
        swal.fire('Select Destination Coin');
        return false;
    } else {
        cryptocoversion();
        $("#spansendcode").text(assetcode);
        //$("#receiver_asset").val(assetcode);
    }
    
}

function fnUpdateReceiverParams() {
    var assetcode = $("#receiver_asset option:selected").val();
    if (assetcode == '') {
        swal.fire('Select Destination Coin');
        return false;
    } else {
        cryptocoversion();
        $("#spanreceivedcode").text(assetcode);
       // $("#receiver_asset").val(assetcode);
    }
    
}


function fnGetCoinDetails(){
	$('#post-form input[name="qs"]').val('porte');
	$('#post-form input[name="rules"]').val('get_crypto_assets_details_vesl_porte');
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
				$('#coin_asset').html('');
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
            
                    $('#coin_asset').append(htmlOptions);
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

function fnSellPorteCoin (security,hasMnemonic){
    $( "#sell-porte-coin-form" ).validate( {
        rules: {
            coin_asset: {
                required: true
            },
            sell_amount: {
                required: true
            }
        },
        messages: {
            coin_asset: {
                required: 'Please select coin to swap'
            },
            sell_amount: {
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

    if($( "#sell-porte-coin-form" ).valid()){
		$('#btn-swap-coins').addClass('btn-loading');
        var formData = new FormData($('#sell-porte-coin-form')[0]);
        formData.append('qs', 'porte');
        formData.append('rules', 'burn_porte_coins'); 
		formData.append('security',security);
		formData.append('hasMnemonic',hasMnemonic);
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
                $('#btn-swap-coins').removeClass('btn-loading');
                var data = JSON.parse(result);
                console.log('data ',data);
                if(data.error=='false'){
                    Swal.fire({
                        icon: 'success',
                        title: 'Success',
                        text: data.message,
                        showConfirmButton: true,
                        confirmButtonText: "Ok",
                    }).then(function() {
                        $('#post-form').attr('action', 'ws');
                        $('input[name="qs"]').val('porte');
                        $('input[name="rules"]').val('Display Transactions');
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

function fnFetchPorteCoins(){
	$('#post-form input[name="qs"]').val('porte');
	$('#post-form input[name="rules"]').val('get_porte_coins_details');
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
				var porteDetails=data.data;	
				console.log(porteDetails);
				var htmlOptions = '';
				$('#coin_balances').html('');
				if(data.error="false"){
                    htmlOptions+=`<h2 class="dashboard-title">Coin Balance</h2>`;
					if(porteDetails.length>0){
						for (i=0; i<porteDetails.length;i++){
							if(porteDetails[i].walletType === "P"){
                                htmlOptions+=`<div class="wallet-transaction-box clearfix"  >
                                                    <div class="wallet-balance-ico">
                                                        <img src="assets/images/crypto/porte.png" alt="Litcoin" height="40" width="40" >
                                                    </div>
                                                    <div class="wallet-transaction-name">
                                                        <h3>`+porteDetails[i].assetCode+`</h3>
                                                        <span>Last Updated</span>
                                                    </div>
                                                    <div class="wallet-transaction-balance">
                                                        <h3> `+porteDetails[i].currentBalance+` `+porteDetails[i].assetCode+`</h3>
                                                        <span>`+porteDetails[i].lastUpdated+`</span>
                                                    </div>
                                                </div>`;
							}else if(porteDetails[i].walletType === "V"){
                                htmlOptions+=`<div class="wallet-transaction-box clearfix">
                                                <div class="wallet-balance-ico">
                                                    <img src="assets/images/crypto/stable.png" alt="Litcoin" height="40" width="40" >
                                                </div>
                                                <div class="wallet-transaction-name">
                                                    <h3>`+porteDetails[i].assetCode+`</h3>
                                                    <span>Last Updated</span>
                                                </div>
                                                <div class="wallet-transaction-balance">
                                                    <h3> `+porteDetails[i].currentBalance+` `+porteDetails[i].assetCode+`</h3>
                                                    <span>`+porteDetails[i].lastUpdated+`</span>
                                                </div>
                                            </div>`;
							}
							
						}
				   }else{
						Swal.fire({
							icon: 'error',
							title: 'Oops',
							text: 'You dont have porte coin, click Register to register',
							showConfirmButton: true,
							confirmButtonText: "Register",
							closeOnConfirm: true,
							}).then(function() {
								$('#post-form').attr('action', 'ws');
								$('input[name="qs"]').val('wal');
								$('input[name="rules"]').val('Create Wallet');
								$("#post-form").submit();
						});
				   } 
				   $('#coin_balances').append(htmlOptions);
                 

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







function fnGetExpectedAmount(){
    $( "#sell-porte-coin-form" ).validate( {
        rules: {
            coin_asset: {
                required: true
            },
            amount: {
                required: true
            },
            receiver_asset:{
                required: true
            }
        },
        messages: {
            coin_asset: {
                required: 'Please select coin to swap'
            },
            amount: {
                required: 'Please enter amount'
            },
            receiver_asset:{
                required: 'Please select destination coin '
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

    if($( "#sell-porte-coin-form" ).valid()){

        $('#post-form input[name="qs"]').val('porte');
        $('#post-form input[name="rules"]').val('get_expected_amount');
        var form = $('#post-form')[0];
        var formData = new FormData(form);
      
        formData.append("coin_asset", $("#coin_asset option:selected").val());
        formData.append("receiver_asset",  $("#receiver_asset option:selected").val());
        formData.append("amount",  $("#amount").val());

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
                if (result) {
                    var data = JSON.parse(result);
                   
                    var destinationAmount = '';
                    if(data.error="false"){
                        destinationAmount=data.destination_amount;
                    }else{
                        destinationAmount="";
                    }
                    $("#receivedamount").val(destinationAmount);
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

$( document ).ready(function() {
    //exchange_icon();
    fnFetchPorteCoins ();
    $("#sell_amount").keyup(function() {
        cryptocoversion();
  });

  $("#amount_usd").keyup(function() {
    cryptocoversionBackwards();
  });
});

function cryptocoversion(){
    var conversion = 1;
    var assetcode = $("#coin_asset option:selected").val();
   
    if(assetcode ==="PORTE"){
     conversion = 0.740000;
    }else if(assetcode ==="VESL"){
      conversion = 1.5;
    }
    var amount = $("#sell_amount").val();
    var equivalence = amount*conversion;

     $("#amount_usd").val(equivalence);
    
}

function cryptocoversionBackwards(){
    var conversion = 1;
    var assetcode = $("#coin_asset option:selected").val();
 
    if(assetcode ==="PORTE"){
     conversion = 0.74074;
    }else if(assetcode ==="VESL"){
      conversion = 0.6667;
    }
    var amount = $("#receivedamount").val();
    var equivalence = amount*conversion;

     $("#amount").val(equivalence);
    
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
                    fnPassword(data.hasmnemonic);
                }else{
                    fnPrivateKey(data.hasmnemonic)
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


function fnPassword(hasMnemonic){
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
				fnSellPorteCoin(passwordVal,hasMnemonic );
			}
		});
}

function fnPrivateKey(hasMnemonic){
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
				fnSellPorteCoin(privatekey,hasMnemonic );
			}
		});
}
