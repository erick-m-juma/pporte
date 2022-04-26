<%@ page language="java" contentType="text/html; charset=ISO-8859-1"    pageEncoding="ISO-8859-1" errorPage="error.jsp"%>
<%@ page import="com.pporte.*,com.pporte.model.*, org.apache.commons.lang3.StringUtils,  java.util.concurrent.*, java.util.*"%>
<%

String publicKey=null;
ArrayList<AssetAccount> accountBalances = null;
try{

	if(request.getAttribute("publickey")!=null)
		publicKey = (String)request.getAttribute("publickey");
	if(request.getAttribute("externalwallets")!=null)
		accountBalances = (ArrayList<AssetAccount> )request.getAttribute("externalwallets");
	
%>
<!doctype html>
<html lang="en" dir="ltr">
	<head>
        <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<!-- Favicon -->
		<link rel="icon" href="assets/images/brand/pporte.png" type="image/x-icon"/>

		<!-- Title -->
		<title>View Porte Coins</title>

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
		<!-- Responsive css -->
		<link href="assets/css/responsive.css" rel="stylesheet" />
		 <!---Sweetalert2 css-->
		 <link href="assets/plugins/sweetalert2/sweetalert2.css" rel="stylesheet" />

	</head>

	<body class="app sidebar-mini rtl" onload="fnFetchPorteCoins()">

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
								<li class="breadcrumb-item active" aria-current="page">Assets</li>
							</ol><!-- End breadcrumb -->
						</div>
						
						<div class="row d-flex justify-content-center">
							<% if( accountBalances!=null){
								for(int i=0;i<accountBalances.size();i++){   
								if(accountBalances.get(i).getAssetCode().equals("PORTE")){ %>
									<div class="col-sm-12 col-md-3 col-lg-3 col-xl-3 card-wallet porte">
										<div class="card-body ">
											<div class="wallet-balance-ico"style="position: static;text-align: center;margin: 0px auto 15px;width: 60px;height: 60px;" >
												<img src="assets/images/crypto/porte.png" alt="PORTE" height="55" width="55">
											</div>
											<h3>PORTE token</h3>
											<h4><%=accountBalances.get(i).getAssetBalance()+" "%>PORTE</h4>
											<div class="my-wallet-address">
												<span><%=publicKey%></span> 
											</div>
										</div>
									</div>
								 <%}else if(accountBalances.get(i).getAssetCode().equals("USDC")){ %>
								 	<div class="col-sm-12 col-md-3 col-lg-3 col-xl-3 card-wallet usdc" style="background-color: #d6f01e30;">
										<div class="card-body ">
											<div class="wallet-balance-ico"style="position: static;text-align: center;margin: 0px auto 15px;width: 60px;height: 60px;" >
												<img src="assets/images/crypto/usdc.png" alt="Ethereum" height="55" width="55">
											</div>
											<h3>USDC</h3>
											<h4><%=accountBalances.get(i).getAssetBalance()+" "%>USDC</h4>
											<div class="my-wallet-address">
												<span><%=publicKey%></span> 
											</div>
										</div>
									</div>
								  <%}else if(accountBalances.get(i).getAssetCode().equals("VESL")){ %>
								  <div class="col-sm-12 col-md-3 col-lg-3 col-xl-3 card-wallet vessel">
									<div class="card-body ">
										<div class="wallet-balance-ico" style="position: static;text-align: center;margin: 0px auto 15px;width: 60px;height: 60px;">
											  <img src="assets/images/crypto/vessel.jpg" alt="VESL" height="55" width="55" >
										</div>
										<h3>Vessel</h3>
										<h4><%=accountBalances.get(i).getAssetBalance()+" "%>VESL</h4>
										<div class="my-wallet-address"> 
											<span><%=publicKey%></span> 
										</div>
									</div>
								</div>
								   <%}else if(accountBalances.get(i).getAssetCode().equals("XLM")){ %>
								   	<div class="col-sm-12 col-md-3 col-lg-3 col-xl-3 card-wallet xlm">
										<div class="card-body ">
											<div class="wallet-balance-ico" style="position: static;text-align: center;margin: 0px auto 15px;width: 60px;height: 60px;">
												<img src="assets/images/crypto/xlm.svg" alt="Stable" height="55" width="55">
											</div>
											<h3>XLM</h3>
											<h4><%=accountBalances.get(i).getAssetBalance()+" "%>XLM</h4>
											<div class="my-wallet-address"> 
												<span><%=publicKey%></span> 
											</div>
										</div>
									</div>
									
								<%}else if(accountBalances.get(i).getAssetCode().equals("BTC")){ %>
							   	<div class="col-sm-12 col-md-3 col-lg-3 col-xl-3 card-wallet xlm">
									<div class="card-body ">
										<div class="wallet-balance-ico" style="position: static;text-align: center;margin: 0px auto 15px;width: 60px;height: 60px;">
											<img src="assets/images/crypto/bitcoin.svg" alt="Stable" height="55" width="55">
										</div>
										<h3>Stellar Bitcoin</h3>
										<h4><%=accountBalances.get(i).getAssetBalance()+" "%>BTC</h4>
										<div class="my-wallet-address"> 
											<span><%=publicKey%></span> 
										</div>
									</div>
								</div>
							<%} %>
							<% }							
							}else{ %>
								<div><span id="ops_all_bin_list_errormsg1">No External Wallets </span> </div> 
							<% } %>	 

						</div>
						<!-- row -->
						<div class="wallet-transaction clearfix hidden" id="div_transactions" >
					
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
		
		<!-- i18next js-->
        <script src="assets/plugins/i18next/i18next.min.js"></script>

		<!--Side-menu js-->
		<script src="assets/plugins/toggle-sidebar/sidemenu.js"></script>

		<!-- Sidebar Accordions js -->
		<script src="assets/plugins/accordion1/js/easyResponsiveTabs.js"></script>

		<!-- Custom scroll bar js-->
		<script src="assets/plugins/scroll-bar/jquery.mCustomScrollbar.concat.min.js"></script>
	
		<!-- Custom js-->
		<script src="assets/js/_cust_show_porte_coins.js"></script>
		<script src="assets/js/custom.js"></script>
		
		<!--Sweetalert2 js-->
		<script src="assets/plugins/sweetalert2/sweetalert2.js"></script>
	</body>
</html>


<%
}catch (Exception e){
	out.println ("Exception : "+e.getMessage());
}finally{
	if ( accountBalances!=null); accountBalances=null;
	if ( publicKey!=null); publicKey=null;
}
%>