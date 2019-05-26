#!/usr/bin/python

import sys

from functools import partial
from mininet.net import Mininet
from mininet.node import OVSSwitch, RemoteController
from mininet.cli import CLI
from mininet.link import TCLink
from mininet.util import quietRun

from topologies import set_topology
from modes import set_mode
from conditions import set_condition

def main():
	controller_ip = sys.argv[1]
	controller_port = eval(sys.argv[2])
	openflow_v = sys.argv[3]
	topology = sys.argv[4]
	lib = sys.argv[5]
	mode = sys.argv[6]
	if mode != 'cli':
		state = sys.argv[7]
		transport = sys.argv[8]
		iteration = sys.argv[9]

	switch = partial(OVSSwitch, protocols=openflow_v)
	net = Mininet(controller=None, link=TCLink, switch=switch)
	net.addController('c0', controller=RemoteController, ip=controller_ip, port=controller_port)
	
	hosts = set_topology(topology, net)

	net.start()

	set_condition(hosts, topology, mode, state, transport)

	if mode == 'cli':
		CLI(net)
	else:
		set_mode(hosts, topology, lib, mode, state, transport, iteration)

	net.stop()

if __name__ == '__main__':
	main()
