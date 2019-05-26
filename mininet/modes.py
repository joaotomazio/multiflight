import os
from time import sleep

def path(topology, lib, mode, state, transport, tag_i, i):
	try:
		if not os.path.exists('results/raw/'):
			os.makedirs('results/raw/')
	except OSError:
		print('Error: Creating directory. ' + path)
	
	tag = ''
	if mode == 'single':
		tag = 'c'
	elif mode == 'sequence':
		tag = 'seq' + str(tag_i)
	elif mode == 'burst':
		tag = 'bur' + str(tag_i)
	elif mode == 'parallel':
		tag = 'c' + str(tag_i)
	else:
		tag = 'error'

	path = 'results/raw/' + topology + '-' + lib + '-' + mode + '-' + state + '-' + transport + '-' + tag + '-' + str(i) + '.dat'
	try:
		os.remove(path)
	except OSError:
		pass
	return path

def flags(transport):
	if transport == 'udp':
		server_flag = '-u '
		client_flag = '-u -b 100m '
	elif transport == 'tcp':
		server_flag = ''
		client_flag = ''
	return server_flag, client_flag

def test_single(hosts, topology, lib, state, transport, i):
	server_flag, client_flag = flags(transport)
	hosts["s1"].cmd('iperf -s ' + server_flag + '&')
	hosts["c1"].cmd('iperf -f m -i 1 -c 192.168.0.101 ' + client_flag + '-t 10 -y C > ' + path(topology, lib, 'single', state, transport, None, i))

def test_sequence(hosts, topology, lib, state, transport, i):
	server_flag, client_flag = flags(transport)
	hosts["s1"].cmd('iperf -s ' + server_flag + '&')
	hosts["c1"].cmd('iperf -f m -i 1 -c 192.168.0.101 ' + client_flag + '-t 10 -y C > ' + path(topology, lib, 'sequence', state, transport, 1, i))
	hosts["c1"].cmd('iperf -f m -i 1 -c 192.168.0.101 ' + client_flag + '-t 10 -y C > ' + path(topology, lib, 'sequence', state, transport, 2, i))

def test_burst(hosts, topology, lib, state, transport, i):
	server_flag, client_flag = flags(transport)
	hosts["s1"].cmd('iperf -s ' + server_flag + '&')
	hosts["c1"].cmd('iperf -f m -i 1 -c 192.168.0.101 ' + client_flag + '-t 15 -y C > ' + path(topology, lib, 'burst', state, transport, 1, i) + ' &')
	sleep(5)
	hosts["c1"].cmd('iperf -f m -i 1 -c 192.168.0.101 ' + client_flag + '-t 15 -y C > ' + path(topology, lib, 'burst', state, transport, 2, i) + ' &')
	sleep(5)
	hosts["c1"].cmd('iperf -f m -i 1 -c 192.168.0.101 ' + client_flag + '-t 15 -y C > ' + path(topology, lib, 'burst', state, transport, 3, i) + ' &')
	sleep(17)

def test_parallel(hosts, topology, lib, state, transport, i):
	server_flag, client_flag = flags(transport)
	hosts["s1"].cmd('iperf -s ' + server_flag + '&')
	hosts["c1"].cmd('iperf -f m -i 1 -c 192.168.0.101 ' + client_flag + '-t 10 -y C > ' + path(topology, lib, 'parallel', state, transport, 1, i) + ' &')
	sleep(5)
	hosts["c2"].cmd('iperf -f m -i 1 -c 192.168.0.101 ' + client_flag + '-t 10 -y C > ' + path(topology, lib, 'parallel', state, transport, 2, i) + ' &')
	sleep(12)

def set_mode(hosts, topology, lib, mode, state, transport, i):
	if mode == 'single':
		test_single(hosts, topology, lib, state, transport, i)
	elif mode == 'sequence':
		test_sequence(hosts, topology, lib, state, transport, i)
	elif mode == 'burst':
		test_burst(hosts, topology, lib, state, transport, i)
	elif mode == 'parallel':
		test_parallel(hosts, topology, lib, state, transport, i)