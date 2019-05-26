package net.floodlightcontroller.multiflight.web;

import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.multiflight.EventIP;

public class ResourceTimeoutHosts extends ServerResource {
    protected static Logger log = LoggerFactory.getLogger(ResourceTimeoutHosts.class);
     
    @Put
    public String setTimeoutHosts(String input){
        String res = EventIP.setTimeoutHosts(Integer.parseInt(input));
        log.info(res);
        return res;
    }
}

