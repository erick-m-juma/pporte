$(window).on("load", function(e) {
    $("#global-loader").fadeOut("slow");
})

function fnStellarPresent(select){

	if(select ==='Yes'){
		 $('#stellar-present').show();
         $('#stellar-not-present').hide();
		document.getElementById( 'hide_stellar' ).style.display = 'none';
	}else{
		$('#stellar-present').hide();
		$('#stellar-not-present').show();
	}
}
function fmnemoniccodePresent(select){
	if(select ==='Yes'){
		 $('#mnemonic-present').removeClass("hidden");
	 	$('#stellar-account-choice').addClass("hidden");
		$('#mnemoni-not-present').addClass("hidden");
		document.getElementById( 'hide_stellar' ).style.display = 'none';
	}else{
		$('#mnemoni-not-present').removeClass("hidden");
		$('#mnemonic-present').addClass("hidden");
		$('#stellar-account-choice').addClass("hidden");
		document.getElementById( 'hide_stellar' ).style.display = 'none';
	}
}
function fnRegisterMneumonic(){
	$( "#register-mnemonic" ).validate( {
        ignore: [],
        rules: {
            has_account: {
                required: true
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
	if($( "#register-mnemonic" ).valid()){
		console.log("I am getting here")
        	$('#btn-register-stellar-ac').addClass('btn-loading');
			$('#register-mnemonic input[name="qs"]').val('wal');
      		$('#register-mnemonic input[name="rules"]').val('create_mnemocic_code');

    		var formData = new FormData($('#register-mnemonic')[0]);

       		 formData.append("relno",relno);
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
						let str = data.data;
						arr =str.trim().split(" ");
						if(arr.length>0){
							console.log("Array is zero",arr[0])
							
							$('#first_corousel').append(arr[0]);
							$('#second_corousel').append(arr[1]);
							$('#third_corousel').append(arr[2]);
							$('#fourth_corousel').append(arr[3]);
							$('#fifth_corousel').append(arr[4]);
							$('#sixth_corousel').append(arr[5]);
							$('#seventh_corousel').append(arr[6]);
							$('#eighth_corousel').append(arr[7]);
							$('#nineth_corousel').append(arr[8]);
							$('#10th_corousel').append(arr[9]);
							$('#11th_corousel').append(arr[10]);
							$('#12th_corousel').append(arr[11]);
							
							console.log("Array is zero",arr[11])
							$("#mneumonic_card").removeClass("hidden");
							$("#hide_stellar").addClass("hidden");
							$("#btn-register-stellar-ac").prop("disabled", true);
						}else{
							`<p>There is no Mnemonic code generated</p>`
						}
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
	console.log("Inside fnConfirmMneumonic ")
	var first_mnemonic =document.getElementById('first_corousel').innerHTML;
	console.log("first_mnemonic",first_mnemonic)
 	var second_corousel =document.getElementById('second_corousel').innerHTML;
	var third_corousel =document.getElementById('third_corousel').innerHTML;
	var fourth_corousel =document.getElementById('fourth_corousel').innerHTML;
	var fifth_corousel =document.getElementById('fifth_corousel').innerHTML;
	var sixth_corousel =document.getElementById('sixth_corousel').innerHTML;
	var seventh_corousel =document.getElementById('seventh_corousel').innerHTML;
	var eighth_corousel =document.getElementById('eighth_corousel').innerHTML;
	var nineth_corousel =document.getElementById('nineth_corousel').innerHTML;
	var tenth_corousel =document.getElementById('10th_corousel').innerHTML;
	var eleventh_corousel =document.getElementById('11th_corousel').innerHTML;
	var twelfth_corousel =document.getElementById('12th_corousel').innerHTML;
	var arr=first_mnemonic.concat(", ",second_corousel,", ",third_corousel,", ",fourth_corousel,", ",fifth_corousel,", ",sixth_corousel,", ",seventh_corousel,
	", ",eighth_corousel,", ",nineth_corousel,", ",tenth_corousel,", ",eleventh_corousel,", ",twelfth_corousel)
		
function fnConfirmMneumonic(){
		Swal.fire({
		  text: arr,
		  icon: 'info',
		  html:
		    'Please check to confirm that you have copied the codes in a correct sequence.',
		  showCloseButton: true,
		  focusConfirm: false,
		  confirmButtonText:
		    '<i class="fa fa-thumbs-up"></i> Great!',
		  confirmButtonAriaLabel: 'Thumbs up, great!'
		}).then(function() {
				$("#div_btn_reg_stellar").removeClass("hidden");
				$("input[name='saved_mneumonic']:checked").val()==="true";  
         });
}

function fnRegisterStellarAccount(){
	$( "#register-stellar" ).validate( {
        ignore: [],
        rules: {
			
            saved_mneumonic: {
               required:true,
            },
            password: {
                required:true
            },
           
        },
        messages: {
          
            saved_mneumonic: {
                required: 'Select the checkbox before you proceed',
            },          
            password: {
                required: 'Please enter your password',
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
        $('#btn_register_stellar').addClass('btn-loading');
             $('#register-stellar input[name="qs"]').val('wal');
      		 $('#register-stellar input[name="rules"]').val('create_stellar_account');
       		 var formData = new FormData($('#register-stellar')[0]);
            formData.append("relno",relno);
			formData.append('arr', arr);
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
                    $('#btn_register_stellar').removeClass('btn-loading');
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
	                        $('input[name="qs"]').val('wal');
	                        $('input[name="rules"]').val('Create Wallet');
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
(function($) {
		"use strict";
		// Toolbar extra buttons
		var btnFinish = $('<button></button>').text('Finish')
			.addClass('btn btn-success')
			.on('click', function(){
				//alert("Submit clicked")
				fnSubmitForm(); 
				return false;
				
			});
		var btnCancel = $('<button></button>').text('Cancel')
			.addClass('btn btn-danger')
			.on('click', function(){ $('#smartwizard').smartWizard("reset"); });
	
	
		// Smart Wizard
		$('#smartwizard').smartWizard({
				selected: 0,
				theme: 'default',
				transitionEffect:'fade',
				showStepURLhash: true,
				toolbarSettings: {
								  toolbarButtonPosition: 'end',
								  toolbarExtraButtons: [btnFinish, btnCancel]
								}
		});
			
		// Arrows Smart Wizard 1
		$('#smartwizard-1').smartWizard({
				selected: 0,
				theme: 'arrows',
				transitionEffect:'fade',
				showStepURLhash: false,
				toolbarSettings: {
								  toolbarExtraButtons: [btnFinish, btnCancel]
								}
		});
				
		// Circles Smart Wizard 1
		$('#smartwizard-2').smartWizard({
				selected: 0,
				theme: 'circles',
				transitionEffect:'fade',
				showStepURLhash: false,
				toolbarSettings: {
								  toolbarExtraButtons: [btnFinish, btnCancel]
								}
		});
				
		// Dots Smart Wizard 1
		$('#smartwizard-3').smartWizard({
				selected: 0,
				theme: 'dots',
				transitionEffect:'fade',
				showStepURLhash: false,
				toolbarSettings: {
								  toolbarExtraButtons: [btnFinish, btnCancel]
								}
		});
		
	})(jQuery);
	function fnSubmitForm(){
	  //Check for the data validation	
	  //validate data
	 
 	$( "#register-has-account" ).validate( {
		rules: {
			first_code: {
				required: true
			},
			second_code: {
				required: true,
			},
			third_code: {
				required: true,
			}, 
			fourth_code: {
				required: true,
			},
			fifth_code: {
				required: true,
			},
			sixth_code: {
				required: true,
			},
			seventh_code: {
				required: true
			},
			eight_code: {
				required: true
			},
			nineth_code: {
				required: true
			},
			tenth_code: {
				required: true
			},
			eleventh_code: {
				required: true
			},
			twelve_code: {
				required: true
			},
			 has_mnemonic: {
                required: true
            },
			pwsd:{
				required: true
			},
			input_public_key:{
				required: true
			}
		},
		messages: {
			first_code: {
				required: 'Please enter mnemonic code',
			},
			second_code: {
				required: 'Please enter mnemonic code',
			},
			third_code: {
				required: 'Please enter mnemonic code',
			},
			fourth_code: {
				required: 'Please enter mnemonic code',
			},
			fifth_code: {
				required: 'Please enter mnemonic code',
			},
			sixth_code: {
				required: 'Please enter mnemonic code',
			},
			seventh_code: {
				required: 'Please enter mnemonic code',
			},
			eight_code: {
				required: 'Please enter mnemonic code',
			},
			nineth_code: {
				required: 'Please enter mnemonic code',
			},
			tenth_code: {
				required: 'Please enter mnemonic code',
			},
			eleventh_code: {
				required: 'Please enter mnemonic code',
			},
			twelve_code: {
				required: 'Please enter mnemonic code',
			},
			has_mnemonic: {
                required: 'Select one of these',
            },
			pwsd:{
				required: 'Please enter password',
			},
			input_public_key:{
				required: 'Please enter public key',
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

	 if($( "#register-has-account" ).valid()){
	  			$('#register-has-account input[name="qs"]').val('wal');
				  $('#register-has-account input[name="rules"]').val('cust_with_stellar_acc');
					  var formData = new FormData($('#register-has-account')[0]);
					  formData.append("relno",relno);
					 for (var pair of formData.entries()) {
						  console.log(pair[0] + " - " + pair[1]);
						}		
				  // Call Ajax here and submit the form 							
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
									   if(data.error=='false'){
									//       console.log("no error");
											  Swal.fire({
													   icon: 'success',
                            						   text:  data.message,
													   showConfirmButton: true,
													   confirmButtonText: "Ok",
													   closeOnConfirm: true,
												   }).then(function(){
													$('#register-has-account').attr('action', 'ws');
							                        $('input[name="qs"]').val('wal');
							                        $('input[name="rules"]').val('Create Wallet');
							                        $("#register-has-account").submit();
												})
										   }else{
										    Swal.fire({
				                            text: data.message, 
				                            icon: "error",
				                            showConfirmButton: true,
				                            confirmButtonText: "Ok",
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
											  closeOnConfirm: true,
											  }).then(function() {
												 
									  });
							  }
						  });
				  }else{
					  Swal.fire({
								  icon: 'error',
								  title: 'Oops',
								  text: 'Please fill required fields',
								  showConfirmButton: true,
								  confirmButtonText: "Ok",
								  closeOnConfirm: true,
								  }).then(function() {
									 
						  });
					  return false;
				  }
		
	
}

function fnNoMnemonicRegisterStellarAccount(){
	$( "#has_no_mnenmonic_code" ).validate( {
        ignore: [],
        rules: {
			has_account: {
                required: true
            },
            nomnemonic_input_public_key: {
               required:true,
            },
            nomnemonicpwsd: {
                required:true
            },
           
        },
        messages: {
            has_account: {
                required: 'Select one of these',
            },
            nomnemonic_input_public_key: {
                required: 'Please enter public key',
            },          
            nomnemonicpwsd: {
                required: 'Please enter your password',
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

    if($( "#has_no_mnenmonic_code" ).valid()){
        $('#no_mnemonic_btn_register_stellar').addClass('btn-loading');
             $('#has_no_mnenmonic_code input[name="qs"]').val('wal');
      		 $('#has_no_mnenmonic_code input[name="rules"]').val('nomnemonic_stellar_account');
       		 var formData = new FormData($('#has_no_mnenmonic_code')[0]);
            formData.append("relno",relno);
			formData.append('arr', arr);
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
                    $('#no_mnemonic_btn_register_stellar').removeClass('btn-loading');
                    var data = JSON.parse(result);
                    console.log('data ',data);
                    if(data.error=='false'){
                        Swal.fire({
                            icon: 'success',
                            text:  data.message,
                            showConfirmButton: true,
                            confirmButtonText: "Ok",
                        }).then(function() {
                           $('#has_no_mnenmonic_code').attr('action', 'ws');
	                        $('input[name="qs"]').val('wal');
	                        $('input[name="rules"]').val('Create Wallet');
	                        $("#has_no_mnenmonic_code").submit();
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