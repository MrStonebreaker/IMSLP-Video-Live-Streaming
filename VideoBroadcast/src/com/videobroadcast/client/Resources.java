package com.videobroadcast.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dev.resource.impl.UrlResource;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

public interface Resources extends ClientBundle {

	Resources INSTANCE = GWT.create(Resources.class);
	
}
