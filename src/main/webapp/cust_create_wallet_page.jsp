<%@ page language="java" contentType="text/html; charset=ISO-8859-1"    pageEncoding="ISO-8859-1" errorPage="error.jsp"%>
<%@ page import="com.pporte.*,com.pporte.model.*, org.apache.commons.lang3.StringUtils,  java.util.concurrent.*, java.util.*"%>
<%
ArrayList<AssetCoin> arrAssetCoins = null;
try{
	if (request.getAttribute("assetcoins") != null) 
		arrAssetCoins = (ArrayList<AssetCoin>) request.getAttribute("assetcoins");
%>
<!doctype html>
<html lang="en" dir="ltr">
	<head>
        <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<!-- Favicon -->
		<link rel="icon" href="assets/images/brand/pporte.png" type="image/x-icon"/>

		<!-- Title -->
		<title>Create Wallet Page</title>

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
		 <!---Sweetalert2 css-->
		 <link href="assets/plugins/sweetalert2/sweetalert2.css" rel="stylesheet" />

		  <!-- Custom css -->
		  <link href="assets/css/app.css" rel="stylesheet" />
		  <!-- Custom css -->
		 <link href="assets/css/style2.css" rel="stylesheet" />
		  <!-- Custom css -->
		 <link href="assets/css/style5.css" rel="stylesheet">
		  <!-- Responsive css -->
		 <link href="assets/css/responsive.css" rel="stylesheet" />

	</head>

	<body class="app sidebar-mini rtl" onload="getCustomerCryptoAssets()">
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
								<li class="breadcrumb-item"><a href="#">Digital Assets</a></li>
								<li class="breadcrumb-item active" aria-current="page">Create Wallet</li>
							</ol><!-- End breadcrumb -->
						</div>
						<!-- End page-header -->
						<!-- page -->
						<div class="page">
							<!-- page-content -->
							<div class="page-content">
								<div class="container text-dark">
										<!-- row -->
										<div class="row justify-content-center align-self-center">
											<div class="col-xl-6 col-lg-12 col-md-12 col-sm-12">
												<div class="card">
													<div class="card-header">
														<h4>Create Wallets</h4>
													</div>
													<div class="card-body myTab">
														<ul class="nav nav-tabs" id="myTab" role="tablist">
															<li class="nav-item">
																<a class="nav-link active show" id="contact-tab" data-toggle="tab" href="#contact" role="tab" aria-controls="contact" aria-selected="true">Digital Assets</a>
															</li>
															<!-- <li class="nav-item">
																<a class="nav-link" id="profile-tab" data-toggle="tab" href="#profile" role="tab" aria-controls="profile" aria-selected="false">Popular Coins</a>
															</li> -->
															
														</ul>
														<div class="tab-content border p-3" id="myTabContent">
															<div class="tab-pane fade p-0" id="profile" role="tabpanel" aria-labelledby="profile-tab">
																
															</div>
															<div class="tab-pane fade p-0 active show" id="contact" role="tabpanel" aria-labelledby="contact-tab">
																<div class="card-body">
																	<form id="create-wal-form" method = "post">
																		<div class="form-group">
																			<label class="form-label">Select Wallet Asset <span style="color: red;">*</span></label>
																			<select name="asset_value" id="asset_value" class="form-control custom-select" >
																			</select>	
																		</div>
																		<!-- <div class="form-group" >
																			<label class="form-label">Do you have Stellar account?<span style="color: red;">*</span></label>
																			<div class="row">
																				<div class="col-3">
																					<label class="custom-control custom-radio">
																						<input type="radio" class="custom-control-input" name="example-radios" value="Y">
																						<span class="custom-control-label">Yes</span>
																					</label>
																				</div>
																				<div class="col-3">
																					<label class="custom-control custom-radio">
																						<input type="radio" class="custom-control-input" name="example-radios" value="N">
																						<span class="custom-control-label">No</span>
																					</label>
																				</div>
																			</div>
																		</div> -->
																		<!-- <div id="div_stellar_id" style="display: none;">
																			<div class="form-group">
																				<label class="form-label"><span> Public Key</span><span style="color: red;">*</span></label>
																				<input type="text" name="input_public_key" id="input_public_key" class="form-control"  placeholder="Enter Public Key">
																			</div>
	
																			<div class="form-group">
																				<label class="form-label"><span>Private Key</span> <span style="color: red;">*</span></label>
																				<input type="text" name="input_private_key" id="input_private_key" class="form-control"  placeholder="Enter Private Key">
																			</div>
																		</div> -->
																		<!-- <div class="form-group">
																			<label class="form-label"><span>Private Key</span> <span style="color: red;">*</span></label>
																			<input type="password" name="input_private_key" id="input_private_key" class="form-control"  placeholder="Enter Private Key">
																		</div> -->
																		
																		<div class="form-group">
																			<label class="form-label"><span>User Wallet Name</span><span style="color: red;">*</span></label>
																			<input type="text" name="input_wallet_desc" id="input_wallet_desc" class="form-control"  placeholder="Enter Description">
																		</div>

																		<input type="hidden" name="qs" value="">
																		<input type="hidden" name="rules" value="">					
																	</form>
																	<div class="transfer-coin-button">
																		<button class="theme-btn" id="btn_create_wal" name="btn_create_wal" style="background: 00008B; border: none; border-radius: 26px;" onclick="javascript:checkIfUserHasMneonicCode()">Create Wallet</button>
																	 </div>
																</div>
															</div>	
														</div>
																												
														
													</div>
													
												</div>
											</div>
	
										</div>
										<!-- row end -->
									</div>
								</div>
							</div>
					</div>
				</div>
				<!-- End app-content-->

			</div>
			
		</div>
		<!-- End Page -->
		
		<form method = "post" id="post-form">
			<input type="hidden" name="qs" value="">
			<input type="hidden" name="rules" value="">
		</form>


		<!-- Back to top -->
		<a href="#top" id="back-to-top"><i class="fa fa-angle-up"></i></a>

		<!-- Jquery js-->
		<script src="assets/js/vendors/jquery-3.2.1.min.js"></script>

		<!--Bootstrap.min js-->
		<script src="assets/plugins/bootstrap/popper.min.js"></script>
		<script src="assets/plugins/bootstrap/js/bootstrap.min.js"></script>

		<!--Jquery Validator js-->
		<script src="assets/plugins/jquery-validation-1.19.2/dist/jquery.validate.min.js"></script>


		<!--Side-menu js-->
		<script src="assets/plugins/toggle-sidebar/sidemenu.js"></script>

		<!-- Sidebar Accordions js -->
		<script src="assets/plugins/accordion1/js/easyResponsiveTabs.js"></script>
		
		<!-- i18next js-->
        <script src="assets/plugins/i18next/i18next.min.js"></script>

		<!-- Custom scroll bar js-->
		<script src="assets/plugins/scroll-bar/jquery.mCustomScrollbar.concat.min.js"></script>

		<!-- Custom js-->
		<script src="assets/js/custom.js"></script>
		<!-- Custom js-->
		<script src="assets/js/_cust_create_wallet_page.js"></script>
		<!--Sweetalert2 js-->
		<script src="assets/plugins/sweetalert2/sweetalert2.js"></script>
	</body>
</html>
<%
}catch (Exception e){
	out.println ("Exception : "+e.getMessage());
}finally{
	if(arrAssetCoins !=null) arrAssetCoins= null;
}
%>