#	c1             s1
#	  \           /
#	   sw1 --- sw2
#	  /           \
#	c2             s2
def pair(net):
	c1 = net.addHost('c1', ip='192.168.0.1')
	c2 = net.addHost('c2', ip='192.168.0.2')
	s1 = net.addHost('s1', ip='192.168.0.101')
	s2 = net.addHost('s2', ip='192.168.0.102')
	sw1 = net.addSwitch('sw1')
	sw2 = net.addSwitch('sw2')

	net.addLink(c1, sw1, bw=100)
	net.addLink(c2, sw1, bw=100)
	net.addLink(s1, sw2, bw=300)
	net.addLink(s2, sw2, bw=300)
	net.addLink(sw1, sw2, bw=1000)

	hosts = {"c1": c1, "c2": c2, "s1": s1, "s2": s2}
	return hosts

#	c1	   sw2     s1
#	  \   /   \   /
# 	   sw1     sw3
#	  /   \   /   \
#	c2	   sw4     s2
def cycle(net):
	c1 = net.addHost('c1', ip='192.168.0.1')
	c2 = net.addHost('c2', ip='192.168.0.2')
	s1 = net.addHost('s1', ip='192.168.0.101')
	s2 = net.addHost('s2', ip='192.168.0.102')
	sw1 = net.addSwitch('sw1')
	sw2 = net.addSwitch('sw2')
	sw3 = net.addSwitch('sw3')
	sw4 = net.addSwitch('sw4')

	net.addLink(c1, sw1, bw=1000)
	net.addLink(c2, sw1, bw=1000)
	net.addLink(s1, sw3, bw=300)
	net.addLink(s2, sw3, bw=300)
	net.addLink(sw1, sw2, bw=100)
	net.addLink(sw2, sw3, bw=100)
	net.addLink(sw3, sw4, bw=100)
	net.addLink(sw4, sw1, bw=100)

	hosts = {"c1": c1, "c2": c2, "s1": s1, "s2": s2}
	return hosts
                  
#	      sw5 --- sw7 --- sw6
#        /   \           /   \
#	  sw1	  sw2     sw3     sw4
#	 /   \   /   \   /   \   /   \
#	c1   c2 c3   c4 s1   s2 s3   s4
def fatTree(net):
	c1 = net.addHost('c1', ip='192.168.0.1')
	c2 = net.addHost('c2', ip='192.168.0.2')
	c3 = net.addHost('c3', ip='192.168.0.3')
	c4 = net.addHost('c4', ip='192.168.0.4')
	s1 = net.addHost('s1', ip='192.168.0.101')
	s2 = net.addHost('s2', ip='192.168.0.102')
	s3 = net.addHost('s3', ip='192.168.0.103')
	s4 = net.addHost('s4', ip='192.168.0.104')
	sw1 = net.addSwitch('sw1')
	sw2 = net.addSwitch('sw2')
	sw3 = net.addSwitch('sw3')
	sw4 = net.addSwitch('sw4')
	sw5 = net.addSwitch('sw5')
	sw6 = net.addSwitch('sw6')
	sw7 = net.addSwitch('sw7')

	net.addLink(c1, sw1, bw=100)
	net.addLink(c2, sw1, bw=100)
	net.addLink(c3, sw2, bw=100)
	net.addLink(c4, sw2, bw=100)
	net.addLink(s1, sw3, bw=300)
	net.addLink(s2, sw3, bw=300)
	net.addLink(s3, sw4, bw=300)
	net.addLink(s4, sw4, bw=300)
	net.addLink(sw1, sw5, bw=500)
	net.addLink(sw2, sw5, bw=500)
	net.addLink(sw3, sw6, bw=500)
	net.addLink(sw4, sw6, bw=500)
	net.addLink(sw5, sw7, bw=1000)
	net.addLink(sw6, sw7, bw=1000)

	hosts = {"c1": c1, "c2": c2, "c3": c3, "c4": c4, "s1": s1, "s2": s2, "s3": s3, "s4": s4}
	return hosts

#	        s4      s3
#           |       |
#	c1     sw2 --- sw5     s2
#	  \   /   \   /   \   /
#	   sw1 --- sw4 --- sw7
#	  /	  \   /   \   /   \
#	c2     sw3 --- sw6     s1
#           |       |
#           c3      c4
def mesh(net):
	c1 = net.addHost('c1', ip='192.168.0.1')
	c2 = net.addHost('c2', ip='192.168.0.2')
	c3 = net.addHost('c3', ip='192.168.0.3')
	c4 = net.addHost('c4', ip='192.168.0.4')
	s1 = net.addHost('s1', ip='192.168.0.101')
	s2 = net.addHost('s2', ip='192.168.0.102')
	s3 = net.addHost('s3', ip='192.168.0.103')
	s4 = net.addHost('s4', ip='192.168.0.104')
	sw1 = net.addSwitch('sw1')
	sw2 = net.addSwitch('sw2')
	sw3 = net.addSwitch('sw3')
	sw4 = net.addSwitch('sw4')
	sw5 = net.addSwitch('sw5')
	sw6 = net.addSwitch('sw6')
	sw7 = net.addSwitch('sw7')

	net.addLink(c1, sw1, bw=100)
	net.addLink(c2, sw1, bw=100)
	net.addLink(c3, sw3, bw=100)
	net.addLink(c4, sw6, bw=100)
	net.addLink(s1, sw7, bw=300)
	net.addLink(s2, sw7, bw=300)
	net.addLink(s3, sw5, bw=300)
	net.addLink(s4, sw2, bw=300)
	net.addLink(sw1, sw2, bw=100)
	net.addLink(sw2, sw5, bw=100)
	net.addLink(sw5, sw7, bw=100)
	net.addLink(sw7, sw6, bw=100)
	net.addLink(sw6, sw3, bw=100)
	net.addLink(sw3, sw1, bw=100)
	net.addLink(sw1, sw4, bw=100)
	net.addLink(sw2, sw4, bw=100)
	net.addLink(sw3, sw4, bw=100)
	net.addLink(sw5, sw4, bw=100)
	net.addLink(sw6, sw4, bw=100)
	net.addLink(sw7, sw4, bw=100)

	hosts = {"c1": c1, "c2": c2, "c3": c3, "c4": c4, "s1": s1, "s2": s2, "s3": s3, "s4": s4}
	return hosts

def set_topology(topology, net):
	if topology == 'pair':
		hosts = pair(net)
	elif topology == 'cycle':
		hosts = cycle(net)
	elif topology == 'fatTree':
		hosts = fatTree(net)
	elif topology == 'mesh':
		hosts = mesh(net)
	return hosts