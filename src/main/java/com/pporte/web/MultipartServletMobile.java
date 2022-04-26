package com.pporte.web;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pporte.NeoBankEnvironment;
import com.pporte.dao.DataPool;
import framework.v8.InternalMultiPartServlet;

/**
 * Servlet implementation class ControllerServlet
 */
@WebServlet("/ps")
@MultipartConfig(fileSizeThreshold=1024*1024*40, 	// 20 MB 
maxFileSize=1024*1024*50,      	// 50 MB
maxRequestSize=1024*1024*100)   	// 100 MB  
public class MultipartServletMobile extends InternalMultiPartServlet implements Servlet {
	private static final long serialVersionUID = 1L;
    private ServletContext context = null;
    private String classname = MultipartServletMobile.class.getSimpleName(); 
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MultipartServletMobile() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
    public void init(ServletConfig config) throws ServletException {
    	try {
    		super.init(config);
    	}catch (Exception e) {
    		System.out.println(classname+ " Error is "+e.getMessage());
    	}
		try {
			if(NeoBankEnvironment.getInstance()==null) {
				System.out.println(classname+ ": ************** inside init() method, 2. Staring NeoBankEnvironment.init() *************");	
					NeoBankEnvironment.init();
			}
			} catch (Exception e) {
			System.out.println(classname+ ": ************** CRITICAL ERROR : Failed to initialize core Environment parameters *************");	
		}
		try {
			DataPool.initDBPool();
		}catch (Exception e) {
			System.out.println(classname+ ": ************** CRITICAL ERROR : Failed to initialize Database *************");	
		}
		if(this.context ==null) {
			this.context = getServletContext();			
			super.setContext(this.context);	
			NeoBankEnvironment.setComment(3,classname,"============== starting for first time :" +classname );
			}		
		}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action =null; JsonObject jsonObj = null; Gson gson = new Gson();
	
		//super.service(request, response);
		try{
			// check headers
			/*
			headerMap  = getHeadersInfo(request);
		    
			 if(headerMap.containsKey("x-api-key") == true) {
				 apiKey = headerMap.get("x-api-key");
				 NeoBankEnvironment.setComment(3, className,"apikey is "+ apiKey);
			 }else if(request.getParameter("hdnapikey")!=null){ 
				 apiKey = request.getParameter("hdnapikey");
			 }
			 
			if(apiKey.equals(NeoBankEnvironment.getAPIKeyPublic()) == false) {
				response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
				throw new Exception ("api key value mismatch in header");
			}*/
				
			if(request.getParameter("qs")!=null){
				action = request.getParameter("qs").trim();
			}else{
				throw new ServletException ("action is null");
			}
			NeoBankEnvironment.setComment(3,classname," method service : action = "+action+" and rules is "+ request.getParameter("rules")); //remove it after sometime 		
			super.service(request, response);
		}catch(Exception e){
			if(e.getMessage().startsWith("api key")) { jsonObj  = new JsonObject();  jsonObj.add("error", gson.toJsonTree("true"));
			response.getWriter().print(  gson.toJson(jsonObj) );}
			NeoBankEnvironment.setComment(1,classname," method service : Error is = "+e.getMessage() );
		}finally {
			if(action != null) action = null;  if(jsonObj!=null) jsonObj = null; if(gson!=null) gson = null;
		}
	}


	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {		
		try {
			DataPool.destroyDBPool();
		} catch (Exception e) {
			NeoBankEnvironment.setComment(1,classname," Error is = "+e.getMessage() );
		}

		try {
			super.destroy();
		}catch(Exception e) {
			NeoBankEnvironment.setComment(1,classname," Error is = "+e.getMessage() );
		}
	}

    @SuppressWarnings("unused")
	private Map<String, String> getHeadersInfo(HttpServletRequest request) throws Exception{
        Map<String, String> map = new HashMap<String, String>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }
}

