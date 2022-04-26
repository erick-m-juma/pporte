$(window).on("load", function(e) {
    $("#global-loader").fadeOut("slow");
})

function shortenPublicKey(pkey) {
  return pkey.substr(0, 3) + '...' + pkey.substr(53, pkey.length);
}
var nextLink ='';
var prevLink ='';
function fnNextPage(link){
	let linkToSend="";
	 if (nextLink === "" ||nextLink === undefined){
		linkToSend=link;
	  }else{
		linkToSend=nextLink;
	}
	
	console.log("link to send is "+linkToSend);
    $('#get-txn-form input[name="qs"]').val('porte');
    $('#get-txn-form input[name="rules"]').val('get_cust_next_transactions');
    $('#get-txn-form input[name="link"]').val(linkToSend);
    
    var formData = new FormData($('#get-txn-form')[0]);
		formData.append("publickey",pubKey);	
		console.log("publickey next",pubKey);	
  
    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader('x-api-key' , getAPIKey());
        }
    });
		$("#prev_btn").removeClass("disabled");
		$("#next_btn").addClass("btn-loadding");
		$('#spinner-div').show();//Load button clicked show spinner
    $.ajax({
        url: 'ms',
        data: formData,
        processData: false,
        contentType: false,
        type: 'POST',
        success: function (result) {
		$("#next_btn").removeClass("remove-loadding");
	    $('#spinner-div').hide();//Request is complete so hide spinner
            //wallet_txn
            var data = JSON.parse(result);
            var htmlTable ='';
            
            var txnList = data.data;
			console.table(txnList);
            $('#htmltable').html('');
  
            if(data.error=='false'){
                htmlTable+=`<table id="stellar_txns" class="table table-striped table-bordered text-nowrap" >
												<thead>
													<tr>
														<th>Operation Id</th>
														<th>Time</th>
														<th>Transaction Type</th>
														<th>Source account</th>
														<th>Destination account</th>
														<th>Amount </th>
														<th>Transaction Fee</th>
													</tr>
												</thead>
												<tbody>`;
                if(txnList.length>0){
                    for (i=0; i<txnList.length;i++){
                        htmlTable+=`<tr>
                                        <td>`+txnList[i].operationId+`</td>
                                        <td>`+txnList[i].createdOn+`</td>
                                        <td>`+txnList[i].type+`</td>`;
										if( txnList[i].type==="payment") {
										 htmlTable+=`
													<td>`+shortenPublicKey(txnList[i].fromAccount)+`</td>
													<td>`+shortenPublicKey(txnList[i].toAccount)+`</td>
													<td>`+txnList[i].amount+` `+txnList[i].assetCode+`</td>`;
										}else if(txnList[i].type==="manage_sell_offer"){
											htmlTable+=`
													<td>`+txnList[i].sellingAsset+`</td>
													<td>`+txnList[i].buyingAsset+`</td>
													<td>`+txnList[i].offerPrice+`</td>`;
										}else if(txnList[i].type==="manage_buy_offer"){
											htmlTable+=`
													<td>`+txnList[i].sellingAsset+`</td>
													<td>`+txnList[i].buyingAsset+`</td>
													<td>`+txnList[i].offerPrice+`</td>`;
										}else if(txnList[i].type==="change_trust"){
											htmlTable+=`
													<td>`+shortenPublicKey(txnList[i].trustee)+`</td>
													<td>`+shortenPublicKey(txnList[i].trustor)+`</td>
													<td>`+txnList[i].assetCode+`</td>`;
										}else if(txnList[i].type==="create_account"){
											htmlTable+=`
													<td></td>
													<td></td>
													<td></td>`;
										}else if(txnList[i].type==="path_payment_strict_send"){
											htmlTable+=`
													<td>`+shortenPublicKey(txnList[i].fromAccount)+`</td>
													<td>`+shortenPublicKey(txnList[i].toAccount)+`</td>
													<td>`+txnList[i].amount+` `+txnList[i].assetCode+`</td>`;
										}else if(txnList[i].type==="path_payment_strict_receive"){
											htmlTable+=`
													<td>`+shortenPublicKey(txnList[i].fromAccount)+`</td>
													<td>`+shortenPublicKey(txnList[i].toAccount)+`</td>
													<td>`+txnList[i].amount+` `+txnList[i].assetCode+`</td>`;
										}else if(txnList[i].type==="create_claimable_balance"){
											htmlTable+=`
													<td>`+shortenPublicKey(txnList[i].fromAccount)+`</td>
													<td></td>
													<td>`+txnList[i].amount+` `+txnList[i].assetCode+`</td>`;
										}else if(txnList[i].type==="claim_claimable_balance"){
											htmlTable+=`
													<td>`+shortenPublicKey(txnList[i].fromAccount)+`</td>
													<td>`+txnList[i].toAccount+`</td>
													<td></td>`;
										}else if(txnList[i].type==="Claimable Balance to Claim"){
											htmlTable+=`
													<td>`+shortenPublicKey(txnList[i].fromAccount)+`</td>
													<td></td>
													<td>`+txnList[i].amount+` `+txnList[i].assetCode+`</td>`;
										}
								htmlTable+=`<td>`+txnList[i].feeCharged+` Stroops</td>
								</tr>`;
								
								nextLink=txnList[i].nextLink;
								prevLink=txnList[i].previousLink;
                    }
                }else{
                    htmlTable=`<tr><td> <span>No data Present</span></td></tr>`
                }
                htmlTable+=`</tbody></table>`;
                $('#htmltable').append(htmlTable);
               
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
function fnPreviousPage(previouslink){
		let prevlinkToSend="";
	 if (prevLink === "" ||prevLink === undefined){
		prevlinkToSend=previouslink;
	  }else{
		prevlinkToSend=prevLink;
	}
	
	
		console.log("Previous to send is "+prevlinkToSend);

    $('#get-txn-form input[name="qs"]').val('porte');
    $('#get-txn-form input[name="rules"]').val('get_cust_prev_transactions');
    $('#get-txn-form input[name="link"]').val(prevlinkToSend);
    
    var formData = new FormData($('#get-txn-form')[0]);
		formData.append("publickey",pubKey)	;
		console.log("pubkey "+pubKey);

    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader('x-api-key' , getAPIKey());
        }
    });
		
		$("#prev_btn").addClass("btn-loadding");
		$('#spinner-div').show();//Load button clicked show spinner 
    $.ajax({
        url: 'ms',
        data: formData,
        processData: false,
        contentType: false,
        type: 'POST',
        success: function (result) {
		$("#prev_btn").removeClass("remove-loadding");
		$('#spinner-div').hide();//Request is complete so hide spinner
            //wallet_txn
            var data = JSON.parse(result);
            var htmlTable ='';
            
            var txnList = data.data;
			console.table(txnList);
            $('#htmltable').html('');
  
            if(data.error=='false'){
                htmlTable+=`<table id="stellar_txns" class="table table-striped table-bordered text-nowrap" >
												<thead>
													<tr>
														<th>Operation Id</th>
														<th>Time</th>
														<th>Transaction Type</th>
														<th>Source account</th>
														<th>Destination account</th>
														<th>Amount </th>
														<th>Transaction Fee</th>
													</tr>
												</thead>
												<tbody>`;
                if(txnList.length>0){
                    for (i=0; i<txnList.length;i++){
                        htmlTable+=`<tr>
                                        <td>`+txnList[i].operationId+`</td>
                                        <td>`+txnList[i].createdOn+`</td>
                                        <td>`+txnList[i].type+`</td>`;
										if( txnList[i].type==="payment") {
										 htmlTable+=`
													<td>`+shortenPublicKey(txnList[i].fromAccount)+`</td>
													<td>`+shortenPublicKey(txnList[i].toAccount)+`</td>
													<td>`+txnList[i].amount+` `+txnList[i].assetCode+`</td>`;
										}else if(txnList[i].type==="manage_sell_offer"){
											htmlTable+=`
													<td>`+txnList[i].sellingAsset+`</td>
													<td>`+txnList[i].buyingAsset+`</td>
													<td>`+txnList[i].offerPrice+`</td>`;
										}else if(txnList[i].type==="manage_buy_offer"){
											htmlTable+=`
													<td>`+txnList[i].sellingAsset+`</td>
													<td>`+txnList[i].buyingAsset+`</td>
													<td>`+txnList[i].offerPrice+`</td>`;
										}else if(txnList[i].type==="change_trust"){
											htmlTable+=`
													<td>`+shortenPublicKey(txnList[i].trustee)+`</td>
													<td>`+shortenPublicKey(txnList[i].trustor)+`</td>
													<td>`+txnList[i].assetCode+`</td>`;
										}else if(txnList[i].type==="create_account"){
											htmlTable+=`
													<td></td>
													<td></td>
													<td></td>`;
										}else if(txnList[i].type==="path_payment_strict_send"){
											htmlTable+=`
													<td>`+shortenPublicKey(txnList[i].fromAccount)+`</td>
													<td>`+shortenPublicKey(txnList[i].toAccount)+`</td>
													<td>`+txnList[i].amount+` `+txnList[i].assetCode+`</td>`;
										}else if(txnList[i].type==="path_payment_strict_receive"){
											htmlTable+=`
													<td>`+shortenPublicKey(txnList[i].fromAccount)+`</td>
													<td>`+shortenPublicKey(txnList[i].toAccount)+`</td>
													<td>`+txnList[i].amount+` `+txnList[i].assetCode+`</td>`;
										}else if(txnList[i].type==="create_claimable_balance"){
											htmlTable+=`
													<td>`+shortenPublicKey(txnList[i].fromAccount)+`</td>
													<td></td>
													<td>`+txnList[i].amount+` `+txnList[i].assetCode+`</td>`;
										}else if(txnList[i].type==="claim_claimable_balance"){
											htmlTable+=`
													<td>`+shortenPublicKey(txnList[i].fromAccount)+`</td>
													<td>`+shortenPublicKey(txnList[i].toAccount)+`</td>
													<td></td>`;
										}else if(txnList[i].type==="Claimable Balance to Claim"){
											htmlTable+=`
													<td>`+shortenPublicKey(txnList[i].fromAccount)+`</td>
													<td></td>
													<td>`+txnList[i].amount+` `+txnList[i].assetCode+`</td>`;
										}
								htmlTable+=`<td>`+txnList[i].feeCharged+` Stroops</td>
								</tr>`;
								
								nextLink=txnList[i].nextLink;
								prevLink=txnList[i].previousLink;
                    }
                }else{
                    htmlTable=`<tr><td> <span>No data Present</span></td></tr>`
                }
                htmlTable+=`</tbody></table>`;
                $('#htmltable').append(htmlTable);
               
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

