from socket import *

import time


def getServerSocket(addr,port):
    serversocket = socket(
       AF_INET, SOCK_STREAM)
    serversocket.bind((addr, port))
    serversocket.listen(5)
    return serversocket

def waitForData(serversocket):
    serversocket.settimeout(5)
    (clientsocket, address) = serversocket.accept()
    print("Connected: " + str(address))
    input = clientsocket.recv(2048)
    return input.decode("utf8")


myIp = gethostbyname(gethostname())

cs = socket(2, 2)
cs.setsockopt(65535, 4, 1)
cs.setsockopt(65535, 32, 1)
cs.sendto(('0xA91:' + str(myIp)).encode("utf8"), ('255.255.255.255', 54545))
socket = getServerSocket("0.0.0.0",2705)

discovered = []
while 1:
    try:
        input = waitForData(socket)
    except Exception:
        break
    print(input)
    if input.startswith("0xA91"):
        discovered.append(input.split(":")[1])
print("Done")
print(discovered)
