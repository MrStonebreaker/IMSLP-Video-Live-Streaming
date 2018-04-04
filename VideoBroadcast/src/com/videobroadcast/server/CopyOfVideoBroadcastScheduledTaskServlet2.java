package com.videobroadcast.server;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
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
import com.google.appengine.api.datastore.Query;
import com.googlecode.objectify.ObjectifyService;
import com.videobroadcast.shared.AuthData;

/**
 * Garbage collection for datastore 2
 * 
 * Job 2: This servlet runs in a cron job to free the datastore from old
 * references to non-existing videos that have been deleted in the users'
 * Youtube accounts by the users. Users always have the possibility to delete their 
 * videos so we have to check from time to time if the datastore is still up-to-date.
 * 
 * @author Tom
 *
 */

@SuppressWarnings("serial")
public class CopyOfVideoBroadcastScheduledTaskServlet2 extends HttpServlet {
	// implements VideoBroadcastServiceScheduledTaskService
	
	private final static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private final static JacksonFactory JSON_FACTORY = new JacksonFactory();
	private static final String APPLICATION_NAME = "Music Live Broadcasting";
	
	private static YouTube youtube;
	
	private static final Logger log = Logger
			.getLogger(CopyOfVideoBroadcastScheduledTaskServlet2.class.getName());
	
	static {
        ObjectifyService.register(BroadcastEntity.class);
    }
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		System.out.println("Cron Job 2");

		try {

			log.info("Cron Job 2 \"Delete_old_references_to_non-existing_videos\" is running");

			youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName(APPLICATION_NAME).build();
			
			List<BroadcastEntity> broadcastList = ofy().load().type(BroadcastEntity.class).list();

			// First cast List<BroadcastEntity> to List<String> and put all ids in deletionList
			List<String> deletionList = new ArrayList<>();
			Iterator<BroadcastEntity> iterator = broadcastList.iterator();
			
			while (iterator.hasNext()) {
				BroadcastEntity broadcastEntity = iterator.next();
				deletionList.add(broadcastEntity.id);
			}

//			System.out.println(deletionList.toString().substring(1, deletionList.toString().length() - 1));
			
			YouTube.Videos.List videoList = youtube.videos().list("id");
			// It's important to put ALL ids in the videoList. Missing entries will be deleted.
			videoList.setId(deletionList.toString().substring(1, deletionList.toString().length() - 1)); 
			videoList.setKey(AuthData.API_KEY); // For unauthorized API calls OAuth 2.0 is not necessary
			VideoListResponse response = videoList.execute();
//			System.out.println(response.toPrettyString());
			
			// Check which videos still exist on Youtube and put their ids in existingVideoList
			List<String> existingVideosList = new ArrayList<>();
			for (int i=0; i < response.getItems().size(); i++) {
				existingVideosList.add(response.getItems().get(i).getId()); // Only existing videos have an id in the items field
//				System.out.println("Existing video " + i + ": " + response.getItems().get(i).getId());
			}
			
			log.info("Datastore before removing non-existing videos: " + deletionList.toString());
			deletionList.removeAll(existingVideosList); // Delete all ids of existing videos from deletionList
			
			log.info("Existing IMSLP videos on Youtube: " + existingVideosList.toString());
			log.info("Old database entities to delete: " + deletionList.toString());

			try {
				if (!deletionList.isEmpty()) {
					
//					Query q = new Query("BroadcastEntity");
					
					ofy().delete().type(BroadcastEntity.class).ids(deletionList.toString().substring(1, deletionList.toString().length() - 1));
					
//					ofy().delete().entity(deletionList.toString().substring(1, deletionList.toString().length() - 1)); // added
				
					log.info("Entries in database after deletion: " + ofy().load().type(BroadcastEntity.class).list().size());
				}
				else 
					log.info("Cron Job 2: No entries to delete");
				
				log.info("Cron Job 2 \"Delete_old_references_to_non-existing_videos\" has been executed");
			
			} catch(Exception e) {
				e.printStackTrace();
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.warning("Cron job 2 failed!");
			log.warning("Reason: " + ex.getMessage());
		}
	}
	
}
