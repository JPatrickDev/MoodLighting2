# https://stackoverflow.com/questions/18080094/how-to-make-a-server-discoverable-to-lan-clients
import json
import socket
import threading
from queue import Queue

import time

from flask import Flask, request

import util


class MoodLightingServer:


    def __init__(self):
        self.VERSION = "0.1a"
        self.clients = []
        self.queue = Queue()
        self.currentShow = {"type" : "NONE"}

    def waitForConnections(self):
        serversocket = util.getServerSocket("0.0.0.0", 2705)
        while 1:
            (clientsocket, address) = serversocket.accept()
            print("Connected: " + str(address))
            self.queue.put(address)
            clientsocket.send(("CONNECTED:" + self.VERSION).encode("utf8"))

    def run(self):
        t = threading.Thread(target=self.waitForConnections)
        t.daemon = False
        t.start()
        return self

    def updateIPS(self):
        while not self.queue.empty():
            self.clients.append(self.queue.get())
    def startFade(self,data):
        self.updateIPS()
        if self.currentShow['type'] is not "NONE":
            return
        self.currentShow = {"type": "FADE", "data": data}
        print(self.currentShow)
        print("Pinging clients")
        self.clients = util.sentToIPS("FADE", self.clients, 1202)

    def setColor(self,color):
        self.updateIPS()
        self.currentShow = {"type" : "NONE"}
        self.clients = util.sentToIPS("COLOUR " + str(color), self.clients, 1202)
        print("Colour set")

    def stopShow(self):
        self.updateIPS()
        self.currentShow = {"type": "NONE"}
        self.clients = util.sentToIPS("STOP", self.clients, 1202)


lights = MoodLightingServer().run()
app = Flask(__name__)

@app.route("/lights/info")
def info():
    return json.dumps(lights.currentShow)

@app.route("/lights/start/FADE",methods = ['GET', 'POST', 'DELETE'])
def start_fade():
    data = json.loads(request.data)
    data['startTime'] = time.time()
    print("Test:" + str(data))
    lights.startFade(data)
    return str(time.time())

@app.route("/lights/stop")
def stop_fade():
    lights.stopShow()
    return str(time.time())

@app.route("/lights/setColor")
def set_colour():
    c = request.args.get('c');
    lights.setColor(c)
    return str(time.time())

app.run('0.0.0.0',2806)
