package com.videobroadcast.server;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import com.googlecode.objectify.ObjectifyService;
import com.videobroadcast.shared.AuthData;

/**
 * 
 * @author Tom
 *
 */

@SuppressWarnings("serial")
public class GiveMeThumbnailServlet extends HttpServlet {
	// implements VideoBroadcastServiceScheduledTaskService
	
	private final static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private final static JacksonFactory JSON_FACTORY = new JacksonFactory();
	private static final String APPLICATION_NAME = "Music Live Broadcasting";
	
	private static YouTube youtube;
	
	private static final Logger log = Logger
			.getLogger(GiveMeThumbnailServlet.class.getName());
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		try {


			youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName(APPLICATION_NAME).build();
			
			
			YouTube.Videos.List videoList = youtube.videos().list("id");
			// It's important to put ALL ids in the videoList. Missing entries will be deleted.
			videoList.setId("d3ZxkA6rNPI");
			videoList.setKey(AuthData.API_KEY); // For unauthorized API calls OAuth 2.0 is not necessary
			VideoListResponse response = videoList.execute();
//			System.out.println(response.toPrettyString());
			
			String output = response.getItems().get(0).getSnippet().getThumbnails().getDefault().getUrl();
			
			PrintWriter out = resp.getWriter();
			out.println(output);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.warning("Cron job 2 failed!");
			log.warning("Reason: " + ex.getMessage());
		}
	}
	
}
