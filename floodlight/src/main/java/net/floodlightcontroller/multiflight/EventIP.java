package net.floodlightcontroller.multiflight;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.projectfloodlight.openflow.protocol.OFFactories;
import org.projectfloodlight.openflow.protocol.OFFactory;
import org.projectfloodlight.openflow.protocol.OFFlowAdd;
import org.projectfloodlight.openflow.protocol.OFMeterFlags;
import org.projectfloodlight.openflow.protocol.OFMeterMod;
import org.projectfloodlight.openflow.protocol.OFMeterModCommand;
import org.projectfloodlight.openflow.protocol.OFPacketOut;
import org.projectfloodlight.openflow.protocol.OFVersion;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.protocol.meterband.OFMeterBand;
import org.projectfloodlight.openflow.protocol.meterband.OFMeterBandDrop;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFBufferId;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.TransportPort;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.types.NodePortTuple;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.SwitchPort;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.TCP;
import net.floodlightcontroller.packet.UDP;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.routing.Path;
import net.floodlightcontroller.util.FlowModUtils;

public class EventIP {

	private static IRoutingService _routingService;
	
	private static int TIMEOUT_HOST_IDLE = 1800;
	private static int TIMEOUT_GENERIC_HARD = 300;
	private static int TIMEOUT_GENERIC_IDLE = 30;
	
    private final static Boolean HOST = false;
    private final static Boolean APP = true;

    private static Boolean PROFILE_MODE = APP;

	private Ethernet _ethIn;
	private OFFactory _factory;

	public static void setSwitchingServices(IRoutingService routingService){
		_routingService = routingService;
	}

	public static String setProfileMode(String mode){
		if(mode.equals("host")){
			PROFILE_MODE = HOST;
			return mode;
		}
		else if(mode.equals("app")){
			PROFILE_MODE = APP;
			return mode;
		}
		else return "Unknown Mode! Please set either 'host' or 'app' modes.";
	}

	public static String setTimeoutHosts(int seconds){
		if(seconds > 0){
			TIMEOUT_HOST_IDLE = seconds;
			return Integer.toString(seconds);
		}
		else return "Timeout value must be higher than 0.";
	}

	public static String setTimeoutProfileHard(int seconds){
		if(seconds > 0){
			TIMEOUT_GENERIC_HARD = seconds;
			return Integer.toString(seconds);
		}
		else return "Timeout value must be higher than 0.";
	}

	public static String setTimeoutProfileIdle(int seconds){
		if(seconds > 0){
			TIMEOUT_GENERIC_IDLE = seconds;
			return Integer.toString(seconds);
		}
		else return "Timeout value must be higher than 0.";
	}

	public EventIP(Ethernet ethIn, OFFactory factory){
		_ethIn = ethIn;
		_factory = factory;
	}

	void process(){
		IDevice deviceTo = QueryDevice.getDevice(_ethIn.getDestinationMACAddress());
        if(deviceTo != null){
			SwitchPort[] swsTo = deviceTo.getAttachmentPoints();
			if(swsTo.length > 0){
				SwitchPort swTupleTo = swsTo[0];
				_packetRelay(deviceTo, swTupleTo);
				_flowRegistration(deviceTo, swTupleTo);
			}
		}
	}

	private void _setLastSwForwarding(MacAddress host, SwitchPort swTuple){
		Match lastSwMatch = _factory.buildMatch()
			.setExact(MatchField.ETH_DST, host)
			.build();

		ArrayList<OFAction> lastSwAction = new ArrayList<OFAction>();
		lastSwAction.add(_factory.actions().buildOutput()
			.setMaxLen(0xFFffFFff)
			.setPort(swTuple.getPortId())
			.build());

		OFFlowAdd lastSwFlow = _factory.buildFlowAdd()
			.setBufferId(OFBufferId.NO_BUFFER)
            .setIdleTimeout(TIMEOUT_HOST_IDLE)
            .setMatch(lastSwMatch)
			.setActions(lastSwAction)
			.setPriority(FlowModUtils.PRIORITY_HIGH)
            .build();
        
		QuerySwitch.getIOFSwitch(swTuple.getNodeId()).write(lastSwFlow);
    }

	private void _packetRelay(IDevice deviceTo, SwitchPort swTupleTo){
		_setLastSwForwarding(deviceTo.getMACAddress(), swTupleTo);
		
		byte[] byteOut = _ethIn.serialize();

		ArrayList<OFAction> relayAction = new ArrayList<OFAction>();
		relayAction.add(_factory.actions().buildOutput()
			.setMaxLen(0xFFffFFff)
			.setPort(swTupleTo.getPortId())
			.build());

		OFPacketOut po = _factory.buildPacketOut()
			.setData(byteOut)
			.setActions(relayAction)
			.setInPort(OFPort.CONTROLLER)
			.build();
		
		QuerySwitch.getIOFSwitch(swTupleTo.getNodeId()).write(po);
	}

	private void _flowRegistration(IDevice deviceTo, SwitchPort swTupleTo){
		if(_ethIn.getEtherType() == EthType.IPv4){
			IPv4 ipIn = (IPv4) _ethIn.getPayload();

			TransportPort srcPort = null;
			TransportPort dstPort = null;

			if(PROFILE_MODE == APP){
				if(ipIn.getProtocol() == IpProtocol.TCP){
					TCP tcpIn = (TCP) ipIn.getPayload();
					srcPort = tcpIn.getSourcePort();
					dstPort = tcpIn.getDestinationPort();
				}
				else if(ipIn.getProtocol() == IpProtocol.UDP){
					UDP udpIn = (UDP) ipIn.getPayload();
					srcPort = udpIn.getSourcePort();
					dstPort = udpIn.getDestinationPort();
				}
				else if(ipIn.getProtocol() == IpProtocol.ICMP){
					// Prevent return
				}	
				else return; // Unsupported protocols
			}

			IDevice deviceFrom = QueryDevice.getDevice(_ethIn.getSourceMACAddress());
			SwitchPort swTupleFrom = deviceFrom.getAttachmentPoints()[0];

			DatapathId src_sw = swTupleFrom.getNodeId();
			DatapathId dst_sw = swTupleTo.getNodeId();
			
			_routingService.forceRecompute();
			Path path = _routingService.getPath(src_sw, dst_sw);
			for(int i = 0; i < path.getPath().size(); i++){
				if(i % 2 != 0) continue;
				_setForwarding(path.getPath().get(i), deviceFrom.getMACAddress(), srcPort, deviceTo.getMACAddress(), dstPort, ipIn.getProtocol());
			}
		}
	}

	private void _setForwarding(NodePortTuple tuple, MacAddress src_mac, TransportPort src_port, MacAddress dst_mac, TransportPort dst_port, IpProtocol proto){
		Match.Builder routeMatch = _factory.buildMatch()
			.setExact(MatchField.ETH_DST, dst_mac)
			.setExact(MatchField.ETH_SRC, src_mac)
			.setExact(MatchField.ETH_TYPE, EthType.IPv4);
		
		if(PROFILE_MODE == APP){
			routeMatch.setExact(MatchField.IP_PROTO, proto);
	
			if(proto == IpProtocol.TCP){
				routeMatch.setExact(MatchField.TCP_SRC, src_port)
					.setExact(MatchField.TCP_DST, dst_port);
			}
			else if(proto == IpProtocol.UDP){
				routeMatch.setExact(MatchField.UDP_SRC, src_port)
					.setExact(MatchField.UDP_DST, dst_port);
			}
		}

		ArrayList<OFAction> routeAction = new ArrayList<OFAction>();
		routeAction.add(_factory.actions().buildOutput()
			.setMaxLen(0xFFffFFff)
			.setPort(tuple.getPortId())
			.build());
	
		OFFlowAdd routeFlow = _factory.buildFlowAdd()
			.setBufferId(OFBufferId.NO_BUFFER)
			.setHardTimeout(TIMEOUT_GENERIC_HARD)
			.setIdleTimeout(TIMEOUT_GENERIC_IDLE)
			.setMatch(routeMatch.build())
			.setActions(routeAction)
			.setPriority(FlowModUtils.PRIORITY_HIGH)
			.build();

		QuerySwitch.getIOFSwitch(tuple.getNodeId()).write(routeFlow);
	}
}