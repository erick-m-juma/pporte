<%@ page language="java" contentType="text/html; charset=ISO-8859-1"    pageEncoding="ISO-8859-1" errorPage="error.jsp"%>
<%@ page import="com.pporte.*,com.pporte.model.*, org.apache.commons.lang3.StringUtils,  java.util.concurrent.*, java.util.*"%>
<%
String relationshipNo=null;
try{
	
	relationshipNo= ((User)session.getAttribute("SESS_USER")).getRelationshipNo();
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
		<title>Create Offers</title>

		<!--Bootstrap.min css-->
		<link rel="stylesheet" href="assets/plugins/bootstrap/css/bootstrap.min.css">

		<!-- Dashboard css -->
		<link href="assets/css/style.css" rel="stylesheet" />

		<!-- Custom scroll bar css-->
		<link href="assets/plugins/scroll-bar/jquery.mCustomScrollbar.css" rel="stylesheet" />

		<!-- Sidemenu css -->
		<link href="assets/plugins/toggle-sidebar/sidemenu.css" rel="stylesheet" />

		<!--Daterangepicker css-->
		<link href="assets/plugins/bootstrap-daterangepicker/daterangepicker.css" rel="stylesheet" />

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

	</head>

	<body class="app sidebar-mini rtl" >

		<!--Global-Loader-->
		<div id="global-loader">
			<img src="assets/images/icons/loader.svg" alt="loader">
		</div>
		<div class="page">
			<div class="page-main">
				<!--app-header and sidebar-->
				    <jsp:include page="ops_topandleftmenu.jsp" />
			<div id="loader" class="lds-dual-ring hidden overlay"></div>
                <!-- app-content-->
				<div class="app-content  my-3 my-md-5 toggle-content">
					<div class="side-app" style="height:89vh;">

						<!-- page-header -->
						<div class="page-header" style="margin:auto">
							<ol class="breadcrumb"><!-- breadcrumb -->
								<li class="breadcrumb-item"><a href="#">Manage Digital Assets</a></li>
								<li class="breadcrumb-item active" aria-current="page">Create Offers</li>
							</ol><!-- End breadcrumb -->
						</div>
						<!-- End page-header -->

								<!-- row -->
						<div class="row">
								<div class="col-md-12">
									<div class="card card-profile  overflow-hidden">
										<div class="card-body">
											<div class="nav-wrapper p-0">
												<ul class="nav nav-pills nav-fill flex-column flex-md-row" id="tabs-icons-text" role="tablist">
													<li class="nav-item">
														<a class="nav-link mb-sm-3 mb-md-0 mt-md-2 mt-0 mt-lg-0 active" id="tabs-icons-text-1-tab" data-toggle="tab" href="#tabs-icons-text-1" role="tab" aria-controls="tabs-icons-text-1" aria-selected="true">Manage Sell Offer</a>
													</li>
													<li class="nav-item">
														<a class="nav-link mb-sm-3 mb-md-0 mt-md-2 mt-0 mt-lg-0" id="tabs-icons-text-2-tab" data-toggle="tab" href="#tabs-icons-text-2" role="tab" aria-controls="tabs-icons-text-2" aria-selected="false">Manage Buy Offer</a>
													</li>
												</ul>
											</div>
										</div>
									</div> 
									<div class="card">
										<div class="card-body pb-0">
											<div class="tab-content" id="myTabContent">
												<div class="tab-pane fade active show" id="tabs-icons-text-1" role="tabpanel" aria-labelledby="tabs-icons-text-1-tab">
													<div class="card">
														<form id="buy_native_form" method="post">
															<div class="card-body">
																<div class="row">
																	<div class="col-xl-6">
																		<div class="form-group">
																			<label class="form-label">Select Asset Code</label> <select
																				class="form-control" id="selaccount" name="selaccount">
																				<option value="">Select Asset Code</option>
																				<option value="VESL">Vessel</option>
																				<option value="PORTE">Porte</option>
																			</select>
																		</div>
																		<div class="form-group">
																			<label class="form-label">Amount you are selling</label>
																			<input type="number" class="form-control" name="sellingamount" id="sellingamount" placeholder="Amount you are selling">
																		</div>
																		<div class="form-group">
																			<label class="form-label">Price of 1 unit in XLM</label>
																			<input type="number" class="form-control" name="priceunit" id="priceunit" placeholder="Price unit">
																		</div>
																		<div class="form-group">
																			<label class="form-label">Business Private Key</label>
																			<input type="password" class="form-control" name="buyprivatekey" id="buyprivatekey" placeholder="Enter Business Private Key">
																		</div>
																	</div>
																</div>
																<div class="text-center col-xl-6">
																	<button type="button" class="btn btn-info col align-self-center" id="btn_sell_offer">Create Offer</button>
																</div>
															</div>
															<input type="hidden" name="qs" value="">
									                        <input type="hidden" name="rules" value="">
		                 							        <input type="hidden" name="hdnusertype" value="">
		                 							        <input type="hidden" name="hdnselaccount" value="">
									                        <input type="hidden" name="hdnlangpref" id="hdnlangpref3" value="en">
														</form>
													</div>
												</div>
												<div aria-labelledby="tabs-icons-text-2-tab" class="tab-pane fade" id="tabs-icons-text-2" role="tabpanel">
													<div class="row">
														<div class="card">
														<form id="sel_porte_form" method="post">
															<div class="card-body">
																<div class="row">
																	<div class="col-xl-6">
																		<div class="form-group">
																			<label class="form-label">Select Asset Code</label> <select
																				class="form-control" id="selectaccount" name="selectaccount">
																				<option value="">Select Asset Code</option>
																				<option value="VESL">Vessel</option>
																				<option value="PORTE">Porte</option>
																			</select>
																		</div>
																		<div class="form-group">
																			<label class="form-label">Amount you are buying</label>
																			<input type="number" class="form-control" name="sellbuyamount" id="sellbuyamount" placeholder="Amount you are buying">
																		</div>
																		<div class="form-group">
																			<label class="form-label">Price of 1 unit in XLM</label>
																			<input type="number" class="form-control" name="sellpriceunit" id="sellpriceunit" placeholder="Price unit">
																		</div>
																		<div class="form-group">
																			<label class="form-label">Business Private Key</label>
																			<input type="password" class="form-control" name="sellprivatekey" id="sellprivatekey" placeholder="Enter Business Private Key">
																		</div>
																	</div>
																</div>
																<div class="text-center col-xl-6">
																	<button type="button" class="btn btn-info col align-self-center" id="btn_buy_offer">Create Offer</button>
																</div>
															</div>
															<input type="hidden" name="qs" value="">
									                        <input type="hidden" name="rules" value="">
		                 							        <input type="hidden" name="hdnusertype" value="">
		                 							        <input type="hidden" name="hdnselaccount" value="">
									                        <input type="hidden" name="hdnlangpref" id="hdnlangpref3" value="en">
														</form>
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
				<!-- End app-content-->

			</div>
			<form method = "post" id="profile-form">
				<input type="hidden" name="qs" value="">
				<input type="hidden" name="rules" value="">
				<input type="hidden" name="relno" value="">
				<input type="hidden" name="hdnassetpath" value="">
			</form>
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

		<!-- Sidebar Accordions js -->
		<script src="assets/plugins/accordion1/js/easyResponsiveTabs.js"></script>

		<!-- Custom scroll bar js-->
		<script src="assets/plugins/scroll-bar/jquery.mCustomScrollbar.concat.min.js"></script>

        <!-- i18next js-->
        <script src="assets/plugins/i18next/i18next.min.js"></script>
		<!--Jquery Validator js-->
        <script src="assets/plugins/jquery-validation-1.19.2/dist/jquery.validate.min.js"></script>
		<!-- Custom js-->
		<script src="assets/js/custom.js"></script>
		<!--Sweetalert2 js-->
		<script src="assets/plugins/sweetalert2/sweetalert2.js"></script>
		
		<!-- Custom js-->
		<script src="assets/js/_ops_create_offers_page.js"></script>
		
		<script>
		function fnChangePageLanguage(lang){
			//alert('inside lang change: ' +lang);
			fnChangePageLang(lang)
			//fnChangePageLang(lang)
		}
		var relno='';
		function fnGetRelno(){
			 relno = '<%=relationshipNo%>';
			 fnCallCustDetails();
		}
		/*
		 $(document).ready(function(){
		        $("#editbutton").click(function(){
					
		        	//alert("button clicked")
		            $("#exampleModal3").modal('show');
		        });
		    });
		*/
		
		</script>
	</body>
</html>


<%
}catch (Exception e){
	out.println ("Exception : "+e.getMessage());
}finally{
	if ( relationshipNo!=null); relationshipNo=null;
}
%>