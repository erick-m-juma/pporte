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

function fnUpdatesenderparamsCB() {
    var assetcode = $("#sender_asset_cb option:selected").val();
    if (assetcode == '') {
        swal.fire('Select Destination Coin');
        return false;
    } else {
        $("#spansendcode_cb").text(assetcode);
        $("#receiver_asset_cb").val(assetcode);
    
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
	$('#post-form input[name="rules"]').val('get_crypto_assets_details_without_btc');
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
				$('#sender_asset_cb').html('');
				$('#receiver_asset').html('');
				$('#receiver_asset_cb').html('');

                
				if(data.error="false"){
                    if(porteCoinList.length>0){
                        htmlOptions+=`<option disabled="disabled" value="-1" selected>Select Coin</option>`;
                        for (i=0; i<porteCoinList.length;i++){
							 if(porteCoinList[i].assetCode!='BTCX'){
                            	htmlOptions+=`<option class="icon-btcoin" value="`+porteCoinList[i].assetCode+`">`+porteCoinList[i].assetCode+`</option>`;
                        	  }
						}
                    }else{
                        htmlOptions+=`<option disabled="disabled" value="-1" selected>No assets available</option>`;
                    }
                    $('#sender_asset').append(htmlOptions);
                    $('#sender_asset_cb').append(htmlOptions);
                    $('#receiver_asset').append(htmlOptions);
                    $('#receiver_asset_cb').append(htmlOptions);
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

function fnTransferCoin (security, hasMnemonic){
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
            },
            input_private_key: {
                required: true
            },
            mnemonic_code: {
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
            },
			input_private_key: {
                required: 'Please enter password'
            },
			mnemonic_code: {
                required: 'Please mnemonic code'
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
        $('#transfer-coin-form input[name="qs"]').val('porte');
        $('#transfer-coin-form input[name="rules"]').val('transfer_porte_coin');
        
        var formData = new FormData($('#transfer-coin-form')[0]);
		formData.append('security',security);
		formData.append('hasMnemonic',hasMnemonic);
		
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

function fnCreateClaimableBalance (security, hasMnemonic){
    $( "#transfer-coin-form-cb" ).validate( {
        rules: {
            sender_asset_cb: {
                required: true
            },
            input_receiver_cb: {
                required: true
            },
            sendamount_cb: {
                required: true
            }
            
        },
        messages: {
            sender_asset_cb: {
                required: 'Please select coin to transfer'
            },
            input_receiver_cb: {
                required: 'Please enter receiver'
            },
            sendamount_cb: {
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
    if($( "#transfer-coin-form-cb" ).valid()){
	 $('#btn-coinspayanyone_cb').addClass('btn-loading');
        $('#transfer-coin-form-cb input[name="qs"]').val('porte');
        $('#transfer-coin-form-cb input[name="rules"]').val('cust_create_claimable_balance');
        
        var formData = new FormData($('#transfer-coin-form-cb')[0]);
		formData.append('security',security);
		formData.append('hasMnemonic',hasMnemonic);
		
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
                $('#btn-coinspayanyone_cb').removeClass('btn-loading');
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

/* function fnGetlastFiveTxn(){

    $('#post-form input[name="qs"]').val('porte');
    $('#post-form input[name="rules"]').val('get_last_five_porte_txn');
    
    var formData = new FormData($('#post-form')[0]);
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
            //wallet_txn
            var data = JSON.parse(result);
            var htmlData ='';
            var txnList = data.data;
            $('#div_transactions').html('');
            console.log('data ',data);
            if(data.error=='false'){
                console.log('length is ', txnList.length)
                if(txnList.length>0){
                    htmlData+=`<h2 class="dashboard-title">Transaction</h2>`;
                    for (i=0; i<txnList.length;i++){
                        if(txnList[i].txnMode === "D"){
                            htmlData+=` <div class="wallet-transaction-box clearfix">
                                            <div class="wallet-transaction-ico sent-coin-transaction"><i class="fa fa-arrow-up"></i></div>
                                            <div class="wallet-transaction-name">
                                                <h3>`+txnList[i].assetCode+`</h3>
                                                <span>Sent</span>
                                            </div>
                                            <div class="wallet-transaction-balance">
                                            <h3>`+txnList[i].txnAmount+`</h3>
                                            <span>`+txnList[i].txnDateTime+`</span>
                                            </div>
                                        </div>`   
                      
                        }else if(txnList[i].txnMode === "C"){
                            htmlData+=`<div class="wallet-transaction-box clearfix">
                                            <div class="wallet-transaction-ico recive-coin-transaction"><i class="fa fa-arrow-down"></i></div>
                                            <div class="wallet-transaction-name">
                                                <h3>`+txnList[i].assetCode+`</h3>
                                                <span>Receive</span>
                                            </div>
                                            <div class="wallet-transaction-balance">
                                                <h3>`+txnList[i].txnAmount+`</h3>
                                                <span>`+txnList[i].txnDateTime+`</span>
                                            </div>
                                        </div>`
                        }

                    }
                }else{
                    htmlData=`<tr><td> <span>No data Present</span></td></tr>`
                }
                $('#div_transactions').append(htmlData);
               
            }else{
                Swal.fire({
                    icon: 'error',
                    title: 'Oops',
                    text: 'Failed to get Transactions',
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
 */
function checkIfUserHasMneonicCode(txnType){
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
                    fnPassword(data.hasmnemonic, txnType);
                }else{
                    fnPrivateKey(data.hasmnemonic, txnType)
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

function fnPassword(hasMnemonic, txnType){
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
				fnGetTxnType(txnType, passwordVal,hasMnemonic );
			}
		});
}

function fnPrivateKey(hasMnemonic, txnType){
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
				fnGetTxnType(txnType, privatekey,hasMnemonic );
			}
		});
}

function fnGetTxnType(txnType, security,hasMnemonic){
    console.log('txnType ', txnType);
    if(txnType =='cb'){
        console.log('txnType1 ', txnType);
        fnCreateClaimableBalance(security,hasMnemonic);
    }else{
        console.log('txnType2 ', txnType);
        fnTransferCoin(security,hasMnemonic );
    }
}

function fnCallClaimaleBalancePage(){
    $('#post-form').attr('action', 'ws');
    $('input[name="qs"]').val('porte');
    $('input[name="rules"]').val('view_claimable_balance');
    $("#post-form").submit();	
}
