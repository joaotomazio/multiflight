import os
from time import sleep

from modes import flags

def set_duration(mode):
	need = 0
	if mode == "single":
		need = 10
	elif mode == "sequence":
		need = 20
	elif mode == "burst":
		need = 25
	elif mode == "parallel":
		need = 15
	else:
		need = 0
	return need + 5

def set_condition(hosts, topology, mode, state, transport):
	if state == "cold":
		return

	duration = set_duration(mode)
	server_flag, client_flag = flags(transport)
	if topology == "pair" or topology == "cycle":
		hosts["s2"].cmd('iperf -s ' + server_flag + '&')
		hosts["c2"].cmd('iperf -c 192.168.0.102 ' + client_flag + '-t ' + str(duration) + ' &')
		sleep(5)
	elif topology == "fatTree" or topology == "mesh":
		duration += 5
		hosts["s3"].cmd('iperf -s ' + server_flag + '&')
		hosts["s4"].cmd('iperf -s ' + server_flag + '&')
		hosts["c3"].cmd('iperf -f m -i 1 -c 192.168.0.103 ' + client_flag + '-t ' + str(duration) + ' &')
		sleep(5)
		duration -= 5
		hosts["c4"].cmd('iperf -f m -i 1 -c 192.168.0.104 ' + client_flag + '-t ' + str(duration) + ' &')
		sleep(5)



