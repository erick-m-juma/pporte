$(window).on("load", function(e) {
    $("#global-loader").fadeOut("slow");
})
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
                var porteDetails = ''
                
				
				var htmlOptions = '';
				$('#div_porte_coin').html('');
				if(data.error="false"){
                    porteDetails=data.data;	
				console.log(porteDetails);
					if(porteDetails.length>0){
						for (i=0; i<porteDetails.length;i++){
							if(porteDetails[i].walletType === "P"){
								htmlOptions+=`<div class="col-sm-12 col-md-3 col-lg-3 col-xl-3 card-wallet porte">
								<div class="card-body ">
									<div class="wallet-balance-ico"style="position: static;text-align: center;margin: 0px auto 15px;width: 60px;height: 60px;">
										<img src="assets/images/crypto/porte.png" alt="Ethereum" height="55" width="55">
									</div>
									<h3>`+porteDetails[i].assetDescription+`</h3>
									<h4>`+porteDetails[i].currentBalance+` `+porteDetails[i].assetCode+`</h4>
									<div class="my-wallet-address">
										<span>`+porteDetails[i].walletId+`</span> 
									</div>
								</div>
							</div>`;
							}else if(porteDetails[i].walletType === "V"){
								htmlOptions+=`<div class="col-sm-12 col-md-3 col-lg-3 col-xl-3 card-wallet vessel">
								<div class="card-body ">
									<div class="wallet-balance-ico"style="position: static;text-align: center;margin: 0px auto 15px;width: 60px;height: 60px;" >
										<img src="assets/images/crypto/stable.png" alt="Stable" height="55" width="55">
									</div>
									<h3>`+porteDetails[i].assetDescription+`</h3>
									<h4>`+porteDetails[i].currentBalance+` `+porteDetails[i].assetCode+`</h4>
									<div class="my-wallet-address">
										<span>`+porteDetails[i].walletId+`</span> 
									</div>
								</div>
							</div>`;	
							}
							
						}
				   }else{
						/* Swal.fire({
							icon: 'info',
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
                                
						}); */
                        htmlTable=`<div><p> <span>No data Present</span></p></div>`
				   } 
				   $('#div_porte_coin').append(htmlOptions);
                 

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

$( document ).ready(function() {
    fnGetlastFiveTxn ();
});

function fnGetlastFiveTxn(){

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
            var txnList ='';
            txnList = data.data;
            $('#div_transactions').html('');
            console.log('data ',data);
            if(data.error=='false'){
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
