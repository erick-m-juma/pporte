package com.pporte.web;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pporte.NeoBankEnvironment;
import com.pporte.dao.DataPool;

import framework.v8.InternalJSONServlet;

/**
 * Servlet implementation class JSONServlet
 */
@WebServlet("/json")
public class JSONServlet extends InternalJSONServlet implements Servlet {
	private static final long serialVersionUID = 1L;
    private ServletContext context = null;
    private String classname = JSONServlet.class.getSimpleName();         
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public JSONServlet() {
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
		String action = null; String jsonString = null; JsonObject jsonObj = null; String rules = null; Map<String, String> headerMap  = null;
		Gson gson = new Gson();
		try{
			headerMap  = getHeadersInfo(request);
			if(headerMap.containsKey("x-api-key") == true) {
				if(headerMap.get("x-api-key").equals(NeoBankEnvironment.getAPIKeyPublic()) == false) 	throw new Exception ("api key value mismatch in header");
			}else {
				throw new Exception ("api key missing in header");
			}
			if(request.getParameter("objarray")!=null){
					jsonString = request.getParameter("objarray").trim();
					NeoBankEnvironment.setComment(3,classname," method service : jsonString = "+jsonString );
			}else	throw new Exception ("Missing JSON String.. cannot progress further");
			jsonObj = new Gson().fromJson(jsonString, JsonObject.class);
			/*
			 * 
			 * if(jsonObj.get("apikey")!=null && (
			 * jsonObj.get("apikey").toString().replaceAll("\"",
			 * "").equals(NeoBankEnvironment.getAPIKeyPublic()) )) { }else {
			 * response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE); throw new
			 * Exception ("API Key is not valid "); }
			 */
					action = jsonObj.get("qs").toString().replaceAll("\"", "");
					rules = jsonObj.get("rules").toString().replaceAll("\"", "");
					
					request.setAttribute("rules", rules);
					request.setAttribute("qs", action);
					super.service(request, response);
		}catch(Exception e){
			NeoBankEnvironment.setComment(1,classname," method service : Problem with delegate action.. action = "+action+" Error : "+e.getMessage() );
			if(e.getMessage().startsWith("api key")) { jsonObj  = new JsonObject();  jsonObj.add("error", gson.toJsonTree("true"));
			response.getWriter().print(  gson.toJson(jsonObj) );}
		}finally {
			if (jsonString!=null) jsonString = null; if(jsonObj!=null) jsonObj = null; if(action!=null) action = null; if(rules!=null) rules = null;
			if(headerMap.isEmpty() == false) headerMap.clear(); if(gson!=null) gson = null;
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
