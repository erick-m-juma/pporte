<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" errorPage="error.jsp"%>
<%@ page
	import="com.pporte.*, com.pporte.model.*, org.apache.commons.lang3.StringUtils,  java.util.concurrent.*, java.util.*"%>
<%

List<StellarAccount> listUnfundedCustomers = null; 
ConcurrentHashMap<String,String> hashStatus = null; Iterator<String> itStatus = null; 

try{
	if(request.getAttribute("allunregisteredaccounts")!=null)	listUnfundedCustomers = (List<StellarAccount>)request.getAttribute("allunregisteredaccounts");
	hashStatus = new ConcurrentHashMap<String,String>();
	hashStatus.put("P","Pending");	hashStatus.put("A","Active"); 
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
	<title>Fund New Accounts</title>
	
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
	<link href="assets/fonts/fonts/font-awesome.min.css" rel="stylesheet">
	
	<!-- Data table css -->
	<link href="assets/plugins/datatable/dataTables.bootstrap4.min.css" rel="stylesheet" />
	<link href="assets/plugins/datatable/responsivebootstrap4.min.css" rel="stylesheet" />
			 <!---Sweetalert2 css-->
        <link href="assets/plugins/sweetalert2/sweetalert2.css" rel="stylesheet" />
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
							<li class="breadcrumb-item"><a href="#">Manage Digital Assets</a></li>
							<li class="breadcrumb-item active" aria-current="page">Fund New Accounts</li>
						</ol>
						<!-- End breadcrumb -->
					</div>
					<!-- End page-header -->
					<!-- row -->
					<div class="row">
						<div class="col-md-12 col-lg-12">
							<div class="card">
								<div class="card-header">
									<div class="card-title">Fund Accounts</div>
								</div>
								<div class="card-body">
									<div class="table-responsive">
										<table id="pendingmerchtable" class="table table-striped table-bordered text-nowrap w-100">
											<thead>
												<tr>
													<th>Customer Name</th>
													<th>Public Key</th>
													<th>Status</th>
													<th>Created On</th>
													<th>Action</th>
 													</tr>
											</thead>
											<tbody>
											<% if( listUnfundedCustomers!=null){
													for(int i=0;i<listUnfundedCustomers.size();i++){       
												%>
		                                            <tr>
														<td><%=((StellarAccount)listUnfundedCustomers.get(i)).getCustomerName()%> </td>
														<td><%=((StellarAccount)listUnfundedCustomers.get(i)).getPublicKey()%></td>
														<td><%=hashStatus.get(((StellarAccount) listUnfundedCustomers.get(i)).getStatus())%></td>
														<td><%=((StellarAccount)listUnfundedCustomers.get(i)).getCreatedOn()%></td>
														<td class="text-center align-middle">
														<div class="btn-group align-top">
															<button class="btn btn-sm btn-primary badge"
																data-target="#user-form-modal" data-toggle="modal" type="button" 
																onClick="javascript:fnFundAccountModal('<%=((StellarAccount)listUnfundedCustomers.get(i)).getCustomerName()%>',
																'<%=((StellarAccount)listUnfundedCustomers.get(i)).getPublicKey()%>','<%=hashStatus.get(((StellarAccount) listUnfundedCustomers.get(i)).getStatus())%>',
																'<%=((StellarAccount)listUnfundedCustomers.get(i)).getCreatedOn()%>');return false;"><i class=""></i>Fund</button>
														</div>
													</td>
 
													<% }
												}else{ %>
												<tr><td colspan = "9"> <span id="ops_all_bin_list_errormsg1">No Unfunded Accounts</span> </td></tr>
												<% } %>	

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
					<div class="modal fade" id="edittxnlimitModal" tabindex="-1" role="dialog" aria-labelledby="largemodal" aria-hidden="true">
							<div class="modal-dialog modal-lg " role="document">
								<div class="modal-content">
									<div class="modal-body">
									<!-- row -->
									<div class="row">
										<div class="col-md-12">
											<div class="card">
											    <form id="fund_acc_form" method="post">
													<div class="card-body">
														<div class="row">
													      <div class="col-xl-6">
													     		 <div class="form-group">
																	<label class="form-label">Customer Name</label> 
																	<input type="text" class="form-control" name="customername" id="customername" readonly>
																</div>
																<div class="form-group">
																	<label class="form-label">Customer's public key</label> 
																	<input type="text" class="form-control" name="publickey" id="publickey" readonly >
																</div>
																<div class="form-group">
																	<label class="form-label">Status</label> 
																	<input type="text" class="form-control" name="status" id="status" readonly >
																</div>
																<div class="form-group">
																	<label class="form-label">Created On</label> 
																	<input type="text" class="form-control" name="createdon" id="createdon" readonly>
																</div>
																<div class="form-group">
																	<label class="form-label">Enter Amount <i>(1.5 XLM)</i></label> 
																	<input type="number" class="form-control" name="amount" id="amount" placeholder="Enter Amount in XLM" >
																</div>
																<div class="form-group">
																	<label class="form-label">Business Private Key</label> 
																	<input type="password" class="form-control" name="businesspvtkey" id="businesspvtkey" placeholder="Enter business private key" >
																</div>
															</div>
														</div>
													</div>
													<input type="hidden" name="qs" value="">
							                        <input type="hidden" name="rules" value="">
							                        <input type="hidden" name="hdnuserid" value="">
                 							        <input type="hidden" name="hdnusertype" value="">
							                        <input type="hidden" name="hdnlangpref" id="hdnlangpref3" value="en">
												</form>
											</div>
											
										</div>
									</div>
							    	<!-- row -->
									</div>
									<div class="modal-footer">
										<button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>										
										<button type="button" class="btn btn-primary" id="btn-edittxnlimits">Submit</button>
									</div>
								</div>
							</div>
						</div>

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
	
	<!-- i18next js-->
    <script src="assets/plugins/i18next/i18next.min.js"></script>

	<!-- Custom scroll bar js-->
	<script src="assets/plugins/scroll-bar/jquery.mCustomScrollbar.concat.min.js"></script>

	<!--Sweetalert2 js-->
		<script src="assets/plugins/sweetalert2/sweetalert2.js"></script>
	<!-- Data tables js-->
	<script src="assets/plugins/datatable/jquery.dataTables.min.js"></script>
	<script src="assets/plugins/datatable/dataTables.bootstrap4.min.js"></script>
	<script src="assets/plugins/datatable/datatable.js"></script>
	<script src="assets/plugins/datatable/datatable-2.js"></script>
	<script src="assets/plugins/datatable/dataTables.responsive.min.js"></script>
	<script src="assets/plugins/jquery-validation-1.19.2/dist/jquery.validate.min.js"></script>
	 
	 <!-- Custom js-->
	<script src="assets/js/custom.js"></script>
	<script src="assets/js/_ops_fund_registered_accounts_page.js"></script>
	
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
	if(listUnfundedCustomers !=null) listUnfundedCustomers=null;
}
%>