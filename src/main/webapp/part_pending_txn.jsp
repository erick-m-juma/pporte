<%@ page language="java" contentType="text/html; charset=ISO-8859-1"    pageEncoding="ISO-8859-1" errorPage="error.jsp"%>
<%@ page import="com.pporte.*,com.pporte.model.*, org.apache.commons.lang3.StringUtils,  java.util.concurrent.*, java.util.*"%>
<%
List<Transaction> transactions = null;
try{
	if(request.getAttribute("transactions")!=null)	transactions = (List<Transaction>)request.getAttribute("transactions");

%>

<!doctype html>
<html lang="en" dir="ltr">
	<head>
		<meta charset="UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta content="Hogo– Creative Admin Multipurpose Responsive Bootstrap4 Dashboard HTML Template" name="description">
		<meta content="Spruko Technologies Private Limited" name="author">
		<meta name="keywords" content="html admin template, bootstrap admin template premium, premium responsive admin template, admin dashboard template bootstrap, bootstrap simple admin template premium, web admin template, bootstrap admin template, premium admin template html5, best bootstrap admin template, premium admin panel template, admin template"/>
		<!-- Favicon -->
		<link rel="icon" href="assets/images/brand/pporte.png" type="image/x-icon"/>
		<link rel="shortcut icon" type="image/x-icon" href="assets/images/brand/pporte.png" />
		<!-- Title -->
		<title>Pending Transactions</title>
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
		<link href="assets/plugins/sweetalert2/sweetalert2.css" rel="stylesheet" />
	</head>
	<body class="app sidebar-mini rtl">
		<!--Global-Loader-->
		<div id="global-loader">
			<img src="assets/images/icons/loader.svg" alt="loader">
		</div>
		<div class="page">
			<div class="page-main">
				<!-- Sidebar menu-->
				<jsp:include page="part_topandleftmenu.jsp" />
				<!--sidemenu end-->
                <!-- app-content-->
				<div class="app-content  my-3 my-md-5 toggle-content">
					<div class="side-app">
						<!-- page-header -->
						<div class="page-header">
							<ol class="breadcrumb"><!-- breadcrumb -->
								<li class="breadcrumb-item"><a href="#">Dashboard</a></li>
								<li class="breadcrumb-item active" aria-current="page">View Pending Transactions</li>
							</ol><!-- End breadcrumb -->
							
						</div>
						<!-- End page-header -->

						<!-- row -->
						<div class="row">
							<div class="col-md-12 col-lg-12">
							<div class="card">
								<div class="card-header">
									<div class="card-title">View Pending Transactions</div>
								</div>
								<div class="card-body">
                                	<div class="table-responsive">
										<table id="jTable" class="table table-striped table-bordered text-nowrap w-100">
											<thead>
												<tr>
													<th class="wd-15p">Transaction Reference</th>
													<th class="wd-20p">Destination Amount</th>
													<th class="wd-15p">Destination Currency</th>
													<th class="wd-15p">Created On</th>
													<th class="wd-15p">Action</th>
													
												</tr>
											</thead>
											<tbody>
											<% if(transactions!=null){
													for(int i=0;i<transactions.size();i++){
											%>
												<tr>
													<td><%= ((Transaction)transactions.get(i)).getSystemReferenceInt() %></td>
												
													<td><%= ((Transaction)transactions.get(i)).getDestinationAmount() %></td>
													<td><%= ((Transaction)transactions.get(i)).getDestinationAssetCode() %></td>
													<td><%= ((Transaction)transactions.get(i)).getTxnDateTime() %></td>
													<td class="text-center align-middle">
														<div class="btn-group align-top">
															<button class="btn btn-sm btn-primary badge"onClick="javascript:fnViewMoreRow( 
															'<%= ((Transaction)transactions.get(i)).getTxnCode() %>', '<%= ((Transaction)transactions.get(i)).getSystemReferenceInt() %>',
															'<%= ((Transaction)transactions.get(i)).getTxnUserCode() %>', '<%= ((Transaction)transactions.get(i)).getRelationshipNo() %>',
															'<%= ((Transaction)transactions.get(i)).getPayMode() %>', '<%= ((Transaction)transactions.get(i)).getSourceAssetCode() %>',
															 '<%= ((Transaction)transactions.get(i)).getSourceAmount() %>',
															'<%= ((Transaction)transactions.get(i)).getDestinationAssetCode() %>', '<%= ((Transaction)transactions.get(i)).getDestinationAmount() %>',
															'<%= ((Transaction)transactions.get(i)).getPublicKey() %>', '<%= ((Transaction)transactions.get(i)).getSystemReferenceExt() %>',
															'<%= ((Transaction)transactions.get(i)).getSenderComment() %>', '<%= ((Transaction)transactions.get(i)).getPartnersComment() %>',
															'<%= ((Transaction)transactions.get(i)).getReceiverName() %>', '<%= ((Transaction)transactions.get(i)).getReceiverBankName() %>',
															'<%= ((Transaction)transactions.get(i)).getReceiverBankCode() %>', '<%= ((Transaction)transactions.get(i)).getReceiverAccountNo() %>',
															'<%= ((Transaction)transactions.get(i)).getReceiverEmail() %>', '<%= ((Transaction)transactions.get(i)).getStatus() %>'
															 )" type="button">View More</button> 
														</div>
													</td>
												</tr>
												
											<% }
											}else{ %>
												<tr><td> <span>No data Present</span></td></tr>
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
					</div>
				</div>
				<!-- End app-content-->
				<form method = "post" id="get-add-branch-form">
					<input type="hidden" name="qs" value="">
					<input type="hidden" name="rules" value="">
				</form>
			</div>
		</div>
		<!-- Add Partners Modal -->
		
	<!-- Large Modal -->
			<div id="largeModal" class="modal fade">
				<div class="modal-dialog modal-lg" role="document">
					<div class="modal-content ">
						<div class="modal-header pd-x-20">
							<h4 class="modal-title">Transaction Details</h4>
							<button type="button" class="close" data-dismiss="modal" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
						<div class="modal-body pd-20">
							
							<div class="table-responsive mb-3">
								<h4>Transaction details</h4>
								<table class="table row table-borderless w-100 m-0 border">
									<tbody class="col-lg-6 p-0">
										<tr>
											<td><strong><span id="id_txn_ref">Transaction Reference :</span> </strong> <span id="sp_txn_ref"></span></td>
										</tr>
										<tr>
											<td><strong><span id="id_source_amount">Source Amount :</span> </strong> <span id="sp_source_amount"></span></td>
										</tr>
										
									</tbody>
									<tbody class="col-lg-6 p-0">
										<tr>
											<td><strong><span id="id_txn_hash">Stellar Transaction hash :</span> </strong> <span id="sp_txn_hash"></span></td>
										</tr>
										
										<tr>
											<td><strong><span id="id_des_amount">Destination Amount :</span> </strong> <span id="sp_des_amount" ></span></td>
										</tr>
										
									</tbody>
								</table>
							</div>

							<div class="table-responsive mb-3">
								<h4>Receiver Details</h4>
								<table class="table row table-borderless w-100 m-0 border">
									<tbody class="col-lg-6 p-0">
										<tr>
											<td><strong><span id="id_receiver_name">Receiver Name :</span> </strong> <span id="sp_receiver_name" ></span></td>
										</tr>
										<tr>
											<td><strong><span id="id_bank_name">Receiver Bank Name :</span> </strong> <span id="sp_bank_name" ></span></td>
										</tr>
										<tr>
											<td><strong><span id="id_bank_acount_no">Receiver Account Number :</span> </strong> <span id="sp_bank_acount_no" ></span></td>
										</tr>
										
									</tbody>
									<tbody class="col-lg-6 p-0">
										<tr>
											<td><strong><span id="id_receiver_email">Receiver Email :</span> </strong> <span id="sp_receiver_email" ></span></td>
										</tr>
										<tr>
											<td><strong><span id="id_bank_code">Receiver Bank Code :</span> </strong> <span id="sp_bank_code" ></span></td>
										</tr>
										
									</tbody>
								</table>
							</div>
							<form method = "post" id="txn-status-form">
							
								<div class="form-group">
									<label class="form-label">Transaction Status</label>
									<select name="select_status" id="select_status" class="form-control custom-select">
										<option value="P" >Pending</option>
										<option value="C" >Complete</option>
									</select>
								</div>

								<div class="col-md-12 ">
									<div class="form-group mb-0">
										<label class="form-label">Comment</label>
										<textarea class="form-control" name="partner_comment" id="partner_comment" rows="4" placeholder="text here.."></textarea>
									</div>
								</div>
							</form>
								
							
						</div><!-- modal-body -->
						<div class="modal-footer">
							<button type="button" class="btn btn-primary" onclick="fnSubmitBtn()">Submit</button>
							<button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
						</div>
					</div>
				</div><!-- modal-dialog -->
			</div><!-- modal -->
		
		
		<form method = "post" id="get-page-form">
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
		<!--Jquery Validator js-->
		<script src="assets/plugins/jquery-validation-1.19.2/dist/jquery.validate.min.js"></script>	
		
		<!--Side-menu js-->
		<script src="assets/plugins/toggle-sidebar/sidemenu.js"></script>
		<!-- Sidebar Accordions js -->
		<script src="assets/plugins/accordion1/js/easyResponsiveTabs.js"></script>
		
		<!-- Custom scroll bar js-->
		<script src="assets/plugins/scroll-bar/jquery.mCustomScrollbar.concat.min.js"></script>
		<!-- Rightsidebar js -->
		<script src="assets/plugins/sidebar/sidebar.js"></script>
		<!-- Data tables js-->
		<script src="assets/plugins/datatable/jquery.dataTables.min.js"></script>
		<script src="assets/plugins/datatable/dataTables.bootstrap4.min.js"></script>
		<script src="assets/plugins/datatable/datatable.js"></script>
		<script src="assets/plugins/datatable/datatable-2.js"></script>
		<script src="assets/plugins/datatable/dataTables.responsive.min.js"></script>
		<!--Sweetalert2 js-->
		<script src="assets/plugins/sweetalert2/sweetalert2.js"></script>
		
		<!-- Custom js-->
		<script src="assets/js/custom.js"></script>
		<script src="assets/js/_part_pending_txn.js"></script>

		<script>
			$('#jTable').dataTable({ "bSort" : false } );
		</script>
	</body>
</html>

<% 
}catch(Exception e){

}finally{
	if(transactions!=null) transactions=null;
	
}

%>