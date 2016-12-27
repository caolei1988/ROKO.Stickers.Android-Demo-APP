package com.rokolabs.app.rokostickers.data;

import java.util.ArrayList;
import java.util.List;

public class ShareSettings
{
	public static final String CHANNEL_FACEBOOK = "facebook";
	public static final String CHANNEL_TWITTER = "twitter";
	public static final String CHANNEL_EMAIL = "email";
	public static final String CHANNEL_SMS = "sms";
	
    public String backgroundColor;
    public String createDate;
    public String updateDate;
    public Preview       preview = new Preview();
    public Navigation    navigation;
    public List<Channel> channels;
    
    //public String contentId = UUID.randomUUID().toString();
	public String contentId;
    public String contentType = "";

    public Channel getChannel(String channelType){
    	if (channels != null){
    		for(Channel ch : channels){
    			if(ch.channelType != null && ch.channelType.equals(channelType))
    				return ch;
    		}
    	}
    	return null;
    }
    
    public void setMessageBody(String msg){
    	if(channels != null){
    		for(Channel ch : channels){
				ch.bodyText = msg;
//    			if(CHANNEL_EMAIL.equals(ch.channelType))
//    				ch.headerText = msg;
//    			else
//    				ch.bodyText = msg;
    		}    		
    	}
    	
    }
    
    public List<Channel> getEnabledChannels(){
    	List<Channel> list = new ArrayList<Channel>();
    	if(channels != null){
    		for(Channel c : channels){
    			if(c.enabled)
    				list.add(c);
    		}
    	}
    	return list;
    }
}
