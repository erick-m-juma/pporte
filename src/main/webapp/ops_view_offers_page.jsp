<%@page import="com.pporte.utilities.Utilities"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"    pageEncoding="ISO-8859-1" errorPage="error.jsp"%>
<%@ page import="com.pporte.*, com.pporte.model.*, org.apache.commons.lang3.StringUtils,  java.util.concurrent.*, java.util.*"%>
<%
ArrayList<AssetOffer> arrOffers=null;
ArrayList<AssetOffer> arrVessleOffers=null;
try{
	if (request.getAttribute("alloffers") != null) arrOffers = (ArrayList<AssetOffer>) request.getAttribute("alloffers");
	if (request.getAttribute("allvessleoffers") != null) arrVessleOffers = (ArrayList<AssetOffer>) request.getAttribute("allvessleoffers");

	
%>
<!doctype html>
<html lang="en" dir="ltr">
	<head>
        <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<!-- Favicon -->
		<link rel="icon" href="assets/images/brand/pporte.png" type="image/x-icon"/>
		<link rel="shortcut icon" type="image/x-icon" href="assets/images/brand/pporte.png" />

		<!-- Title -->
		<title>View and Edit</title>

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
		
		<!-- Data table css -->
		<link href="assets/plugins/datatable/dataTables.bootstrap4.min.css" rel="stylesheet" />
		<link href="assets/plugins/datatable/responsivebootstrap4.min.css" rel="stylesheet" />
		<!-- Parsley -->
		<link href="assets/plugins/parsley/parsley.css" rel="stylesheet">
		<!--Sweetaler css-->
		<link href="assets/plugins/sweetalert2/sweetalert2.css">
		
	</head>

	<body class="app sidebar-mini rtl">

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
					<div class="side-app" style="height: 89vh;">
	
	
						<!-- page-header -->
						<div class="page-header">
							<ol class="breadcrumb">
								<!-- breadcrumb -->
								<li class="breadcrumb-item"><a href="#">Manage Offers></li>
								<li class="breadcrumb-item active" aria-current="page">View Offers</li>
							</ol>
							<!-- End breadcrumb -->
						</div>
						<!-- End page-header -->
							<!-- row -->
					<div class="row">
						<div class="col-md-12 col-lg-12">
							<div class="card card-profile  overflow-hidden">
								<div class="card-body">
									<div class="nav-wrapper p-0">
										<ul class="nav nav-pills nav-fill flex-column flex-md-row" id="tabs-icons-text" role="tablist">
											<li class="nav-item">
												<a class="nav-link mb-sm-3 mb-md-0 mt-md-2 mt-0 mt-lg-0 active" id="tabs-icons-text-1-tab" data-toggle="tab" href="#tabs-icons-text-1" role="tab" aria-controls="tabs-icons-text-1" aria-selected="true">View Porte Offers</a>
											</li>
											<li class="nav-item">
												<a class="nav-link mb-sm-3 mb-md-0 mt-md-2 mt-0 mt-lg-0" id="tabs-icons-text-2-tab" data-toggle="tab" href="#tabs-icons-text-2" role="tab" aria-controls="tabs-icons-text-2" aria-selected="false">View Vessel Offers</a>
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
														<div class="card-body">
															<div class="table-responsive">
																<table id="example3" style="color:black" class="table table-striped table-bordered text-nowrap" >
																	<thead>
																		<tr>
																			<th>Sell</th>
																			<th>Buy</th>
																			<th>Price</th>
																			<th>Date</th>
																			<th>Account</th>
																			
																		</tr>
																	</thead>
																	<tbody>
																		<%
																		if (arrOffers != null) {
																			for (int i = 0; i < arrOffers.size(); i++) {
																		%>
																		<tr>
																			<td><%=((AssetOffer) arrOffers.get(i)).getSellAsset().split(":")[0]%></td>
																			<td><%=((AssetOffer) arrOffers.get(i)).getBuyAsset().split(":")[0]%></td>
																			<td><%=((AssetOffer) arrOffers.get(i)).getPrice()%></td>
																			<td><%=Utilities.getStellarDateConvertor(((AssetOffer) arrOffers.get(i)).getDate()) %></td>
																			<td><%=((AssetOffer) arrOffers.get(i)).getAccountId()%></td>
																			
																			</tr>
						
																			<%
																			 }
																			} else {
																			%>
																		
																		<tr>
																			<td colspan="9"><span id="ops_all_bin_list_errormsg1">No Offers Created</span></td>
																			<p><span style="color:#1753fc"><a href=""onclick="javascript:fnCreateOffers();return false;">Click here to Create a new offer.</a></span></p>  
																			
																		</tr>
																		<%
																		}
																		%>
						
																	</tbody>
																</table>
															</div>
														</div>
													</div>
												</div>
												<div aria-labelledby="tabs-icons-text-2-tab" class="tab-pane fade" id="tabs-icons-text-2" role="tabpanel">
													<div class="row">
														<div class="card">
															<div class="card-body">
																<div class="table-responsive">
																	<table id="example" style="color:black" class="table table-striped table-bordered text-nowrap" >
																		<thead>
																			<tr>
																				<th>Sell</th>
																				<th>Buy</th>
																				<th>Price</th>
																				<th>Date</th>
																				<th>Account</th>
																				
																			</tr>
																		</thead>
																		<tbody>
																			<%
																			if (arrVessleOffers != null) {
																				for (int i = 0; i < arrVessleOffers.size(); i++) {
																			%>
																			<tr>
																				<td><%=((AssetOffer) arrVessleOffers.get(i)).getSellAsset().split(":")[0]%></td>
																				<td><%=((AssetOffer) arrVessleOffers.get(i)).getBuyAsset().split(":")[0]%></td>
																				<td><%=((AssetOffer) arrVessleOffers.get(i)).getPrice()%></td>
																				<td><%=Utilities.getMySQLDateConvertor(((AssetOffer) arrVessleOffers.get(i)).getDate())%></td>
																				<td><%=((AssetOffer) arrVessleOffers.get(i)).getAccountId()%></td>
																				
																				</tr>
							
																				<%
																				 }
																				} else {
																				%>
																			
																			<tr>
																				<td colspan="9"><span id="ops_all_bin_list_errormsg1">No Offers Created</span></td>
																				<p><span style="color:#1753fc"><a href=""onclick="javascript:fnCreateOffers();return false;">Click here to Create a new offer.</a></span></p>  
																				
																			</tr>
																			<%
																			}
																			%>
							
																		</tbody>
																	</table>
																</div>
															</div>
														</div>
													</div>
												</div>
											</div>
										</div>
									</div>
							<!-- section-wrapper -->
						</div>
						<form id="create_new_offer" method="post">
							<input type="hidden" name="qs">
							<input type="hidden" name="rules">
						<input type="hidden"name="hdnlangpref" id="hdnlangpref3" value="en">
						</form>
					</div>
					<!-- row end -->					
	
					</div>
				</div>
				<!-- End app-content-->
	
			</div>
	
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

		
		
		<!-- Data tables js-->
		<script src="assets/plugins/datatable/jquery.dataTables.min.js"></script>
		<script src="assets/plugins/datatable/dataTables.bootstrap4.min.js"></script>
		<script src="assets/plugins/datatable/datatable-2.js"></script>
		<script src="assets/plugins/datatable/dataTables.responsive.min.js"></script>
		
		<!-- Parsley js-->
		<script src="assets/plugins/parsley/parsley.min.js"></script>
		<!-- Sweetalert js-->
		<script src="assets/plugins/sweetalert2/sweetalert2.all.min.js"></script>
		
		<!--Jquery Validator js-->
		<script src="assets/plugins/jquery-validation-1.19.2/dist/jquery.validate.min.js"></script>
		
		<!-- Custom js-->
		<script src="assets/js/custom.js"></script>
		<script src="assets/js/_ops_view_offers_page.js"></script>
		
		
		
		
		<script>
			function fnChangePageLanguage(lang){
				//alert('inside lang change: ' +lang);
				fnChangePageLang(lang)
				//fnChangePageLang(lang)
			}
		</script>
	</body>
</html>


<%
}catch (Exception e){
	out.println ("Exception : "+e.getMessage());
}finally{
}
%>