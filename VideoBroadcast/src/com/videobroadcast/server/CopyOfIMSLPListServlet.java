package com.videobroadcast.server;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoStatistics;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.googlecode.objectify.ObjectifyService;
import com.videobroadcast.shared.AuthData;

/**
 * 
 * Returns the list of broadcasts + additional information for a requested IMSLP
 * piece
 * 
 * @author Tom
 *
 */

@SuppressWarnings("serial")
public class CopyOfIMSLPListServlet extends HttpServlet {
	
	private final static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private final static JacksonFactory JSON_FACTORY = new JacksonFactory();
	private static final String APPLICATION_NAME = "Music Live Broadcasting";
	
	private static YouTube youtube;
	
	private static final Logger log = Logger
			.getLogger(CopyOfIMSLPListServlet.class.getName());
	
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	static {
        ObjectifyService.register(BroadcastEntity.class);
    }

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		System.out.println("Get broadcast list");

		log.info("IMSLPListServlet is running");

//		resp.addHeader("Access-Control-Allow-Origin", "*");
//		resp.addHeader("Content-Type", "text/csv");
//		resp.getWriter().append(csvString);
		
		youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName(APPLICATION_NAME).build();
		
		try {
			
			String jsonData = getDataAsJson(req.getParameter("title"));
			String output = req.getParameter("callback") + "(" + jsonData + ");";
//			resp.setHeader("Access-Control-Allow-Origin", "*");
//			resp.setHeader("Access-Control-Allow-Methods", "GET");
//			resp.setHeader("Access-Control-Allow-Credentials", "true");
	
			resp.setContentType("text/javascript");
//			resp.setContentType("application/json");
		          
			PrintWriter out = resp.getWriter();
			out.println(output);
			System.out.println(output);
			
			log.info("IMSLPListServlet has been executed");

		} catch (Exception ex) {
			ex.printStackTrace();
			log.warning("Get Broadcast list of IMSLPListServlet failed!");
			log.warning("Reason: " + ex.getMessage());
		}
	}
	
	
	private String getDataAsJson(String title) {
		
		List<BroadcastEntity> resultList = searchInDatabase(title); 
		List<BroadcastEntity> sortedList = sortResultList(resultList);
		
		JSONArray jsonArray = new JSONArray();
		
		System.out.println("Create JSONObject and sort list");
		
		/* Create JSONObject and sort list */
		try {
			for (BroadcastEntity entity:sortedList) {
				String id = entity.id;

				
				JSONObject jsonObj = new JSONObject();
				
				
				YouTube.Videos.List videoList = youtube.videos().list("status, statistics");
				videoList.setId(id);
				videoList.setKey(AuthData.API_KEY); // For unauthorized API calls OAuth 2.0 is not necessary
				VideoListResponse response = videoList.execute();
				
				System.out.println(response);
				
				VideoStatistics statistics = response.getItems().get(0).getStatistics();
				
				jsonObj.put("id", id);
				jsonObj.put("startTime", entity.startTime);
				jsonObj.put("endTime", entity.endTime);
				jsonObj.put("lifeCycleStatus", entity.lifeCycleStatus);
				jsonObj.put("channelName", entity.channelName);
				jsonObj.put("viewCount", statistics.getViewCount());
				jsonObj.put("likeCount", statistics.getLikeCount());
				jsonObj.put("dislikeCount", statistics.getDislikeCount());
				
				jsonArray.put(jsonObj);
				
//				if (entity.lifeCycleStatus.equals("live")) {
//					jsonObj.put("lifeCycleStatus", "live");
//					jsonArray.put(0, jsonObj);		// Put broadcasts that are currently live on the first position
//				} else { 
//					jsonObj.put("lifeCycleStatus", "complete");
//					jsonArray.put(jsonObj);
//				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSONObject mainObj = new JSONObject();
		try {
			mainObj.put("results", jsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
//		parameter = "{'videos': [{'id':'5', 'id2':7}]}";
		return mainObj.toString();
	}

	private List<BroadcastEntity> searchInDatabase(String title) {

		log.info("Search for broadcasts of the piece: " + title);
		System.out.println("Search for broadcasts of the piece: " + title);
		
		List<BroadcastEntity> broadcastList = ofy().load().type(BroadcastEntity.class).list();
		Iterator<BroadcastEntity> iterator = broadcastList.iterator();
		List<BroadcastEntity> resultList = new ArrayList<BroadcastEntity>();
		
		while (iterator.hasNext()) {
			BroadcastEntity entity = iterator.next();
			if (entity.piece.equals(title)) {
				resultList.add(entity); // Add broadcast id to resultList
			}
		}
		
		System.out.println("Searched for broadcasts of the piece: " + title);
		
		return resultList;
	}
	
	private List<BroadcastEntity> sortResultList(List<BroadcastEntity> resultList) {
		// Sort list according to the broadcasts' startTime
		Collections.sort(resultList, Collections.reverseOrder());
		return resultList;
	}
	
}

