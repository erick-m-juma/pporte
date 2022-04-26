$("button[data-dismiss=modal]").click(function(){
	$(".modal").modal('hide');
});

$(window).on("load", function(e) {
	$("#global-loader").fadeOut("slow");
})


function checkIfUserHasMneonicCode(claimableBalaceId, assetCode, assetIssuer){
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
                    fnPassword(data.hasmnemonic, claimableBalaceId, assetCode, assetIssuer);
                }else{
                    fnPrivateKey(data.hasmnemonic, claimableBalaceId, assetCode, assetIssuer)
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

function fnPassword(hasMnemonic, claimableBalaceId, assetCode, assetIssuer){
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
				fnClaimClaimableBalance(passwordVal,hasMnemonic, claimableBalaceId, assetCode, assetIssuer );
			}
		});
}

function fnPrivateKey(hasMnemonic, claimableBalaceId, assetCode, assetIssuer){
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
				fnClaimClaimableBalance(privatekey,hasMnemonic, claimableBalaceId, assetCode, assetIssuer );
			}
		});
}

function fnClaimClaimableBalance (security,hasMnemonic, claimableBalaceId, assetCode, assetIssuer){
	var formData = new FormData();
	formData.append('qs','porte');
	formData.append('rules','claim_claimable_balance');
	formData.append('security',security);
	formData.append('hasMnemonic',hasMnemonic);
	formData.append('claimableBalaceId',claimableBalaceId);
	formData.append('assetCode',assetCode);
	formData.append('assetIssuer',assetIssuer);
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
            var data = JSON.parse(result);
            if(data.error=='false'){
              Swal.fire({
						icon: 'success',
						title: 'Success',
						text: data.message,
						showConfirmButton: true,
						confirmButtonText: "Ok",
					}).then(function() {
						$('#get-page-form').attr('action', 'ws');
						$('#get-page-form input[name="qs"]').val('porte');
						$('#get-page-form input[name="rules"]').val('view_claimable_balance');
						$("#get-page-form").submit();
					});
               
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