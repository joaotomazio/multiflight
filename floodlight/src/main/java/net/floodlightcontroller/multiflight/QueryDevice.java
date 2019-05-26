package net.floodlightcontroller.multiflight;

import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.MacAddress;

import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.IDeviceService;

public class QueryDevice {

	private static IDeviceService _deviceService;

	static void setDeviceService(IDeviceService deviceService){
		_deviceService = deviceService;
	};

	static IDevice getDevice(MacAddress mac){
		IDevice host = null;
		for(IDevice device : _deviceService.getAllDevices()){
			if(device.getMACAddress().equals(mac)){
				host = device;
			}
		}
		return host;
	}
	
	static IDevice getDevice(IPv4Address ip){
		IDevice host = null;
		for(IDevice device : _deviceService.getAllDevices()){
			IPv4Address[] ips = device.getIPv4Addresses();
			if(ips.length > 0){
				if(ips[0].equals(ip)) host = device;
			}
		}
		return host;
	}
}

