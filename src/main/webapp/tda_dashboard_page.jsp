<%@ page language="java" contentType="text/html; charset=ISO-8859-1"    pageEncoding="ISO-8859-1" errorPage="error.jsp"%>
<%@ page import="com.pporte.*,com.pporte.model.*, org.apache.commons.lang3.StringUtils,  java.util.concurrent.*, java.util.*"%>
<%

try{
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
		<title>TDA Dashboard Page</title>

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
								<li class="breadcrumb-item"><a href="#">Welcome</a></li>
								<li class="breadcrumb-item active" aria-current="page">Home</li>
							</ol><!-- End breadcrumb -->
						</div>
						<!-- End page-header -->
									<!-- page -->
									<div class="page">

										<!-- page-content -->
										<div class="page-content">
											<div class="container text-center text-dark">
													<div class="construction ">
														<div class="text-dark">
															<div class="card-body ">
																<h2 class="font mb-4">Coming Soon</h2>
																<h4 class="mt-5 text-dark">This page is under construction!</h4>
																<br>
																<div class="count-down construction row text-center mb-5">
																	<div class="countdown clock-presenter">
																		<span class="days">20</span>
																		<span class="">Days</span>
																	</div>
																	<div class=" countdown clock-presenter">
																		<span class="hours">17</span>
																		<span class="">Hours</span>
																	</div>
																	<div class="countdown clock-presenter">
																		<span class="minutes">50</span>
																		<span class="">Minutes</span>
																	</div>
																	<div class=" countdown clock-presenter">
																		<span class="seconds">39</span>
																		<span class="">Seconds</span>
																	</div>
																</div>
																
															</div>
														</div>
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

		<!--Jquery Sparkline js-->
		<script src="assets/js/vendors/jquery.sparkline.min.js"></script>

		<!-- Star Rating js-->
		<script src="assets/plugins/rating/jquery.rating-stars.js"></script>

		<!--Moment js-->
		<script src="assets/plugins/moment/moment.min.js"></script>

		<!--Time Counter js-->
		<script src="assets/plugins/counters/jquery.missofis-countdown.js"></script>
		<script src="assets/plugins/counters/counter.js"></script>

		<!-- Daterangepicker js-->
		<script src="assets/plugins/bootstrap-daterangepicker/daterangepicker.js"></script>

		<!--Side-menu js-->
		<script src="assets/plugins/toggle-sidebar/sidemenu.js"></script>

		<!-- Sidebar Accordions js -->
		<script src="assets/plugins/accordion1/js/easyResponsiveTabs.js"></script>

		<!-- Custom scroll bar js-->
		<script src="assets/plugins/scroll-bar/jquery.mCustomScrollbar.concat.min.js"></script>

        <!-- i18next js-->
        <script src="assets/plugins/i18next/i18next.min.js"></script>

		<!-- Charts js-->
		<script src="assets/plugins/chart/chart.bundle.js"></script>
		<script src="assets/plugins/chart/utils.js"></script>


		
		
		<!--Time Counter js-->
		<script src="assets/plugins/counters/jquery.missofis-countdown.js"></script>
		<script src="assets/plugins/counters/count-down.js"></script>
		<script src="assets/plugins/counters/counter.js"></script>
		<script src="assets/plugins/countdown/moment.min.js"></script>
		<script src="assets/plugins/countdown/moment-timezone.min.js"></script>
		<script src="assets/plugins/countdown/moment-timezone-with-data.min.js"></script>
		<script src="assets/plugins/countdown/countdowntime.js"></script>
		
		<!-- Custom js-->
		<script src="assets/js/custom.js"></script>
		<script src="assets/js/_part_dashboard.js"></script>
		
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