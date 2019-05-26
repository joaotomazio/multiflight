package net.floodlightcontroller.multiflight;

import java.util.Set;

import org.projectfloodlight.openflow.types.DatapathId;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.internal.IOFSwitchService;

public class QuerySwitch {

	private static IOFSwitchService _switchService;

	static void setSwitchService(IOFSwitchService switchService){
		_switchService = switchService;
	};

	static Set<DatapathId> getSwitchSet(){
		return _switchService.getAllSwitchMap().keySet();
	}

	static IOFSwitch getIOFSwitch(DatapathId dpid){
		return _switchService.getSwitch(dpid);
	}
}