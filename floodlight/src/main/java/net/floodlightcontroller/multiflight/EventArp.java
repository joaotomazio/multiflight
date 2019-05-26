package net.floodlightcontroller.multiflight;

import java.util.Collections;

import org.projectfloodlight.openflow.protocol.OFFactory;
import org.projectfloodlight.openflow.protocol.OFPacketOut;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.types.ArpOpcode;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFPort;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.devicemanager.SwitchPort;
import net.floodlightcontroller.packet.ARP;
import net.floodlightcontroller.packet.Ethernet;

public class EventArp {

	private Ethernet _ethIn;
	private OFFactory _factory;

	EventArp(Ethernet ethIn, OFFactory factory){
		_ethIn = ethIn;
		_factory = factory;
	}

	private void _handleRequest(ARP arpIn){
		if(!arpIn.getTargetHardwareAddress().equals(MacAddress.of(1))){
			arpIn.setTargetHardwareAddress(MacAddress.of(1));

			byte[] byteOut = _ethIn.serialize();
			OFPacketOut po = _factory.buildPacketOut()
				.setData(byteOut)
				.setActions(Collections.singletonList((OFAction) _factory.actions().output(OFPort.FLOOD, 0xffFFffFF)))
				.setInPort(OFPort.CONTROLLER)
				.build();

			for(DatapathId dpidTo : QuerySwitch.getSwitchSet()){
				IOFSwitch swTo = QuerySwitch.getIOFSwitch(dpidTo);
				swTo.write(po);
			}
		}
	}

	private void _handleReply(ARP arpIn){
		SwitchPort[] sws = QueryDevice.getDevice(arpIn.getTargetHardwareAddress()).getAttachmentPoints();
		if(sws.length > 0){
			SwitchPort swPort = sws[0];
			IOFSwitch requesterSw = QuerySwitch.getIOFSwitch(swPort.getNodeId());

			byte[] byteOut = _ethIn.serialize();
			OFPacketOut po = _factory.buildPacketOut()
				.setData(byteOut)
				.setActions(Collections.singletonList((OFAction) _factory.actions().output(swPort.getPortId(), 0xffFFffFF)))
				.setInPort(OFPort.CONTROLLER)
				.build();
			requesterSw.write(po);
		}
	}

	void process(){
		ARP arpIn = (ARP) _ethIn.getPayload();
		if(arpIn.getOpCode().equals(ArpOpcode.REQUEST))
			_handleRequest(arpIn);
		else if(arpIn.getOpCode().equals(ArpOpcode.REPLY))
			_handleReply(arpIn);
		else return;
	}



}