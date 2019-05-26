package net.floodlightcontroller.multiflight.web;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import net.floodlightcontroller.restserver.RestletRoutable;

public class MultiflightRestlet implements RestletRoutable {

    @Override
    public Restlet getRestlet(Context context) {
        Router router = new Router(context);
        router.attach("/profile", ResourceProfile.class);
        router.attach("/timeouts/last", ResourceTimeoutHosts.class);
        router.attach("/timeouts/generic/hard", ResourceTimeoutGenericHard.class);
        router.attach("/timeouts/generic/idle", ResourceTimeoutGenericIdle.class);
        return router;
    }
 
    @Override
    public String basePath() {
        return "/wm/multiflight";
    }
}