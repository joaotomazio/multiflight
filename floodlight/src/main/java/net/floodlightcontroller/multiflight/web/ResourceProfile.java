package net.floodlightcontroller.multiflight.web;

import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.multiflight.EventIP;

public class ResourceProfile extends ServerResource {
    protected static Logger log = LoggerFactory.getLogger(ResourceProfile.class);
     
    @Put
    public String setProfileMode(String input){
        String res = EventIP.setProfileMode(input);
        log.info(res);
        return res;
    }
}

