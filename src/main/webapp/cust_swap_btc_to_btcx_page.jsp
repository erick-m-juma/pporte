<%@page import="com.pporte.utilities.Utilities"%>
<%@page import="com.google.gson.JsonObject"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"    pageEncoding="ISO-8859-1" errorPage="error.jsp"%>
<%@ page import="com.pporte.*,com.pporte.model.*, org.apache.commons.lang3.StringUtils,  java.util.concurrent.*, java.util.*"%>
<%
JsonObject balanceObj = null;
String balanceAccount = null;
String relationshipNo=null;
try{
	relationshipNo= ((User)session.getAttribute("SESS_USER")).getRelationshipNo();
	if (request.getAttribute("btc_balance") != null) balanceObj =  (JsonObject) request.getAttribute("btc_balance");
	if(balanceObj!=null){
		balanceAccount = Utilities.convertSatoshiToBTC(balanceObj.get("balance").getAsString());
	}
	
%>
<!doctype html>
<html lang="en" dir="ltr">
	<head>
        <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<!-- Favicon -->
		<link rel="icon" href="assets/images/brand/pporte.png" type="image/x-icon"/>

		<!-- Title -->
		<title> Swap BTC To BTCx</title>

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

		<!---Font icons css-->
		<link href="assets/plugins/iconfonts/plugin.css" rel="stylesheet" />
		<link href="assets/plugins/iconfonts/icons.css" rel="stylesheet" />
		<link  href="assets/fonts/fonts/font-awesome.min.css" rel="stylesheet">


		
		<!-- Custom css -->
		<link href="assets/css/style2.css" rel="stylesheet" />
	    <!---Custom Dropdown css-->
	    <link href="assets/css/style5.css" rel="stylesheet">
 		<!-- Responsive css -->
		<link href="assets/css/responsive.css" rel="stylesheet" />
		<!-- Custom css -->
		<link href="assets/css/app.css" rel="stylesheet" />
		<!---Sweetalert2 css-->
        <link href="assets/plugins/sweetalert2/sweetalert2.css" rel="stylesheet" />
	</head>

	<body class="app sidebar-mini rtl" style="position: relative; min-height: 100%; top: 0px;" onload="fnGetRelno()">
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
				    <jsp:include page="cust_topandleftmenu.jsp" />

                <!-- app-content-->
				<div class="app-content  my-3 my-md-5 toggle-content">
					<div class="side-app" style="height:89vh;">

						<!-- page-header -->
						<div class="page-header" style="margin:auto">
							<ol class="breadcrumb"><!-- breadcrumb -->
								<li class="breadcrumb-item"><a href="#">Bitcoin</a></li>
								<li class="breadcrumb-item active" aria-current="page">Swap BTC To BTCx</li>
							</ol><!-- End breadcrumb -->
						</div>
							<div class="ml-auto">
								<div class="input-group">
									<a  class="btn btn-primary text-white mr-2"  id="btn-add-branch" onclick="fnCallPendingTransactionPage()">
										<span>
											<i class="fa fa-th-list"></i>View Pending BTC Swap Transactions
										</span>
									</a>
								</div>
							</div>
						<!-- End page-header -->
									<!-- page -->
									<div class="page">

										<!-- page-content -->
										<div class="page-content">
										 	<div class="wallet-bradcrumb">
					                           <h2><i class=" fa-coins"></i>Swap BTC To BTCx</h2>
					                        </div>	                        
												<div class="tranfer-coin-box">
						                           <div class="row">
						                              <div class="col-xl-7">
						                                 <div class="transfercoin-left-box">
						                                     <div class="transfer-coin-content">
						                                       <div class="transfer-coin-content-box active" id="sent-coin">
																<form id="exchange-btc-coin-form"  method="post" >
						                                          
																  <div class="transfer-coin-input transfer-coin-select clearfix">
																	
																	<div class="dropdown">
																		<label><span id="sp_from_id">Source Asset</span> </label>
																		<select class="form-control"  name="source_asset" id ="source_asset" onChange="javascript: fnUpdatesenderparams(); return false">
																		</select>
																	</div>  
																	<i class="fa fa-exchange" style="color:#28a59a; display: none;" ></i>
																	<div class="dropdown">
																		<label>Destination </label>
																		<select class="form-control"  name="destination_asset" id ="destination_asset" ></select>
																	</div> 
																	 
																  </div>
						                                         
						                                        
						                                          <div class="transfer-coin-input">
						                                             <label>Amount</label>
						                                             <div class="input-two clearfix">
																		<div class="input-two-box">
																			<input type="" name="destination_amount" id= "destination_amount"value="" placeholder="" >
																			<span id="spanreceivedcode">BTC</span>
																		 </div>
						                                               
						                                                <i class="fa fa-exchange" id="exchange_icon" style="color:#28a59a; cursor: pointer;" onclick="javascript: cryptocoversionFromSource(); return false" ></i>
																		<div class="input-two-box">
																			<input type="" name="source_amount" id="source_amount" value="" placeholder="" readonly="readonly">
																			<span id="spansendcode">BTCx</span>
																		 </div>
						                                             </div>
						                                          </div>

																  	<!-- <div class="transfer-coin-input">
																		<label>Private Key</label>
																		<input type="password" name="private_key" id="private_key" value="" placeholder="Enter Your Private Key">
																	</div> -->
						                                          
						                                          <div class="transfer-coin-button">
					                                                 <button type="button" class="theme-btn" id="btn-swap-coins" onclick="javascript: checkIfUserHasMneonicCode(); return false">Exchange Asset</button>
						                                          </div>
						                                         </form>
						                                       </div>
						                                      </div>
						                                       
					                                        				                                       
						                                 </div>
						                              </div>
						                              
						                              
						                              
						                              	<div class="col-xl-5" >
															 <div class="wallet-transaction">
															  <% if( balanceAccount!=null){ %>
															   <div class="wallet-transaction-box clearfix" >
																	<div class="wallet-balance-ico">
																		<img src="assets/images/crypto/bitcoin.svg" alt="Litcoin" height="40" width="40" >
																	</div>
																	<div class="wallet-transaction-name">
																		<h3>Bitcoin</h3>
																	   <!--  <span>Last Updated</span> -->
																	</div>
																	<div class="wallet-transaction-balance">
																		<h3> <%=balanceAccount+" "%>BTC</h3>
																		<!-- <span>`+porteDetails[i].lastUpdated+`</span> -->
																	</div>
																</div>
																
													
														<%}else{ %>
															<div><span id="ops_all_bin_list_errormsg1">You don't have Bitcoin Wallet </span> </div> 
														<% } %>	 
															 
															 
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
		
		<div id="error-msg-cust-crypt-pay-anyone-page" style="display: none;">
			<span id="pay-anyone-data-validation-error-from">Please select the Coin</span>
			<span id="pay-anyone-data-validation-error-sendto">Please enter the receiver Wallet Id</span>
			<span id="pay-anyone-data-validation-error-amounttosend">Please enter Amount to send</span>
		</div>
		<form method = "post" id="post-form">
			<input type="hidden" name="qs" value="">
			<input type="hidden" name="rules" value="">
		</form>
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
		<!-- Sidebar Accordions js -->
		<script src="assets/plugins/accordion1/js/easyResponsiveTabs.js"></script>
		
		<!-- i18next js-->
        <script src="assets/plugins/i18next/i18next.min.js"></script>
		
		<!-- Custom scroll bar js-->
		<script src="assets/plugins/scroll-bar/jquery.mCustomScrollbar.concat.min.js"></script>
		<script type="text/javascript" src="assets/plugins/dropdown/jquery.dropdown2.js"></script>
		
		<!--Jquery Validator js-->
		<script src="assets/plugins/jquery-validation-1.19.2/dist/jquery.validate.min.js"></script>
		<!--Sweetalert2 js-->
		<script src="assets/plugins/sweetalert2/sweetalert2.js"></script>
        <!-- Custom js-->
        <script src="assets/js/custom.js"></script>
		<script src="assets/js/_cust_swap_btc_to_btcx_page.js"></script>

		<script>
			function fnChangePageLanguage(lang){
				//alert('inside lang change: ' +lang);
				fnChangePageLang(lang)
				//fnChangePageLang(lang)
			}
			var relno='';
			function fnGetRelno(){
				 relno = '<%=relationshipNo%>';
			}
		</script>
		
	</body>
</html>
<%
}catch (Exception e){
	out.println ("Exception : "+e.getMessage());
}finally{
	if ( relationshipNo!=null); relationshipNo=null;
	 if(balanceAccount!=null)balanceAccount=null;
	 if(balanceObj!=null)balanceObj=null;
	
}
%>