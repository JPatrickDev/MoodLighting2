import socket

def getServerSocket(addr,port):
    serversocket = socket.socket(
        socket.AF_INET, socket.SOCK_STREAM)
    serversocket.bind((addr, port))
    serversocket.listen(5)
    return serversocket

def waitForData(serversocket):
    (clientsocket, address) = serversocket.accept()
    print("Connected: " + str(address))
    input = clientsocket.recv(2048)
    return input.decode("utf8")

def sentToIPS(data,ips,port):
    validIPS = []
    for ip in ips:
        client = socket.socket()
        try:
            client.connect((ip[0], port))
            client.send(data.encode("utf8"))
            client = None
            validIPS.append(ip)
        except Exception as e:
            print(e)
            client = None
    return validIPS

