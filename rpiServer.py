import socket
import sys
import os
import serial
import time


ser = serial.Serial('/dev/tty.usbmodemfa131',9600)

def main():
	os.system('. /home/pi/mjpgStream.sh &')
	time.sleep(2)
	#TCP/IP sockets
	sock = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
	#Blind the address to port
	#ip=socket.gethostbyname(socket.gethostname())
	ip='raspberrypi.local' 
	server_address = (ip,10010)
	print >>sys.stderr, 'starting up on %s port %s' % server_address
	sock.bind(server_address)
	#listen for incoming conn
	sock.listen(1)
 
	while True:
		#Wait for it
		print >>sys.stderr, 'Waiting for connection'
		connection, client_address = sock.accept()
		try:
			print >>sys.stderr, 'connection from', client_address
 
			#Recieve data
			while True:
				data = connection.recv(16)
				print >>sys.stderr, 'recieved "%s"' % data
				
				if data:
					if data=='ADIOS':
						break
					m=motorVal(data)
					try:
						print >>sys.stderr, 'enviando'
						rdata=serialSend(str(unichr(110+m[0])))
						connection.sendall(rdata)
						rdata=serialSend(str(unichr(110+m[1])))
						connection.sendall(rdata)
					except KeyError:
						print >>sys.stderr, 'uknow command'	
						connection.sendall('null')	
				
 
				else:
					print >>sys.stderr, 'no more data from', client_address
					break
		finally:
			#clean
			connection.close()

	
def serialSend(comm):
	print >> sys.stderr, 'sending %s' % comm
	#The device should be checken in the specific
	ser.write(str(comm))
	#In case of receiving data
	ret = ser.read()
	print ret
	return ret

def motorVal(strV):
	arrV=strV.split(',')
	p=float(arrV[1])
	c=float(arrV[0])
	d=1-abs(c)/10.0
	#print 'power: %f' %p
	#print 'c: %f' %c
	#print 'd: %f' %d
	if(c<0.0):
		#print 'c es menor a 0'
		m1=p*d
		m2=p
	else:
		#print 'c es mayor a 0'
		m1=p
		m2=p*d
	m = [int(m1),int(m2)]
	return m
	
	
if __name__ == '__main__':
	main()
