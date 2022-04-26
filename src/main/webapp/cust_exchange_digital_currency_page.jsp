<%@ page language="java" contentType="text/html; charset=ISO-8859-1"    pageEncoding="ISO-8859-1" errorPage="error.jsp"%>
<%@ page import="com.pporte.*,com.pporte.model.*, org.apache.commons.lang3.StringUtils,  java.util.concurrent.*, java.util.*"%>
<%
String relationshipNo=null;
String publicKey=null;
List<AssetCoin> digitalCurrencies = null;
List<CryptoAssetCoins> assetCoins= null;
try{
	relationshipNo= ((User)session.getAttribute("SESS_USER")).getRelationshipNo();
	if(request.getAttribute("publickey")!=null)
		publicKey = (String)request.getAttribute("publickey");
	if(request.getAttribute("digitalcurrencies")!=null)
		digitalCurrencies = (List<AssetCoin> )request.getAttribute("digitalcurrencies");
	if(request.getAttribute("assetcoins")!=null)
		assetCoins = (List<CryptoAssetCoins> )request.getAttribute("assetcoins");
%>
<!doctype html>
<html lang="en" dir="ltr">
	<head>
        <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<!-- Favicon -->
		<link rel="icon" href="assets/images/brand/favicon.ico" type="image/x-icon"/>
		<link rel="shortcut icon" type="image/x-icon" href="assets/images/brand/favicon.ico" />

		<!-- Title -->
		<title>Trade Digital Currency</title>

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

	<body class="app sidebar-mini rtl" style="position: relative; min-height: 100%; top: 0px;" >
	

		<!--Global-Loader-->
		<!-- <div id="global-loader">
			<img src="assets/images/icons/loader.svg" alt="loader">
		</div> -->

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
								<li class="breadcrumb-item"><a href="#">Currency Remittance</a></li>
								<li class="breadcrumb-item active" aria-current="page">Trade Currency</li>
							</ol><!-- End breadcrumb -->
						</div>
						<div class="ml-auto">
								<div class="input-group">
									<a  class="btn btn-primary text-white mr-2"  id="btn-add-branch" onclick="fnCallRemittanceTransactionPage()">
										<span>
											<i class="fa fa-th-list"></i> View Remittance Transactions
										</span>
									</a>
								</div>
							</div>
						<!-- End page-header -->
						<!-- page -->
						<div class="page">
							<!-- page-content -->
							<div class="page-content">
								<!--row open-->
								<div class="row">
									<div class="col-lg-12">
										<div class="card">
											<div class="card-header">
												<h3 class="card-title">Trade Digital Currency</h3>
											</div>
											<div class="card-body">
												<form id="form" method="post">
													<div class="list-group">
														<div class="list-group-item py-3" data-acc-step>
															<h5 class="mb-0" data-acc-title>Step 1</h5>
															<div data-acc-content>
																<div class="my-3">
																	<div class="row">
																		<div class="col-lg-6 col-md-12">
																			<div class="form-group ">
																				<label class="form-label">Select Currency to Trade</label>	
																				<select name="sel_currency" id="sel_currency"  class="form-control custom-select">																	
																					 <% if(digitalCurrencies!=null){%>
																							<option selected disabled >Choose One</option>
																							<% for (int i=0; i<digitalCurrencies.size();i++){ %>
																								<option value="<%=((AssetCoin)digitalCurrencies.get(i)).getAssetCode()%>"><%=((AssetCoin)digitalCurrencies.get(i)).getAssetDescription()%></option>
																							<%}  %>	
																					<%	 } else { %> 
																								<option selected disabled >No Currency to trade available at the Moment</option>
																					<%} %>
																				</select>
																			</div>
																		</div>
																		<div class="col-lg-6 col-md-12">
																			<div class="form-group ">
																				<label class="form-label">Select Source Coin</label>	
																				<select name="source_coin" id="source_coin" class="form-control custom-select">																	
																					 <% if(assetCoins!=null){%>
																							<option label="Choose one">
																							</option>
																							<% for (int i=0; i<assetCoins.size();i++){ %>
																								<option value="<%=((CryptoAssetCoins)assetCoins.get(i)).getAssetCode()%>"><%=((CryptoAssetCoins)assetCoins.get(i)).getAssetDescription()%></option>
																							<%}  %>	
																						<%	 } else { %> 
																								<option selected disabled >No Source Coins at the Moment</option>
																							<%} %>
																				</select>
																			</div>
																		</div>
																	</div>
																	<div class="form-group">

																		<label>Enter amount you are willing to send</label>

																		<input type="number" name="amount_to_spend" id="amount_to_spend" class="form-control" />
																	</div>
																		<h3>Enter Receiver Details</h3>
																<div class="row">
																	<div class="col-lg-6 col-md-12">
																		<div class="form-group">
																			<label id="register-label-fullname">Receiver's Name <span style="color: red;">*</span></label>
																			<input type="text" name="receiver_name" id="receiver_name" class="form-control">
																		</div>
																	</div>
																	<div class="col-lg-6 col-md-12">
																		<div class="form-group">
																			<label id="register-label-mobileno">Receiver's Email <span style="color: red;">*</span></label>
																			<input type="email" name="receiver_email"id="receiver_email" class="form-control">
																		</div>
																	</div>
																</div>
																<div class="row">
																	<div class="col-lg-6 col-md-12">
																		<div class="form-group">
																			<label id="register-label-emailadd">Receiver's Bank Name<span style="color: red;">*</span></label>
																			<input type="text" name="receiver_bank_name" id="receiver_bank_name" class="form-control">
																		</div>
																	</div>
																	<div class="col-lg-6 col-md-12">
																		<div class="form-group">
																			<label id="register-label-confirmemailadd">Receiver's Bank Code <span style="color: red;">*</span></label>
																			<input type="text" name="receiver_bank_code" id="receiver_bank_code" class="form-control">
																		</div>
																	</div>
																</div>
																<div class="row">
																	<div class="col-lg-6 col-md-12">
																		<div class="form-group">
																			<label id="register-label-emailadd">Receiver's Account Number<span style="color: red;">*</span></label>
																			<input type="text" name="receiver_account_no" id="receiver_account_no" class="form-control">
																		</div>
																	</div>
																</div>
																	
																</div>
															</div>
														</div>
														
														<div class="list-group-item py-3" data-acc-step>
															<h5 class="mb-0" data-acc-title>Step 2</h5>
															<div data-acc-content>
																<div class="my-3">
																	<div class="row">
																		<div class="col-lg-12">
																			<div class="form-group form-elements">
																				<h5>Select Best Offer</h5>
																				<div class="custom-controls-stacked" id="div_offers_options" >
																					
																				</div>
																			</div>
																		</div>
																	</div>
																</div>
															</div>
														</div>
														
													</div>
												</form>
											</div>
										</div>
									</div>
								</div>
								<!--row closed-->
							
							</div>
						</div>


					</div>
				</div>
				<!-- End app-content-->

			</div>
			
		</div>
		
		<div id="error-msg" style="display: none;">
			
		</div>
		<form method = "post" id="post-form">
			
		</form>
		<form method = "post" id="post-form-2">
			<input type="hidden" name="qs" >
			<input type="hidden" name="rules" >
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
		<!-- Custom scroll bar js-->
		<script src="assets/plugins/scroll-bar/jquery.mCustomScrollbar.concat.min.js"></script>
		<script type="text/javascript" src="assets/plugins/dropdown/jquery.dropdown2.js"></script>
		<!--Jquery Validator js-->
		<script src="assets/plugins/jquery-validation-1.19.2/dist/jquery.validate.min.js"></script>
		<!--Sweetalert2 js-->
		<script src="assets/plugins/sweetalert2/sweetalert2.js"></script>
		
		<!-- i18next js-->
        <script src="assets/plugins/i18next/i18next.min.js"></script>
		
		<!--Accordion-Wizard-Form js-->
		<script src="assets/plugins/accordion-Wizard-Form/jquery.accordion-wizard.min.js"></script>
        <!-- Custom js-->
		<script src="assets/js/_cust_exchange_digital_currency_page.js"></script>
		<script src="assets/js/custom.js"></script>
	</body>
</html>
<%
}catch (Exception e){
	out.println ("Exception : "+e.getMessage());
}finally{
	if ( relationshipNo!=null); relationshipNo=null;
	if ( digitalCurrencies!=null); digitalCurrencies=null;
	if ( assetCoins!=null); assetCoins=null;
	if ( publicKey!=null); publicKey=null;
}
%>