package com.rokolabs.app.rokostickers.data;

import java.util.List;

public class PackIconFileGroup
{
    public List<ImageFile> files;
    public int             objectId;
    
    public String getFirstUrl(){
    	try{
    		return files.get(0).file.url;
    	}catch(Exception e){
    		return null;
    	}
    }
}
