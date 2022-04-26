i18next.init({
      lng: 'en',
      fallbackLng: 'en',
      debug: false,
      resources: {
        en: {
          translation: {
			  /* Nav Pages start */
            "idnav_UserSettings" : "User Settings", 
			"idnav_UpdateProfile" : "Update Profile",
			"idnav_Logout" : "Logout",

			/* start Page specific changes */
          }
        },
        es: {
			/* Nav Pages start */
            translation: {
            "idnav_UserSettings" : "Ajustes de usuario", 
			"idnav_UpdateProfile" : "Actualizaci�n del perfil",
			"idnav_Logout" : "Cerrar sesi�n",

			/* start Page specific changes */

          }
          }
      }
    }, function(err, t) {
         updateContent();
    });

        function updateContent() {
			/* Nav Pages start */
            $('#idnav_UserSettings').text(i18next.t('idnav_UserSettings')); 
			$('#idnav_UpdateProfile').text(i18next.t('idnav_UpdateProfile'));
			$('#idnav_Logout').text(i18next.t('idnav_Logout'));

			/* end Page specific changes */

      }
         
		function fnChangePageLang(lng){
			//alert ('inside navpage :' +lng)
			i18next.changeLanguage(lng, fnChangeLanguage(lng))
		}

		i18next.on('languageChanged', function(lng) {
		  updateContent(lng);
		});
               
        function fnChangeLanguage(lang){
            if(lang=='en' )  $('#lang_def').text('EN') 
          	else if(lang=='es')  $('#lang_def').text('ES')
			$('input[name="hdnlang"]').val(lang);

            //$('#hdnlangpref1,#hdnlangpref2,#hdnlangpref3').val(lang);
        }

		$( function() {
			$( '#cd-dropdown' ).dropdown( {
				gutter : 5,
				stack : false,
				delay : 100,
				slidingIn : 100
			} );
		});
				
		function fnUpdateParams(ac) {
			$('input[name="hdnassetcoincode"]').val(ac);
			//alert('here 4 ' + $('input[name="hdnassetcoincode"]').val());
		}
		
		$(function () {
		  $("select").change(function () {
		    $("img").attr("src", "https://theme.bitrixinfotech.com/crypto-wallet/assets/images/btc.png" + this.value + "?text=" + $(this).find("option:selected").text());
		  });
		});
			
			
$('#btn-regcoin').click(function() {
	//Check for the data validation	
	$("#regcryptocoin-form").validate({
		rules: {
			selcryptocoin: {
				required: true,
			},
			asssetdesc: {
				required: true,
			}

		},
		messages: {
			selcryptocoin: {
				required: 'Please select the Asset Coin ',
			},
			asssetdesc: {
				required: 'Please enter the Asset Description',
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

	if ($("#regcryptocoin-form").valid()) {
		$('input[name="qs"]').val('cryp');
		$('input[name="rules"]').val('custregistercoin');
		//$('input[name="hdnassetcoincode"]').val($('.selcryptocoin :selected').val());

		//alert(' hdnassetcoincode send is '+ $('input[name="hdnassetcoincode"]').val())
		//console.log('rule is ' + $('input[name="rules"]').val())
		//set required attribute on input to true
		//$('input').attr('data-parsley-required', 'true');

		//$("#editmerchuser-form").submit();

		var formData = new FormData($('#regcryptocoin-form')[0]);
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
						//location.reload();
						$('#regcryptocoin-form').attr('action', 'ws');
						$('#regcryptocoin-form input[name="qs"]').val('cryp');
						$('#regcryptocoin-form input[name="rules"]').val('View Coins');
						$("#regcryptocoin-form").submit();
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


function getCustomerCryptoAssets(){
	console.log("getCustomerCryptoAssets");
	/* $('post_data input[name="qs"]').val('wal');
	$('post_data input[name="rules"]').val('get_wallet_coin_details');
	
	var form = $('#post_data')[0]; */
    var formData = new FormData();
	formData.append('qs','wal');
	formData.append('rules', 'get_wallet_coin_details');

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
			if (result) {
				var data = JSON.parse(result);
				var listCryptoAssets = data.data;
				var htmlOptions = '';
				$('#wallet_assets_div').html('');
                //htmlOptions=`<option value="" disabled selected>Select Asset</option>`;
				 if(listCryptoAssets.length>0){
					 for (i=0; i<listCryptoAssets.length;i++){
                        // htmlOptions+=`<option value="`+ listCryptoAssets[i].assetCode+`" >`+listCryptoAssets[i].assetCode+`</option>`;
						if(listCryptoAssets[i].assetCode === 'PORTE'){
                            htmlOptions+=`<div class="col-3">
                                            <label>
                                                <input type="radio" name="asset_value" value="`+listCryptoAssets[i].assetCode+`">
                                                <img src="assets/images/crypto/porte.png" alt="`+listCryptoAssets[i].assetCode+`" height="40" width="40" >
                                                <p>`+listCryptoAssets[i].assetCode+`</p>
                                            </label>
                                     </div>`;

                        }else if(listCryptoAssets[i].assetCode === 'VESL'){
                            htmlOptions+=`<div class="col-3">
                                            <label>
                                                <input type="radio" name="asset_value" value="`+listCryptoAssets[i].assetCode+`">
                                                <img src="assets/images/crypto/vessel.jpg" alt="`+listCryptoAssets[i].assetCode+`" height="40" width="40" >
                                                <p>`+listCryptoAssets[i].assetCode+`</p>
                                            </label>
                                     </div>`;

                        }else if(listCryptoAssets[i].assetCode === 'XLM'){
                            htmlOptions+=`<div class="col-3">
                                            <label>
                                                <input type="radio" name="asset_value" value="`+listCryptoAssets[i].assetCode+`" >
                                                <img src="assets/images/crypto/xlm.svg" alt="`+listCryptoAssets[i].assetCode+`" height="40" width="40" >
                                                <p>`+listCryptoAssets[i].assetCode+`</p>
                                            </label>
                                     </div>`;
                        }else if(listCryptoAssets[i].assetCode === 'USDC'){
                            htmlOptions+=`<div class="col-3">
                                            <label>
                                                <input type="radio" name="asset_value" value="`+listCryptoAssets[i].assetCode+`">
                                                <img src="assets/images/crypto/usdc.png" alt="`+listCryptoAssets[i].assetCode+`" height="40" width="40" >
                                                <p>`+listCryptoAssets[i].assetCode+`</p>
                                            </label>
                                     </div>`;
                        }else if(listCryptoAssets[i].assetCode === 'BTCX'){
                            htmlOptions+=`<div class="col-3">
                                            <label>
                                                <input type="radio" name="asset_value" value="`+listCryptoAssets[i].assetCode+`">
                                                <img src="assets/images/crypto/bitcoin.svg" alt="`+listCryptoAssets[i].assetCode+`" height="40" width="40" >
                                                <p>`+listCryptoAssets[i].assetCode+`</p>
                                            </label>
                                     </div>`;
                        }
						}
				}else{
					htmlOptions ='<div>You have already assets all assets </div>';
				} 
				$('#wallet_assets_div').append(htmlOptions);
			}
			},
			error: function() {
			
		}
	}); 

}

function fnCreateWallet(security, hasMnemonic){
	    $( "#create-wal-form" ).validate( {
        rules: {
            input_wallet_desc: {
                required: true
            },
            asset_value: {
                required: true
            }
        },
        messages: {
            input_wallet_desc: {
                required: 'Wallet description is required',
                minlength: 'Description must be atleast 10 characters'
            },
            asset_value: {
                required: 'Asset is required'
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
    if($( "#create-wal-form" ).valid()){
		
		$('#btn_create_wal').addClass('btn-loading');
        $('#create-wal-form input[name="qs"]').val('wal');
        $('#create-wal-form input[name="rules"]').val('create_porte_coin_wallets');
        
        var formData = new FormData($('#create-wal-form')[0]);
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
                
                var data = JSON.parse(result);
				$('#btn_create_wal').removeClass('btn-loading');
                //console.log('data ',data);
                if(data.error=='false'){
                    Swal.fire({
                        icon: 'success',
                        text: data.message,
                        showConfirmButton: true,
                        confirmButtonText: "Ok",
                    }).then(function() {
                        $('#post-form').attr('action', 'ws');
                        $('input[name="qs"]').val('porte');
                        $('input[name="rules"]').val('Show Coins');
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


function checkIfUserHasMneonicCodeCreateWal(){
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
                    fnPasswordCreateWal(data.hasmnemonic);
                }else{
                    fnPrivateKeyCreateWal(data.hasmnemonic)
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

function fnPasswordCreateWal(hasMnemonic){
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
				fnCreateWallet(passwordVal,hasMnemonic );
			}
		});
}

function fnPrivateKeyCreateWal(hasMnemonic){
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
				fnCreateWallet(privatekey,hasMnemonic );
			}
		});
}

// A $( document ).ready() block.
$( document ).ready(function() {
    getCustomerCryptoAssets();
});



