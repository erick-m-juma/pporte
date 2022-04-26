<%@ page language="java" contentType="text/html; charset=ISO-8859-1"    pageEncoding="ISO-8859-1" errorPage="error.jsp"%>
<%@ page import="com.pporte.*,com.pporte.model.*, org.apache.commons.lang3.StringUtils,  java.util.concurrent.*, java.util.*"%>
<%
ArrayList<AssetCoin> arrAssetExchangePricing = null; ConcurrentHashMap<String, String> hashStatus = null; 

try{
	if (request.getAttribute("assetexchangepricing") !=null) arrAssetExchangePricing = (ArrayList<AssetCoin>) request.getAttribute("assetexchangepricing");

	hashStatus = new ConcurrentHashMap<String, String>();
	hashStatus.put("A", "Active");
	hashStatus.put("I", "Inactive");
	
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
		<title>BTC to BTCX Pricing</title>

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
		<!--Sweetaler css-->
		<link href="assets/plugins/sweetalert2/sweetalert2.css">
		  <!-- Custom css -->
		  <link href="assets/css/app.css" rel="stylesheet" />
		  <!-- Custom css -->
		 <link href="assets/css/style2.css" rel="stylesheet" />
		  <!-- Custom css -->
		 <link href="assets/css/style5.css" rel="stylesheet">
		  <!-- Responsive css -->
		 <link href="assets/css/responsive.css" rel="stylesheet" />

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
					<div class="side-app" style="height:89vh;">

						<!-- page-header -->
						<div class="page-header" style="margin:auto">
							<ol class="breadcrumb"><!-- breadcrumb -->
								<li class="breadcrumb-item"><a href="#">Manage Bitcoin</a></li>
								<li class="breadcrumb-item active" aria-current="page">BTC to BTCX</li>
							</ol><!-- End breadcrumb -->
						</div>
						<!-- End page-header -->
									<!-- page -->
									<div class="page">

										<!-- page-content -->
										<div class="page-content">
											<!-- row -->
												<div class="row">
													<div class="col-md-12 col-lg-12">
														<div class="card" style="margin-top:-100px;">
															<div class="card-header">
																<div class="card-title">BTC Pricing against BTCx</div>
																<div class="ml-auto">
																	<div class="input-group">
																		<a class="btn btn-primary text-white mr-2" data-toggle="modal" data-target="#addBTCxpricingModal">
																			<span> <i class="typcn typcn-plus"></i> Add Price</span>
																		</a>
							
																	</div>
																</div>
															</div>
															<div class="card-body">
																<div class="table-responsive">
																	<table id="viewbtcxpricingtable" class="table table-striped table-bordered text-nowrap w-100">
																	
																		<thead>
																			<tr>
																				<th>Exchange Rate</th>
																				<th>Status</th>
																				<th>Created On</th>
																				<th>Action</th>
																				
																			</tr>
																		</thead>
																		<tbody>
																			<%
																			if (arrAssetExchangePricing != null) {
																				for (int i = 0; i < arrAssetExchangePricing.size(); i++) {
																			%>
																			<tr>
																				<td>1 <%=((AssetCoin) arrAssetExchangePricing.get(i)).getSourceAssetCode()%> =  <%=((AssetCoin) arrAssetExchangePricing.get(i)).getExchangeRate()%> <%=((AssetCoin) arrAssetExchangePricing.get(i)).getDestinationAssetCode()%></td>
																				<%if(((AssetCoin) arrAssetExchangePricing.get(i)).getStatus().equals("A")){ %>
																					<td style="color: #2962ff"><%=hashStatus.get( ((AssetCoin) arrAssetExchangePricing.get(i)).getStatus())%></td>													
																				<%}else{ %>
																					<td><%=hashStatus.get( ((AssetCoin) arrAssetExchangePricing.get(i)).getStatus())%></td>													
																				<%} %>
																				<td><%=((AssetCoin) arrAssetExchangePricing.get(i)).getCreatedOn()%></td>													
																				<%if(((AssetCoin) arrAssetExchangePricing.get(i)).getStatus().equals("A")){ %>
							
																				<td class="text-center align-middle">
																					<div class="btn-group align-top">
																						<button class="btn btn-sm btn-primary badge"
																							data-target="#editBTCxPricingModal" data-toggle="modal" type="button" 
																							onClick="javascript:fnEditAssetPrice('<%=((AssetCoin) arrAssetExchangePricing.get(i)).getSourceAssetCode()%>','<%=((AssetCoin) arrAssetExchangePricing.get(i)).getDestinationAssetCode()%>',
																							'<%=((AssetCoin) arrAssetExchangePricing.get(i)).getExchangeRate() %>','<%=((AssetCoin) arrAssetExchangePricing.get(i)).getStatus()%>','<%=((AssetCoin) arrAssetExchangePricing.get(i)).getCreatedOn()%>',
																							'<%=((AssetCoin) arrAssetExchangePricing.get(i)).getSequenceNo()%>'
																							);return false;"><i class="fa fa-edit"></i>Edit</button>
																					</div>
																				</td>
																					<%}else{ %>
																				 <td class="text-center align-middle">
																					<span class="text-danger"> Can't be Edited</span>
																				</td>
																					
																					<%} %>
																				</tr>
							
																				<%
																				 }
																				} else {
																				%>
																			
																			<tr>
																				<td colspan="9"><span id="ops_all_bin_list_errormsg1">No Data available </span></td>
																			</tr>
																			<%
																			}
																			%>
							
																		</tbody>
																	</table>
																</div>
															</div>
															<!-- table-wrapper -->
														</div>
														<!-- section-wrapper -->
													</div>
												</div>
												<!-- row end -->
										</div>
									</div>
									
									<!-- Modal -->
					 <div class="modal fade" id="editBTCxPricingModal" tabindex="-1" role="dialog" aria-labelledby="largemodal" aria-hidden="true">
							<div class="modal-dialog modal-lg " role="document">
								<div class="modal-content">
									<div class="modal-header">
										<h5 class="modal-title" id="largemodal1">Edit BTC Pricing</h5>
										<button type="button" class="close" data-dismiss="modal" aria-label="Close">
											<span aria-hidden="true">X</span>
										</button>
									</div>
									<div class="modal-body">
									<!-- row -->
									<div class="row">
										<div class="col-md-12">
											<div class="card">
											    <form id="editassetrate-form" method="post">
													<div class="card-body">
														<div class="row">
													      <div class="col-xl-6">
																<div class="form-group">
																	<label class="form-label">Source Asset Code</label> 
																	<input type="text" class="form-control" name="editsourceassetcode" id="editsourceassetcode" readonly >
																</div>
																<div class="form-group">
																	<label class="form-label">Exchange Rate</label> 
																	<input type="text" class="form-control" name="editexchangerate" id="editexchangerate" readonly>
																</div>
																<div class="form-group">
																	<label class="form-label">Created On</label> 
																	<input type="text" class="form-control" name="editcreatedon" id="editcreatedon" readonly>
																</div>
															</div>
															<div class="col-xl-6">
																<div class="form-group">
																		<label class="form-label">Destination Asset Code</label> 
																		<input type="text" class="form-control" name="editdestinationassetcode" id="editdestinationassetcode" readonly >
																	</div>
																
																<div class="form-group">
																	<label class="form-label">Status</label> <select
																		class="form-control" id="selleditstatus" name="selleditstatus">
																		<option selected disabled value="">Select Status</option>
																		<option value="A">Active</option>
																		<option value="I">	Inactive</option>
																	</select>
																</div>	
															</div>
														</div>
													</div>
													<input type="hidden" name="qs" value="">
							                        <input type="hidden" name="rules" value="">
												</form>
											</div>
											
										</div>
									</div>
							    	<!-- row -->
									</div>
									<div class="modal-footer">
										<button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>										
										<button type="button" class="btn btn-primary" id="btn-editeditassetpricing">Submit</button>
									</div>
								</div>
							</div>
						</div>
							
						<!-- End Model -->
						<!-- Modal -->
						 <div class="modal fade" id="addBTCxpricingModal" tabindex="-1" role="dialog" aria-labelledby="largemodal" aria-hidden="true">
						
							<div class="modal-dialog modal-lg " role="document">
								<div class="modal-content">
									<div class="modal-header">
										<h5 class="modal-title" id="largemodal1">Add BTCx Pricing</h5>
										<button type="button" class="close" data-dismiss="modal" aria-label="Close">
											<span aria-hidden="true">X</span>
										</button>
									</div>
									<div class="modal-body">
									<!-- row -->
									<div class="row">
										<div class="col-md-12">
											<div class="card">
											    <form id="addassetpricing-from" method = "post">
													<div class="card-body">
														<div class="row">
														 <div class="col-xl-6">
																<div class="form-group">
																	<label class="form-label"> Source Asset Code</label>
																	<select class="form-control" name="seladdsourceassetcode" id="seladdassetcode" onchange="javascript:UpdateassetDescParam(); return false;">
																	<option selected disabled value="">Select Asset</option>
																		<option value="<%=NeoBankEnvironment.getBitcoinCode()%>"><%=NeoBankEnvironment.getBitcoinCode()%></option>									
																	</select>
																
																</div>
																<div class="form-group ">
																	<label class="form-label">Exchange Rate</label>
																	<div class="input-two-box">
																		<input type="text" class="form-control" name="addexhangerate" id="addexhangerate" value="" onkeyup="javascript: UpdateSellamount(); return false;">
																		<span style="color: #2b65ec; display: none" id="exchangerate"> 1 <span id="spansellassetcode"> </span> = <span id=spansexchangerate> 0</span> <span><%=NeoBankEnvironment.getStellarBTCxCode()%></span></span>
																	</div>

																</div>
															</div>
															<div class="col-xl-6">
															
															<div class="form-group">
																
																	<label class="form-label"> Destination Asset Code</label>
																	<select class="form-control" name="seladddestinationassetcode" id="seladdassetcode" onchange="javascript:UpdateassetDescParam(); return false;">
																	<option selected disabled value="">Select Asset</option>
																		<option value="<%=NeoBankEnvironment.getStellarBTCxCode()%>"><%=NeoBankEnvironment.getStellarBTCxCode()%></option>									
																	</select>
																
																</div>
																
																<div class="form-group">
																	<label class="form-label">Status</label> <select
																		class="form-control" id="selladdstatus" name="selladdstatus">
																		<option selected disabled value="">Select Status</option>
																		<option value="A">Active</option>
																		<option value="I">	Inactive</option>
																	</select>
																</div>	
															</div>
														</div>
													</div>
													<input type="hidden" name="qs" value="">
							                        <input type="hidden" name="rules" value="">
												</form>
											</div>
											
										</div>
									</div>
							    	<!-- row -->
									</div>
									<div class="modal-footer">
										<button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
										<button type="button" class="btn btn-primary" id="btn-addassetpricing">Submit</button>
									</div>
								</div>
							</div>
						</div>
							
						<!-- End Model -->


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
		<script src="assets/plugins/datatable/dataTables.responsive.min.js"></script>
	
		<!-- Sweetalert js-->
		<script src="assets/plugins/sweetalert2/sweetalert2.all.min.js"></script>
		
		<!--Jquery Validator js-->
		<script src="assets/plugins/jquery-validation-1.19.2/dist/jquery.validate.min.js"></script>
		
		<!-- Custom js-->
		<script src="assets/js/custom.js"></script>
		<script src="assets/js/_tda_btc_to_stellar_btcx_pricing_page.js"></script>
		
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
	if (arrAssetExchangePricing!=null) arrAssetExchangePricing=null; if(hashStatus!=null)hashStatus=null;
}
%>