import socket
import sys
import time

# Create a TCP/IP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Connect the socket to the port where the server is listening
#ip = raw_input('set ip: ')
#port = raw_input('set port: ')
#ip = 'raspberrypi.local'
ip = '192.168.0.105'
port = 10010
server_address = (ip, int(port))
print >>sys.stderr, 'connecting to %s port %s' % server_address
sock.connect(server_address)
while True:
	try:
		# Send data
		message = raw_input('command: ')
		print >>sys.stderr, 'sending "%s"' % message
		sock.sendall(message)
		data = sock.recv(16)
		print >>sys.stderr, 'received "%s"' % data
	finally:
		print ''