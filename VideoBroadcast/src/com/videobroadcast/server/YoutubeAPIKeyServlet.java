package com.videobroadcast.server;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.gwt.oauth2.client.Auth;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.googlecode.objectify.ObjectifyService;
import com.videobroadcast.shared.AuthData;

/**
 * 
 * Returns the Youtube API key which is necessary to make Youtube API calls
 * in the IMSLP embedding javascript. 
 * 
 * @author Tom
 *
 */

@SuppressWarnings("serial")
public class YoutubeAPIKeyServlet extends HttpServlet {
	
	private static final Logger log = Logger
			.getLogger(YoutubeAPIKeyServlet.class.getName());
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		System.out.println("Get API key");

		log.info("YoutubeAPIKeyServlet is running");

		try {
			
//			String output = req.getParameter("callback") + "(" + getDataAsJson() + ");";
//	
//			resp.setContentType("text/javascript");
//		          
//			PrintWriter out = resp.getWriter();
//			out.println(output);
//			System.out.println(output);
			
			log.info("YoutubeAPIKeyServlet has been executed");

		} catch (Exception ex) {
			ex.printStackTrace();
			log.warning("Get API key from YoutubeAPIKeyServlet failed!");
			log.warning("Reason: " + ex.getMessage());
		}
	}
	
	
	private String getDataAsJson() {
		
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("key", AuthData.API_KEY);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObj.toString();
	}

}
