package net.floodlightcontroller.multiflight.web;

import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.multiflight.EventIP;

public class ResourceTimeoutGenericIdle extends ServerResource {
    protected static Logger log = LoggerFactory.getLogger(ResourceTimeoutGenericIdle.class);
     
    @Put
    public String setTimeoutProfileIdle(String input){
        String res = EventIP.setTimeoutProfileIdle(Integer.parseInt(input));
        log.info(res);
        return res;
    }
}

