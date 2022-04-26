<%@ page language="java" contentType="text/html; charset=ISO-8859-1"    pageEncoding="ISO-8859-1" errorPage="error.jsp"%>
<%@ page import="com.pporte.*,com.pporte.model.*, org.apache.commons.lang3.StringUtils,  java.util.concurrent.*, java.util.*"%>
<!doctype html>
<html lang="en" dir="ltr">
	<head>
        <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<!-- Favicon -->
		<link rel="icon" href="assets/images/brand/pporte.png" type="image/x-icon"/>

		<!-- Title -->
		<title>Create Claimable Balances</title>

		<!--Bootstrap.min css-->
		<link rel="stylesheet" href="assets/plugins/bootstrap/css/bootstrap.min.css">

		<!-- Dashboard css -->
		<link href="assets/css/style.css" rel="stylesheet" />

		<!-- Custom scroll bar css-->
		<link href="assets/plugins/scroll-bar/jquery.mCustomScrollbar.css" rel="stylesheet" />

		<!-- Sidemenu css -->
		<link href="assets/plugins/toggle-sidebar/sidemenu.css" rel="stylesheet" />

		<!-- Sidebar Accordions css -->
		<link href="assets/plugins/accordion1/css/easy-responsive-tabs.css" rel="stylesheet">

		<!-- Rightsidebar css -->
		<link href="assets/plugins/sidebar/sidebar.css" rel="stylesheet">

		<!---Sweetalert2 css-->
        <link href="assets/plugins/sweetalert2/sweetalert2.css" rel="stylesheet" />

		<!---Font icons css-->
		<link href="assets/plugins/iconfonts/plugin.css" rel="stylesheet" />
		<link href="assets/plugins/iconfonts/icons.css" rel="stylesheet" />
		<link  href="assets/fonts/fonts/font-awesome.min.css" rel="stylesheet">
		 <!-- Custom css -->
		<link href="assets/css/app.css" rel="stylesheet" />
		 <!-- Custom css -->
		<link href="assets/css/style2.css" rel="stylesheet" />
	     <!-- Custom css -->
	    <link href="assets/css/style5.css" rel="stylesheet">
 		<!-- Responsive css -->
		<link href="assets/css/responsive.css" rel="stylesheet" />
	</head>

	<body class="app sidebar-mini rtl" style="position: relative; min-height: 100%; top: 0px;"  onload="fnGetCoinDetails()">
	  <style>
         .wallet-transaction{
            margin: 0px 30px;
         }
         @media screen and (max-width: 767px){
            .wallet-transaction{
               margin: 30px 0px 0px !important;
            }
         }
         @media screen and (max-width: 1199px) and (min-width: 768px){
            .tranfer-coin-box .wallet-transaction{
               margin-top: 30px;
            }
         }
      </style>

		<!--Global-Loader-->
		<div id="global-loader">
			<img src="assets/images/icons/loader.svg" alt="loader">
		</div>

		<div class="page">
			<div class="page-main">
				<!--app-header and sidebar-->
				    <jsp:include page="ops_topandleftmenu.jsp" />

                <!-- app-content-->
				<div class="app-content  my-3 my-md-5 toggle-content">
					<div class="side-app" style="height:89vh;">

						<!-- page-header -->
						<div class="page-header" style="margin:auto">
							<ol class="breadcrumb"><!-- breadcrumb -->
								<li class="breadcrumb-item"><a href="#">Manage Digital Assets</a></li>
								<li class="breadcrumb-item active" aria-current="page">Create Claimable Balances</li>
							</ol><!-- End breadcrumb --> 
						</div>
						<!-- End page-header -->
									<!-- page -->
									<div class="page">

										<!-- page-content -->
										<div class="page-content">
										 	<div class="wallet-bradcrumb">
					                           <h2><i class="fa fa-coins"></i> Create Claimable Balances</h2>
					                        </div>
											<div class="tranfer-coin-box" style="margin-top: 0px">
					                           <div class="row ">
					                              <div class="col-xl-7">
					                                 <div class="transfercoin-left-box">
					                                     <div class="transfer-coin-content">
					                                       <div class="transfer-coin-content-box active" id="sent-coin" style="margin-top: 0%;">
					                                       	 <form id="transfer-coin-form"  method="post" >
					                                          <div class="transfer-coin-input transfer-coin-select clearfix">
																<div class="dropdown" style="width: 94%;">
																	<label><span id="sp_from_id">From</span> </label>
																	<select class="form-control"  name="sender_asset" id ="sender_asset" onChange="javascript: fnUpdatesenderparams(); return false">
																
																	</select>
																</div>   																
					                                          </div>
					                                         
					                                          <div class="transfer-coin-input">
					                                             <label>Claimant</label>
					                                             <input type="" name="input_receiver" id="input_receiver" value="" placeholder="Enter Receiver Public Key">
					                                          </div>
					                                           <!-- <div class="transfer-coin-input">
					                                             <label>Private Key</label>
					                                             <input type="password" name="input_private_key" id="input_private_key" value="" placeholder="Enter Your Private Key">
					                                          </div> -->
					                                          <div class="transfer-coin-input">
					                                             <label>Amount to send</label>
					                                             <div class="input-two clearfix">
					                                                <div class="input-two-box" style="width: 93%;">
					                                                   <input type="" name="sendamount" id="sendamount" value="" placeholder="">
					                                                   <span id="spansendcode">USD</span>
					                                                </div>
					                                                <!-- <i class="fa fa-exchange"></i>
					                                                <div class="input-two-box">
					                                                   <input type="" name="receivedamount" id= "receivedamount"value="" placeholder="">
					                                                   <span id="spanreceivedcode">BTC</span>
					                                                </div> -->
					                                             </div>
					                                          </div>
					                                          <div class="transfer-coin-input">
					                                             <label>Comment</label>
					                                             <input type="" name="commet" id="commet" value="" placeholder="Enter Comment">
					                                          </div>
					                                          
					                                             <input type="hidden" name="qs" value="">
					                        					 <input type="hidden" name="rules" value="">
						                                       </form>	
					                                          <div class="transfer-coin-button">
					                                             <button class="theme-btn" id="btn-coinspayanyone" name="btn-coinspayanyone" onclick="javascript:fnPrivateKey()">Send Coin</button>
					                                          </div>
					                                       </div>
					                                      </div>
					                                       
				                                        				                                       
					                                 </div>
					                              </div>
					                              
					                              <div class="col-xl-5 hidden" >
					                                 <div class="wallet-transaction" id="div_transactions">
					                                  
					                                 </div>
					                              </div> 
					                              
					                           </div>
					                        </div>
											</div>
										</div>


					</div>
				</div>
				<!-- End app-content-->

			</div>
			
		</div>
		<form method = "post" id="post-form">
			<input type="hidden" name="qs" value="">
			<input type="hidden" name="rules" value="">
		</form>
		
		<div id="error-msg-cust-crypt-pay-anyone-page" style="display: none;">
			<span id="pay-anyone-data-validation-error-from">Please select the Coin</span>
			<span id="pay-anyone-data-validation-error-sendto">Please enter the receiver Wallet Id</span>
			<span id="pay-anyone-data-validation-error-amounttosend">Please enter Amount to send</span>
		</div>
		<!-- End Page -->

		<!-- Back to top -->
		<a href="#top" id="back-to-top"><i class="fa fa-angle-up"></i></a>
		<!-- Jquery js-->
		<script src="assets/js/vendors/jquery-3.2.1.min.js"></script>
		<!--Bootstrap.min js-->
		<script src="assets/plugins/bootstrap/popper.min.js"></script>
		<script src="assets/plugins/bootstrap/js/bootstrap.min.js"></script>

		<!--Side-menu js-->
		<script src="assets/plugins/toggle-sidebar/sidemenu.js"></script>
		
				<!-- i18next js-->
        <script src="assets/plugins/i18next/i18next.min.js"></script>

		<!-- Sidebar Accordions js -->
		<script src="assets/plugins/accordion1/js/easyResponsiveTabs.js"></script>

		<!-- Custom scroll bar js-->
		<script src="assets/plugins/scroll-bar/jquery.mCustomScrollbar.concat.min.js"></script>
		<!--Jquery Validator js-->
		<script src="assets/plugins/jquery-validation-1.19.2/dist/jquery.validate.min.js"></script>
		<!--Sweetalert2 js-->
		<script src="assets/plugins/sweetalert2/sweetalert2.js"></script>
		
		<!-- Custom js-->
		<script src="assets/js/custom.js"></script>
		<script src="assets/js/_ops_create_claimable_balance.js"></script>

		<script src="assets/plugins/dropdown/jquery.dropdown2.js"></script>
				
	</body>
</html>