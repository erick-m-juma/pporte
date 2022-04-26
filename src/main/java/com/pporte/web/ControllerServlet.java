package com.pporte.web;

import java.io.IOException;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.pporte.NeoBankEnvironment;
import com.pporte.dao.DataPool;
import com.pporte.utilities.JSPUtilities;
import com.pporte.utilities.Utilities;

import framework.v8.InternalWebServlet;

/**
 * Servlet implementation class ControllerServlet
 */
@WebServlet("/ws")
public class ControllerServlet extends InternalWebServlet implements Servlet {
	private static final long serialVersionUID = 1L;
    private ServletContext context = null;
    private String classname = ControllerServlet.class.getSimpleName();  
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ControllerServlet() {
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
		try {
			JSPUtilities.initAll();
		} catch (Exception e) {
			System.out.println(classname+ ": ************** Non-Critial ERROR : Failed to initialize JSPUtils *************");	
		}
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action =null;
		//super.service(request, response);
		try{
			if(request.getParameter("qs")!=null){
				action = request.getParameter("qs").trim();
			}else{
				throw new ServletException ("action is null");
			}
			NeoBankEnvironment.setComment(3,classname," method service : action = "+action+" and rules is "+ request.getParameter("rules")); //remove it after sometime 		
			super.service(request, response);

		}catch(Exception e){
			NeoBankEnvironment.setComment(1,classname," method service : Error is = "+e.getMessage() );
			try {
				Utilities.callException(request, response, this.context, e.getMessage());
			} catch (Exception e1) {
				NeoBankEnvironment.setComment(1,classname," method service : Error is = "+e1.getMessage() );
			}
		}finally {
			if(action != null) action = null;
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
			JSPUtilities.destroyAll();
		}catch(Exception e) {
			NeoBankEnvironment.setComment(1,classname," Error is = "+e.getMessage() );
		}

		try {
			super.destroy();
		}catch(Exception e) {
			NeoBankEnvironment.setComment(1,classname," Error is = "+e.getMessage() );
		}
	}

}
