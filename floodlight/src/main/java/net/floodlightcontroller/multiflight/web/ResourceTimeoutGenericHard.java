package net.floodlightcontroller.multiflight.web;

import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.multiflight.EventIP;

public class ResourceTimeoutGenericHard extends ServerResource {
    protected static Logger log = LoggerFactory.getLogger(ResourceTimeoutGenericHard.class);
     
    @Put
    public String setTimeoutProfileHard(String input){
        String res = EventIP.setTimeoutProfileHard(Integer.parseInt(input));
        log.info(res);
        return res;
    }
}

