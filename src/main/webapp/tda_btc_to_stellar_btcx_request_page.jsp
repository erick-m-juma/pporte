<%@page import="com.pporte.model.Transaction"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"    pageEncoding="ISO-8859-1" errorPage="error.jsp"%>
<%@ page import="com.pporte.*,com.pporte.model.*, org.apache.commons.lang3.StringUtils,  java.util.concurrent.*, java.util.*"%>
<%
ArrayList<Transaction> btcToBTCXRequestTxns = null;
try{
	if(request.getAttribute("btctobtcxtransactions")!=null)	btcToBTCXRequestTxns = (ArrayList<Transaction>)request.getAttribute("btctobtcxtransactions");
%>

    
<!doctype html>
<html lang="en" dir="ltr">
	<head>
		<meta charset="UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<link rel="icon" href="assets/images/brand/pporte.png" type="image/x-icon"/>
		<link rel="shortcut icon" type="image/x-icon" href="assets/images/brand/pporte.png" />
		<!-- Title -->
		<title>BTC To BTCX Request</title>
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
	<style>
    .overlay{
        display: none;
        position: fixed;
        width: 100%;
        height: 100%;
        top: 0;
        left: 0;
        z-index: 10000;
        background: rgba(255,255,255,0.8) url("assets/images/icons/loader.svg") center no-repeat;
    }
    body{
        text-align: center;
    }
    /* Turn off scrollbar when body element has the loading class */
    body.loading{
        overflow: hidden;   
    }
    /* Make spinner image visible when body element has the loading class */
    body.loading .overlay{
        display: block;
    }
</style>
	<body class="app sidebar-mini rtl">
		<!--Global-Loader-->
		<div id="global-loader">
			<img src="assets/images/icons/loader.svg" alt="loader">
		</div>
		<div id="global-loader2" class="hidden">
			<img src="assets/images/icons/loader.svg" alt="loader">
		</div>
		<div class="page">
			<div class="page-main">
				<!-- Sidebar menu-->
				<jsp:include page="ops_topandleftmenu.jsp" />
				<!--sidemenu end-->
                <!-- app-content-->
				<div class="app-content  my-3 my-md-5 toggle-content">
					<div class="side-app">
						<!-- page-header -->
						<div class="page-header">
							<ol class="breadcrumb"><!-- breadcrumb -->
								<li class="breadcrumb-item"><a href="#">Manage Bitcoin</a></li>
								<li class="breadcrumb-item active" aria-current="page">Request From BTC TO BTCX</li>
							</ol><!-- End breadcrumb -->
						</div>
						<!-- End page-header -->

						<!-- row -->
						<div class="row">
							<div class="col-md-12 col-lg-12">
							<div class="card">
								<div class="card-header">
									<div class="card-title">BTCx Requests From BTC</div>
								</div>
								<div class="card-body">
                                	<div class="table-responsive">
										<table id="example" class="table table-striped table-bordered text-nowrap w-100">
											<thead>
												<tr>
													<th class="wd-15p">Customer Id</th>
													<th class="wd-15p">Public Key </th>
													<th class="wd-20p">Source Asset Code</th>
													<th class="wd-20p">Source Amount</th>
													<th class="wd-15p">Destination Asset Code</th>
													<th class="wd-15p">Destination Amount</th>
													<th class="wd-15p">Action</th>
													<th class="wd-15p">Date and Time</th>
												</tr>
											</thead>
											<tbody>
											<% if(btcToBTCXRequestTxns!=null){
													for(int i=0;i<btcToBTCXRequestTxns.size();i++){
											%>
												<tr>
													<td><%= ((Transaction)btcToBTCXRequestTxns.get(i)).getCustomerId() %></td>
													<td><%= ((Transaction)btcToBTCXRequestTxns.get(i)).getPublicKey() %></td>
													<td><%= ((Transaction)btcToBTCXRequestTxns.get(i)).getSourceAssetCode() %></td>
													<td><%= ((Transaction)btcToBTCXRequestTxns.get(i)).getSourceAmount() %></td>
													<td><%= ((Transaction)btcToBTCXRequestTxns.get(i)).getDestinationAssetCode() %></td>
													<td><%= ((Transaction)btcToBTCXRequestTxns.get(i)).getDestinationAmount() %></td>
													<td class="text-center align-middle">
														<div class="btn-group align-top">
															<button class="btn btn-sm btn-primary badge"
																data-target="#user-form-modal" id="btn_approve_txn" name="btn_approve_txn" data-toggle="modal" type="button" 
																onClick="javascript:fnApproveTransactions('<%=((Transaction) btcToBTCXRequestTxns.get(i)).getCustomerId()%>',
																'<%=((Transaction) btcToBTCXRequestTxns.get(i)).getCustomerName()%>','<%=((Transaction) btcToBTCXRequestTxns.get(i)).getPublicKey()%>',
																'<%=((Transaction) btcToBTCXRequestTxns.get(i)).getSourceAssetCode()%>','<%=((Transaction) btcToBTCXRequestTxns.get(i)).getSourceAmount()%>',
																'<%=((Transaction) btcToBTCXRequestTxns.get(i)).getDestinationAmount()%>','<%=((Transaction) btcToBTCXRequestTxns.get(i)).getRelationshipNo()%>',
																'<%=((Transaction) btcToBTCXRequestTxns.get(i)).getDestinationAssetCode()%>','<%=((Transaction) btcToBTCXRequestTxns.get(i)).getTxnCode()%>'
																);return false;"><i class="fa fa-check"></i>Approve</button>
														</div>
													</td>
													<td><%= ((Transaction)btcToBTCXRequestTxns.get(i)).getTxnDateTime() %></td>
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
				<form method = "post" id="post-form">
					
				</form>
				<form method = "post" id="post-form-data">
					<input type="hidden" name="qs" value="">
					<input type="hidden" name="rules" value="">
				</form>
			</div>
		</div>
		<!-- Message Modal -->
		<div class="modal fade" id="myModal" tabindex="-1" role="dialog"  aria-hidden="true">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-body">
						<form id="private-key-form">
							<div class="form-group">
								<label for="recipient-name" class="form-control-label">BTCx Distribution Account private key:</label>
								<input type="password" class="form-control" id="privatekey" name="privatekey" placeholder="Enter private key">
							</div>
							<div class="form-group">
									<textarea class="form-control" id="comment" name="comment" rows="2" placeholder="Enter Comment"></textarea>
							</div>
						</form>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary" onClick="fnCloseMyModal()" >Close</button>
						<button type="button" class="btn btn-primary" id="btn_send_to_backend" name="btn_send_to_backend" onClick="fnSendToBanckend()">Submit</button>
					</div>
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
		<!-- Sidebar Accordions js -->
		<script src="assets/plugins/accordion1/js/easyResponsiveTabs.js"></script>
		<!-- Custom scroll bar js-->
		<script src="assets/plugins/scroll-bar/jquery.mCustomScrollbar.concat.min.js"></script>
		<!-- Rightsidebar js -->
		<script src="assets/plugins/sidebar/sidebar.js"></script>
		
		<!-- Data tables js-->
		<script src="assets/plugins/datatable/jquery.dataTables.min.js"></script>
		<script src="assets/plugins/datatable/dataTables.bootstrap4.min.js"></script>
		<script src="assets/plugins/datatable/datatable-2.js"></script>
		<script src="assets/plugins/datatable/dataTables.responsive.min.js"></script>
		
		<!--Jquery Validator js-->
		<script src="assets/plugins/jquery-validation-1.19.2/dist/jquery.validate.min.js"></script>
		<!--Sweetalert2 js-->
		<script src="assets/plugins/sweetalert2/sweetalert2.js"></script>
		<!-- Custom js-->
		<script src="assets/js/custom.js"></script>
		<script src="assets/js/_tda_request_from_porte_assets_page.js"></script>
	</body>
</html>

<% 
}catch(Exception e){

}finally{
	if(btcToBTCXRequestTxns!=null) btcToBTCXRequestTxns=null;
	
	
}

%>