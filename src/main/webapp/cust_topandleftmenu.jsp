<%@page import="com.pporte.utilities.JSPUtilities"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"    pageEncoding="ISO-8859-1" errorPage="error.jsp"%>
<%@ page import="com.pporte.*,com.pporte.model.*, org.apache.commons.lang3.StringUtils,  java.util.concurrent.*, java.util.*"%>
<%
User user_leftmenu = null; String lastaction=""; String strsubmenu=null;  String lastrule = "";  String userType = null;
String topnavUserName = "";	String topnavUserEmail="";  String topnavRelNo=""; String userId = "";

try{
	if(session.getAttribute("SESS_USER")!=null)		user_leftmenu = (User)session.getAttribute("SESS_USER");
	if(request.getAttribute("lastaction")!=null)		lastaction=(String)request.getAttribute("lastaction");
	if(request.getAttribute("lastrule")!=null)		lastrule=(String)request.getAttribute("lastrule");
	if(user_leftmenu!=null){
		userType = "C";
		topnavUserName = user_leftmenu.getName();	
		userId = user_leftmenu.getCustomerId(); 
	}
%>
				<!--app-header-->
				<div class="app-header header d-flex">
					<div class="container-fluid">
						<div class="d-flex">
						    <a class="header-brand" href="index.html">
								<img src="assets/images/brand/logo.png" class="header-brand-img main-logo" alt="logo" >
								<!--<img src="assets/images/brand/icon.png" class="header-brand-img icon-logo" alt="logo">-->
							</a><!-- logo-->
							<a aria-label="Hide Sidebar" class="app-sidebar__toggle" data-toggle="sidebar" href="#"></a>
							
                            <div class="d-flex order-lg-2 ml-auto header-rightmenu">
								<div class="dropdown">
									<a  class="nav-link icon full-screen-link" id="fullscreen-button">
										<i class="fe fe-maximize-2"></i>
									</a>
								</div><!-- full-screen -->
								<div class="dropdown mt-2 mb-2">
									<button type="button" class="btn btn-outline-primary dropdown-toggle" data-toggle="dropdown">
										<span id="lang_def">EN</span>
									</button>
									<div class="dropdown-menu">
										<a class="dropdown-item" href="#" onclick="javascript: fnChangePageLanguage('en');return false;">English</a>
										<a class="dropdown-item" href="#" onclick="javascript: fnChangePageLanguage('es');return false;">Spanish</a>
									</div>
								</div>
 								<div class="dropdown header-notify">
									<a class="nav-link icon" data-toggle="dropdown" aria-expanded="false">
										<i class="fe fe-settings "></i>
										
									</a>
									<div class="dropdown-menu dropdown-menu-right dropdown-menu-arrow ">
										<a href="#" class="dropdown-item text-center" ><span id='idnav_UserSettings'>User Settings</span></a>
										<div class="dropdown-divider"></div>
										<a href="#" class="dropdown-item d-flex pb-3" onclick="javascript:fnGoToSubMenuPageJQ('View and Edit', 'lgt');return false;">
											<div class="notifyimg bg-green">
												<i class="fe fe-user-check"></i>
											</div>
											<div>
												<strong><span id='idnav_UpdateProfile'>Update Profile</span></strong>
											</div>
										</a>
										<a href="#" class="dropdown-item d-flex pb-3" onclick="javascript:fnGoToSubMenuPageJQ('Logout', 'lgt');return false;">
											<div class="notifyimg bg-orange">
												<i class="fe fe-external-link"></i>
											</div>
											<div>
												<strong><span id='idnav_Logout'>Logout</span></strong>
											</div>
										</a>
									</div>
								</div><!-- notifications -->                               
                                
                            </div>
						</div>
					</div>
				</div>
				<!--app-header end-->

				<!-- Sidebar menu-->
				<div class="app-sidebar__overlay" data-toggle="sidebar"></div>
				<aside class="app-sidebar toggle-sidebar">
					<div class="app-sidebar__user pb-0">

						<div class="user-info">
							<a href="#" class="ml-2"><span class="text-dark app-sidebar__user-name font-weight-semibold"><%=topnavUserName %></span><br>
								<span class="text-muted app-sidebar__user-name text-sm"> Customer </span>
							</a>
						</div>
					</div>

					<div class="panel-body p-0 border-0 ">
						<div class="tab-content">
								<ul class="side-menu toggle-menu">
								
								<%if(user_leftmenu.getPricingPlanid().equals("") == true){ 
									 for (int i=0;i<JSPUtilities.getFreeTireCustomerMenu().size();i++){ 
											String menukey = JSPUtilities.getFreeTireCustomerMenu().get(i).substring(0, JSPUtilities.getFreeTireCustomerMenu().get(i).indexOf(","));
											String menuvalue = JSPUtilities.getFreeTireCustomerMenu().get(i).substring(JSPUtilities.getFreeTireCustomerMenu().get(i).indexOf(",")+1); 
												
											if(lastaction.equals(menukey)){ %>
											 		<li class="slide is-expanded">
											 		<% if( JSPUtilities.getFreeTireCustomerSubMenu().get(menukey)!=null){ %>
														<a class="side-menu__item active" data-toggle="slide" href="#"><i class="<%=JSPUtilities.getFreeTireCustomerSubMenu().get(i)%>"></i><span class="side-menu__label" id='idnavmenu_<%=StringUtils.replace(menuvalue, " ", "")%>'><%=menuvalue %></span><i class="angle fa fa-angle-right"></i></a>
													<% }else { %>
														<a class="side-menu__item active" href="#" onclick="javascript:fnGoToSubMenuPageJQ('<%=menuvalue%>', '<%=menukey%>');return false;"><i class="<%=JSPUtilities.getFreeTireCustomerMenuIcons().get(i)%>"></i><span class="side-menu__label" id='idnavmenu_<%=StringUtils.replace(menuvalue, " ", "")%>'><%=menuvalue %></span></a>
													<% } %>
														<% }else{ %>
														<li class="slide">
																<% if(JSPUtilities.getFreeTireCustomerSubMenu().get(menukey)!=null){ %>
																	<a class="side-menu__item" data-toggle="slide" href="#"><i class="<%=JSPUtilities.getFreeTireCustomerMenuIcons().get(i)%>"></i><span class="side-menu__label" id='idnavmenu_<%=StringUtils.replace(menuvalue, " ", "")%>'><%=menuvalue %></span><i class="angle fa fa-angle-right"></i></a>
																<% }else { %>
																	<a class="side-menu__item" href="#" onclick="javascript:fnGoToSubMenuPageJQ('<%=menuvalue%>', '<%=menukey%>');return false;"><i class="<%=JSPUtilities.getFreeTireCustomerMenuIcons().get(i)%>"></i><span class="side-menu__label" id='idnavmenu_<%=StringUtils.replace(menuvalue, " ", "")%>'><%=menuvalue %></span></a>
																<% } %>
														<% } %>

														 <ul class="slide-menu">
														<%	
																if(JSPUtilities.getFreeTireCustomerSubMenu().get(menukey)!=null) {	
																	String strEachSubmenu[] =   JSPUtilities.getFreeTireCustomerSubMenu().get(menukey).split(",");
																	for(int j=0; j<strEachSubmenu.length; j++){  
																		 if(strEachSubmenu[j].trim().equals(lastrule)){ %>
																		 <li><a href="#" onclick="javascript:fnGoToSubMenuPageJQ('<%=strEachSubmenu[j].trim()%>', '<%=menukey%>');return false;" class="slide-item"><b><span id='idnavsubmenu_<%=StringUtils.replace(strEachSubmenu[j], " ", "")%>'><%=strEachSubmenu[j].trim()%></span></b></a></li>
																		 <% } else { %>
																		 <li><a href="#" onclick="javascript:fnGoToSubMenuPageJQ('<%=strEachSubmenu[j].trim()%>', '<%=menukey%>');return false;" class="slide-item"> <span id='idnavsubmenu_<%=StringUtils.replace(strEachSubmenu[j], " ", "")%>'><%=strEachSubmenu[j].trim()%></span></a></li>
																		 <% }
																	} 
																}	
															%>							 
														 </ul>
													 </li>
												<% } %>
									
								<%}else if (user_leftmenu.getPricingPlanid().equals("1")){ 
									  for (int i=0;i<JSPUtilities.getBasicCustomerMenu().size();i++){ 
											String menukey = JSPUtilities.getBasicCustomerMenu().get(i).substring(0, JSPUtilities.getBasicCustomerMenu().get(i).indexOf(","));
											String menuvalue = JSPUtilities.getBasicCustomerMenu().get(i).substring(JSPUtilities.getBasicCustomerMenu().get(i).indexOf(",")+1); 
												
											if(lastaction.equals(menukey)){ %>
											 		<li class="slide is-expanded">
											 		<% if( JSPUtilities.getBasicCustomerSubMenu().get(menukey)!=null){ %>
														<a class="side-menu__item active" data-toggle="slide" href="#"><i class="<%=JSPUtilities.getBasicCustomerSubMenu().get(i)%>"></i><span class="side-menu__label" id='idnavmenu_<%=StringUtils.replace(menuvalue, " ", "")%>'><%=menuvalue %></span><i class="angle fa fa-angle-right"></i></a>
													<% }else { %>
														<a class="side-menu__item active" href="#" onclick="javascript:fnGoToSubMenuPageJQ('<%=menuvalue%>', '<%=menukey%>');return false;"><i class="<%=JSPUtilities.getBasicCustomerMenuIcons().get(i)%>"></i><span class="side-menu__label" id='idnavmenu_<%=StringUtils.replace(menuvalue, " ", "")%>'><%=menuvalue %></span></a>
													<% } %>
														<% }else{ %>
														<li class="slide">
																<% if(JSPUtilities.getBasicCustomerSubMenu().get(menukey)!=null){ %>
																	<a class="side-menu__item" data-toggle="slide" href="#"><i class="<%=JSPUtilities.getBasicCustomerMenuIcons().get(i)%>"></i><span class="side-menu__label" id='idnavmenu_<%=StringUtils.replace(menuvalue, " ", "")%>'><%=menuvalue %></span><i class="angle fa fa-angle-right"></i></a>
																<% }else { %>
																	<a class="side-menu__item" href="#" onclick="javascript:fnGoToSubMenuPageJQ('<%=menuvalue%>', '<%=menukey%>');return false;"><i class="<%=JSPUtilities.getBasicCustomerMenuIcons().get(i)%>"></i><span class="side-menu__label" id='idnavmenu_<%=StringUtils.replace(menuvalue, " ", "")%>'><%=menuvalue %></span></a>
																<% } %>
														<% } %>

														 <ul class="slide-menu">
														<%	
																if(JSPUtilities.getBasicCustomerSubMenu().get(menukey)!=null) {	
																	String strEachSubmenu[] =   JSPUtilities.getBasicCustomerSubMenu().get(menukey).split(",");
																	for(int j=0; j<strEachSubmenu.length; j++){  
																		 if(strEachSubmenu[j].trim().equals(lastrule)){ %>
																		 <li><a href="#" onclick="javascript:fnGoToSubMenuPageJQ('<%=strEachSubmenu[j].trim()%>', '<%=menukey%>');return false;" class="slide-item"><b><span id='idnavsubmenu_<%=StringUtils.replace(strEachSubmenu[j], " ", "")%>'><%=strEachSubmenu[j].trim()%></span></b></a></li>
																		 <% } else { %>
																		 <li><a href="#" onclick="javascript:fnGoToSubMenuPageJQ('<%=strEachSubmenu[j].trim()%>', '<%=menukey%>');return false;" class="slide-item"> <span id='idnavsubmenu_<%=StringUtils.replace(strEachSubmenu[j], " ", "")%>'><%=strEachSubmenu[j].trim()%></span></a></li>
																		 <% }
																	} 
																}	
															%>							 
														 </ul>
													 </li>
												<% } %>
									
								<%}else if (user_leftmenu.getPricingPlanid().equals("2")){ 
									
									 for (int i=0;i<JSPUtilities.getPlatinumCustomerMenu().size();i++){ 
											String menukey = JSPUtilities.getPlatinumCustomerMenu().get(i).substring(0, JSPUtilities.getPlatinumCustomerMenu().get(i).indexOf(","));
											String menuvalue = JSPUtilities.getPlatinumCustomerMenu().get(i).substring(JSPUtilities.getPlatinumCustomerMenu().get(i).indexOf(",")+1); 
												
											if(lastaction.equals(menukey)){ %>
											 		<li class="slide is-expanded">
											 		<% if( JSPUtilities.getPlatinumCustomerSubMenu().get(menukey)!=null){ %>
														<a class="side-menu__item active" data-toggle="slide" href="#"><i class="<%=JSPUtilities.getPlatinumCustomerSubMenu().get(i)%>"></i><span class="side-menu__label" id='idnavmenu_<%=StringUtils.replace(menuvalue, " ", "")%>'><%=menuvalue %></span><i class="angle fa fa-angle-right"></i></a>
													<% }else { %>
														<a class="side-menu__item active" href="#" onclick="javascript:fnGoToSubMenuPageJQ('<%=menuvalue%>', '<%=menukey%>');return false;"><i class="<%=JSPUtilities.getPlatinumCustomerMenuIcons().get(i)%>"></i><span class="side-menu__label" id='idnavmenu_<%=StringUtils.replace(menuvalue, " ", "")%>'><%=menuvalue %></span></a>
													<% } %>
														<% }else{ %>
														<li class="slide">
																<% if(JSPUtilities.getPlatinumCustomerSubMenu().get(menukey)!=null){ %>
																	<a class="side-menu__item" data-toggle="slide" href="#"><i class="<%=JSPUtilities.getPlatinumCustomerMenuIcons().get(i)%>"></i><span class="side-menu__label" id='idnavmenu_<%=StringUtils.replace(menuvalue, " ", "")%>'><%=menuvalue %></span><i class="angle fa fa-angle-right"></i></a>
																<% }else { %>
																	<a class="side-menu__item" href="#" onclick="javascript:fnGoToSubMenuPageJQ('<%=menuvalue%>', '<%=menukey%>');return false;"><i class="<%=JSPUtilities.getPlatinumCustomerMenuIcons().get(i)%>"></i><span class="side-menu__label" id='idnavmenu_<%=StringUtils.replace(menuvalue, " ", "")%>'><%=menuvalue %></span></a>
																<% } %>
														<% } %>

														 <ul class="slide-menu">
														<%	
																if(JSPUtilities.getPlatinumCustomerSubMenu().get(menukey)!=null) {	
																	String strEachSubmenu[] =   JSPUtilities.getPlatinumCustomerSubMenu().get(menukey).split(",");
																	for(int j=0; j<strEachSubmenu.length; j++){  
																		 if(strEachSubmenu[j].trim().equals(lastrule)){ %>
																		 <li><a href="#" onclick="javascript:fnGoToSubMenuPageJQ('<%=strEachSubmenu[j].trim()%>', '<%=menukey%>');return false;" class="slide-item"><b><span id='idnavsubmenu_<%=StringUtils.replace(strEachSubmenu[j], " ", "")%>'><%=strEachSubmenu[j].trim()%></span></b></a></li>
																		 <% } else { %>
																		 <li><a href="#" onclick="javascript:fnGoToSubMenuPageJQ('<%=strEachSubmenu[j].trim()%>', '<%=menukey%>');return false;" class="slide-item"> <span id='idnavsubmenu_<%=StringUtils.replace(strEachSubmenu[j], " ", "")%>'><%=strEachSubmenu[j].trim()%></span></a></li>
																		 <% }
																	} 
																}	
															%>							 
														 </ul>
													 </li>
												<% } %>
									
								<%} %>
								</ul>
								<% if(user_leftmenu.getPricingPlanid().equals("") == false) {%>
								<div class="card rounded" style="width: 222px;background-color:#222d65 ;height: 150px;margin: auto;background-repeat: no-repeat; background-size: 64px; background-position: center bottom;">
						       		 <h3 class="text" style="display: flex;justify-content: center;color: #ffffff;font-size: 15px;margin-top: 15px;">Buy Porte Token</h3>
						       		 <img src="assets/images/crypto/porte.png" class="" alt="" style="width:53px;margin: auto;">
						       		 <button class="btn btn-default mt-3" style="width: 80px;display: flex;justify-content: center;height: 33px;text-align: center;
										font-size: 13px;margin: auto;background-color: #e1e7f0;" onclick="javascript:fnBuyCoin();return false;">Buy Coin</button> 
								</div>
								<% } %>
							
						</div>
					</div>
				</aside>
				<!--sidemenu end-->
			<form id="form-leftmenu" method="post">
				<input type="hidden" name="qs" value="">
				<input type="hidden" name="rules" value="">
				<input type="hidden" name="hdnlang" id="hdnlangnav" value="">          
	    	</form>
	<script>
	var username = '<%=topnavUserName%>';
	//console.log("User name is "+username);
		  function fnGoToSubMenuPageJQ(submenu,menu){
			$('#form-leftmenu').attr('action', 'ws');
			$('input[name="qs"]').val(menu);
			$('input[name="rules"]').val(submenu);
			$('#form-leftmenu input[name="hdnlang"]').val($('#lang_def').text());
			//$('#hdnlangnav').val($('#lang_def').text());
			$("#form-leftmenu").submit();
		}	
		  function fnBuyCoin(){
			  $('#form-leftmenu').attr('action', 'ws');
              $('input[name="qs"]').val('porte');
              $('input[name="rules"]').val('Buy Coin');
              $('#form-leftmenu input[name="hdnlang"]').val($('#lang_def').text());
              $("#form-leftmenu").submit();
		  }
	</script>
<%
}catch (Exception e){
	out.println ("Exception : "+e.getMessage());
}finally{
if(user_leftmenu!=null) user_leftmenu=null; if(lastaction!=null) lastaction=null; if(strsubmenu!=null) strsubmenu=null; if(lastrule!=null) lastrule=null; 
if(userType!=null) userType=null; 
}
%>