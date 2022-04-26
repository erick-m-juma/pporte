<%@ page language="java" contentType="text/html; charset=ISO-8859-1"    pageEncoding="ISO-8859-1" errorPage="error.jsp"%>
<%@ page import="com.pporte.*,com.pporte.model.*, com.pporte.utilities.*,org.apache.commons.lang3.StringUtils,  java.util.concurrent.*, java.util.*"%>

<%
ArrayList<AssetTransactions> assetTransactions = null; 
String publicKey=null;
String nextLink=null;
String previousLink=null;
try{
	
	if (request.getAttribute("assetTransactions") != null) assetTransactions = 
			(ArrayList<AssetTransactions>) request.getAttribute("assetTransactions");
	if (request.getAttribute("publickey") != null) publicKey = (String) request.getAttribute("publickey");

	/* if(assetTransactions!=null)
		Collections.reverse(assetTransactions); */
%>

<!doctype html>
<html lang="en" dir="ltr">
	<head>
		<meta charset="UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<meta content="Hogo– Creative Admin Multipurpose Responsive Bootstrap4 Dashboard HTML Template" name="description">
		<meta content="Spruko Technologies Private Limited" name="author">
		<meta name="keywords" content="html admin template, bootstrap admin template premium, premium responsive admin template, admin dashboard template bootstrap, bootstrap simple admin template premium, web admin template, bootstrap admin template, premium admin template html5, best bootstrap admin template, premium admin panel template, admin template"/>

		<!-- Favicon -->
		<link rel="icon" href="assets/images/brand/pporte.png" type="image/x-icon"/>
		
		<!-- Title -->
		<title>Display Transactions</title>

		<!--Bootstrap.min css-->
		<link rel="stylesheet" href="assets/plugins/bootstrap/css/bootstrap.min.css">

		<!-- Dashboard css -->
		<link href="assets/css/style.css" rel="stylesheet" />

		<!-- Custom scroll bar css-->
		<link href="assets/plugins/scroll-bar/jquery.mCustomScrollbar.css" rel="stylesheet" />

		<!---Sweetalert2 css-->
		<link href="assets/plugins/sweetalert2/sweetalert2.css" rel="stylesheet" />

		<!-- Sidemenu css -->
		<link href="assets/plugins/toggle-sidebar/sidemenu.css" rel="stylesheet" />

		<!-- Sidebar Accordions css -->
		<link href="assets/plugins/accordion1/css/easy-responsive-tabs.css" rel="stylesheet">

		<!-- Time picker css-->
		<link href="assets/plugins/time-picker/jquery.timepicker.css" rel="stylesheet" />

		<!-- Date Picker css-->
		<link href="assets/plugins/date-picker/spectrum.css" rel="stylesheet" />

		

		<!-- Rightsidebar css -->
		<link href="assets/plugins/sidebar/sidebar.css" rel="stylesheet">

		<!---Font icons css-->
		<link href="assets/plugins/iconfonts/plugin.css" rel="stylesheet" />
		<link href="assets/plugins/iconfonts/icons.css" rel="stylesheet" />
		<link  href="assets/fonts/fonts/font-awesome.min.css" rel="stylesheet">
		<!-- Data table css -->
		<link href="assets/plugins/datatable/dataTables.bootstrap4.min.css" rel="stylesheet" />
		<link href="assets/plugins/datatable/responsivebootstrap4.min.css" rel="stylesheet" />
		
		<!-- Custom css -->
		 <link href="assets/css/app.css" rel="stylesheet" />
		 <!-- Custom css -->
		<link href="assets/css/style2.css" rel="stylesheet" />
	     <!-- Custom css -->
	    <link href="assets/css/style5.css" rel="stylesheet">
 		<!-- Responsive css -->
		<link href="assets/css/responsive.css" rel="stylesheet" />
		

	</head>
		<style>
			#spinner-div {
				  position: fixed;
				  display: none;
				  width: 100%;
				  height: 100%;
				  top: 10;
				  left: 0;
				  text-align: center;
				  background-color: rgba(255, 255, 255, 0.8);
				  z-index: 2;
				}
				
		</style>

	<body class="app sidebar-mini rtl"  >

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
					<div class="side-app">
						<!-- page-header -->
						<div class="page-header">
							<ol class="breadcrumb"><!-- breadcrumb -->
								<li class="breadcrumb-item"><a href="#">Digital Assets</a></li>
								<li class="breadcrumb-item active" aria-current="page">Display Transactions</li>
							</ol><!-- End breadcrumb -->
						</div>
						<!-- End page-header -->

						<!-- row -->
						<div class="row row-cards">
						
							<div class="col-lg-12">
								<div class="card">
									<div class="card-header">
										<h4><span id="card_txn_header"></span></h4>
									</div>
									<div class="card-body">
										<div id="spinner-div" class="pt-5">
												<img src="assets/images/icons/loader.svg" alt="loader">
										</div>
										<div class="table-responsive"id="transactiontable">
										<div id="htmltable">
											<table class="table table-striped table-bordered text-nowrap" >
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
												<tbody>
											<% if( assetTransactions!=null){
													for(int i=0;i<assetTransactions.size();i++){       
												%>
		                                            <tr> 
														<td><%=((AssetTransactions)assetTransactions.get(i)).getOperationId()%> </td>
														<td><%=((AssetTransactions)assetTransactions.get(i)).getCreatedOn()%> </td>
														<td><%=((AssetTransactions)assetTransactions.get(i)).getType()%> </td>
														<%if( (assetTransactions.get(i).getType().equals("payment"))) {%>
														
														<td><%=Utilities.shortenPublicKey(((AssetTransactions)assetTransactions.get(i)).getFromAccount())%> </td>
														<td><%=Utilities.shortenPublicKey(((AssetTransactions)assetTransactions.get(i)).getToAccount())%> </td>
														<td><%=((AssetTransactions)assetTransactions.get(i)).getAmount() +" "+((AssetTransactions)assetTransactions.get(i)).getAssetCode()%> </td>
														
														<%}else if( (assetTransactions.get(i).getType().equals("manage_sell_offer"))) {%>
														
														<td><%=((AssetTransactions)assetTransactions.get(i)).getSellingAsset()%> </td>
														<td><%=((AssetTransactions)assetTransactions.get(i)).getBuyingAsset()%> </td>
														<td><%=((AssetTransactions)assetTransactions.get(i)).getOfferPrice()%> </td>
														
														<%}else if( (assetTransactions.get(i).getType().equals("manage_buy_offer"))) {%>
														
														<td><%=((AssetTransactions)assetTransactions.get(i)).getSellingAsset()%> </td>
														<td><%=((AssetTransactions)assetTransactions.get(i)).getBuyingAsset()%> </td>
														<td><%=((AssetTransactions)assetTransactions.get(i)).getOfferPrice()%> </td>
														
														<%}else if( (assetTransactions.get(i).getType().equals("change_trust"))) {%>
														
														<td><%=Utilities.shortenPublicKey(((AssetTransactions)assetTransactions.get(i)).getTrustee())%> </td>
														<td><%=Utilities.shortenPublicKey(((AssetTransactions)assetTransactions.get(i)).getTrustor())%> </td>
														<td><%=((AssetTransactions)assetTransactions.get(i)).getAssetCode()	%> </td>
														
														<%}else if( (assetTransactions.get(i).getType().equals("create_account"))) {%>
														<td></td>
														<td></td>
														<td></td>
														<%}else if( (assetTransactions.get(i).getType().equals("path_payment_strict_send"))) {%>
														<td><%=Utilities.shortenPublicKey(((AssetTransactions)assetTransactions.get(i)).getFromAccount())%></td>
														<td><%=Utilities.shortenPublicKey(((AssetTransactions)assetTransactions.get(i)).getToAccount())%></td>
														<td><%=((AssetTransactions)assetTransactions.get(i)).getAmount()  +" "+((AssetTransactions)assetTransactions.get(i)).getAssetCode()%></td>
														<%}else if( (assetTransactions.get(i).getType().equals("path_payment_strict_receive"))) {%>
														<td><%=Utilities.shortenPublicKey(((AssetTransactions)assetTransactions.get(i)).getFromAccount())%></td>
														<td><%=Utilities.shortenPublicKey(((AssetTransactions)assetTransactions.get(i)).getToAccount())%></td>
														<td><%=((AssetTransactions)assetTransactions.get(i)).getAmount()  +" "+((AssetTransactions)assetTransactions.get(i)).getAssetCode()%></td>
														<%}else if( (assetTransactions.get(i).getType().equals("create_claimable_balance"))) {%>
														<td><%=Utilities.shortenPublicKey(((AssetTransactions)assetTransactions.get(i)).getFromAccount())%></td>
														<td></td>
														<td><%=((AssetTransactions)assetTransactions.get(i)).getAmount()  +" "+((AssetTransactions)assetTransactions.get(i)).getAssetCode()%></td>
														<%}else if( (assetTransactions.get(i).getType().equals("claim_claimable_balance"))) {%>
														<td><%=Utilities.shortenPublicKey(((AssetTransactions)assetTransactions.get(i)).getFromAccount())%></td>
														<td><%=Utilities.shortenPublicKey(((AssetTransactions)assetTransactions.get(i)).getToAccount())%></td>
														<td></td>
														<%}else if( (assetTransactions.get(i).getType().equals("Claimable Balance to Claim"))) {%>
														<td><%=Utilities.shortenPublicKey(((AssetTransactions)assetTransactions.get(i)).getFromAccount())%></td>
														<td></td>
														<td><%=((AssetTransactions)assetTransactions.get(i)).getAmount()  +" "+((AssetTransactions)assetTransactions.get(i)).getAssetCode()%></td>
														<%}%>
														
														<td><%=((AssetTransactions)assetTransactions.get(i)).getFeeCharged() +" Stroops"%> </td>
 															<%
 																previousLink=((AssetTransactions)assetTransactions.get(i)).getPreviousLink();
 																nextLink=((AssetTransactions)assetTransactions.get(i)).getNextLink();
 																
 															%>
													<% }
												}else{ %>
												<tr><td colspan = "9"> <span id="ops_all_bin_list_errormsg1">No available Transactions Details </span> </td></tr>
												<% } %>	
											
												</tbody>
											</table>
											</div>
									</div>
									<div class="card-footer">
									
											<ul class="pagination justify-content-center mb-0">
											<li id="prev_btn"class="page-item page-prev disabled" onclick="javascript:fnPreviousPage('<%=previousLink%>');return false;">
												<a class="page-link" href="#" tabindex="-1">Prev</a>
											</li>
											<li  id="next_btn" class="page-item page-next" onclick="javascript:fnNextPage('<%=nextLink%>');return false;">
												<a class="page-link" href="#">Next</a>
											</li>
										</ul>
									</div>
								</div>
							</div>
						</div>
						<!-- row end -->

					</div>
					
										
					<form method = "post" id="get-txn-form">
						<input type="hidden" name="qs" value="">
						<input type="hidden" name="rules" value="">
						<input type="hidden" name="link" value="">
					</form>

					

					<!--footer-->
					
					<!-- End Footer-->

				</div>
				<!-- End app-content-->
			</div>
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
		
		<script src="assets/plugins/select2/select2.full.min.js"></script>
		
		<!-- Custom scroll bar js-->
		<script src="assets/plugins/scroll-bar/jquery.mCustomScrollbar.concat.min.js"></script>
		<!-- Rightsidebar js -->
		<script src="assets/plugins/sidebar/sidebar.js"></script>
		<!--Sweetalert2 js-->
		<script src="assets/plugins/sweetalert2/sweetalert2.js"></script>
		<script src="assets/js/select2.js"></script>
		
		<!-- Data tables js-->
		<script src="assets/plugins/datatable/jquery.dataTables.min.js"></script>
		<script src="assets/plugins/datatable/dataTables.bootstrap4.min.js"></script>
		<script src="assets/plugins/datatable/datatable-2.js"></script>
		<script src="assets/plugins/datatable/dataTables.responsive.min.js"></script>
		<!--Jquery Validator js-->
        <script src="assets/plugins/jquery-validation-1.19.2/dist/jquery.validate.min.js"></script>
		<!-- Timepicker js -->
		<script src="assets/plugins/time-picker/jquery.timepicker.js"></script>
		<script src="assets/plugins/time-picker/toggles.min.js"></script>
		<!-- Datepicker js -->
		<script src="assets/plugins/date-picker/spectrum.js"></script>
		<script src="assets/plugins/date-picker/jquery-ui.js"></script>
		<script src="assets/plugins/input-mask/jquery.maskedinput.js"></script>
		
		<!-- i18next js-->
        <script src="assets/plugins/i18next/i18next.min.js"></script>
	
		<!-- Sidebar Accordions js -->
		<script src="assets/plugins/accordion1/js/easyResponsiveTabs.js"></script>
		
		<!-- Custom js-->
		<script src="assets/js/_cust_porte_display_porte_txn.js"></script>
		<script src="assets/js/custom.js"></script>	
		
			<script>
			  var pubKey='';
				$(function (){
				  
				    pubKey='<%=publicKey%>';
				    console.log("pubkey is"+pubKey);
				});
				
				
				    
		</script>

	</body>
</html>


<%
}catch (Exception e){
	out.println ("Exception : "+e.getMessage());
}finally{
	
	if(assetTransactions !=null) assetTransactions = null; 
	if(publicKey !=null) publicKey = null; 
	if(nextLink !=null) nextLink = null; 
	if(previousLink !=null) previousLink = null; 
}
%>