package net.floodlightcontroller.multiflight;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.projectfloodlight.openflow.protocol.OFFactory;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.types.EthType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.multiflight.web.MultiflightRestlet;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.routing.IRoutingService;

public class Multiflight implements IFloodlightModule, IOFMessageListener {

    //Services
    private static IFloodlightProviderService _floodlightProvider;
    private static IRestApiService _restApiService;
    
    public static final Logger logger = LoggerFactory.getLogger(Multiflight.class);

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies(){
        Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
        l.add(IFloodlightProviderService.class);
        l.add(IOFSwitchService.class);
        l.add(IDeviceService.class);
        l.add(IRoutingService.class);
        l.add(IRestApiService.class);
        return l;
    }

    @Override
    public void init(FloodlightModuleContext ctx) throws FloodlightModuleException{
        _floodlightProvider = ctx.getServiceImpl(IFloodlightProviderService.class);
        QuerySwitch.setSwitchService(ctx.getServiceImpl(IOFSwitchService.class));
        QueryDevice.setDeviceService(ctx.getServiceImpl(IDeviceService.class));
        EventIP.setSwitchingServices(ctx.getServiceImpl(IRoutingService.class));
        _restApiService = ctx.getServiceImpl(IRestApiService.class);
        logger.info("MULTIFLIGHT Module initialized!");
    }

    @Override
    public void startUp(FloodlightModuleContext ctx){
        _floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
        _restApiService.addRestletRoutable(new MultiflightRestlet());
    }

    @Override
    public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx){
        OFFactory factory = sw.getOFFactory();
        switch(msg.getType()){
            case PACKET_IN:
                Ethernet ethIn = IFloodlightProviderService.bcStore
                    .get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
                if(ethIn.getEtherType() == EthType.ARP)
                    new EventArp(ethIn, factory).process();
                else
                    new EventIP(ethIn, factory).process();                  
                break;
            default:
                break;
        }
        return Command.CONTINUE;
    }

	@Override
    public boolean isCallbackOrderingPrereq(OFType type, String name) {
        return 
            (type.equals(OFType.PACKET_IN) &&
            (name.equals("topology") || name.equals("devicemanager")));
    }

    @Override
    public boolean isCallbackOrderingPostreq(OFType type, String name) {
        return false;
    }

	@Override
	public String getName() {
		return "Multiflight";
	}
}