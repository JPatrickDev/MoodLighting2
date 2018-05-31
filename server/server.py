# https://stackoverflow.com/questions/18080094/how-to-make-a-server-discoverable-to-lan-clients
import json
import random
import socket
import string
import threading
from queue import Queue

import time
import os.path
from flask import Flask, request

import util


class ConnectedClient:
    def __init__(self, id, name, address):
        self.id = id
        self.name = name
        self.address = address


class Group:
    def __init__(self, id, name):
        self.groupID = id
        self.name = name
        self.clients = []


class MoodLightingServer:
    def __init__(self):
        self.VERSION = "0.1a"
        self.clients = []
        self.queue = Queue()
        self.groups = []
        self.currentShow = {"type": "NONE"}
        self.loadGroups()

    def waitForConnections(self):
        serversocket = util.getServerSocket("0.0.0.0", 2705)
        while 1:
            (clientsocket, address) = serversocket.accept()
            print("Connected: " + str(address))
            data = clientsocket.recv(2048).decode("utf8")
            data = data.split(":")
            id = data[0]
            name = data[1]
            clientObj = ConnectedClient(id, name, address)
            print(str(id) + ":" + str(name))
            self.queue.put(clientObj)
            clientsocket.send(("CONNECTED:" + self.VERSION).encode("utf8"))

    def run(self):
        t = threading.Thread(target=self.waitForConnections)
        t.daemon = False
        t.start()
        return self

    def updateIPS(self):
        while not self.queue.empty():
            self.clients.append(self.queue.get())

    def startFade(self, data):
        self.updateIPS()
        if self.currentShow['type'] is not "NONE":
            return
        self.currentShow = {"type": "FADE", "data": data}
        self.removeDead(util.sentToIPS("FADE", [x.address for x in self.clients], 1202))

    def setColor(self, color):
        self.updateIPS()
        self.currentShow = {"type": "NONE"}
        self.removeDead(util.sentToIPS("COLOUR " + str(color), [x.address for x in self.clients], 1202))

    def stopShow(self):
        self.updateIPS()
        self.currentShow = {"type": "NONE"}
        self.removeDead(util.sentToIPS("STOP", [x.address for x in self.clients], 1202))

    def removeDead(self, addresses):
        for c in self.clients:
            if not c.address in addresses:
                print(str(c.id) + " disconnected, dropping.")
                self.clients.remove(c)

    def addToGroup(self, id, groupID):
        group = self.getGroup(groupID)
        if group is not None:
            print("Adding to group")
            group.clients.append(id)
            self.saveGroups()

    def getGroup(self, groupID):
        for g in self.groups:
            if g.groupID == groupID:
                return g
        return None

    def createGroup(self, name):
        id = ''.join(random.choices(string.ascii_uppercase + string.digits, k=6))
        # TODO Check unique
        self.groups.append(Group(id, name))
        print(self.groups)
        self.saveGroups()

    def saveGroups(self):
        with open('groups.json', 'w') as outfile:
            json.dump([x.__dict__ for x in lights.groups], outfile)

    def loadGroups(self):
        if not os.path.exists('groups.json'):
            return
        with open('groups.json') as f:
            data = json.load(f)
        for g in data:
            grObj = Group(g['groupID'], g['name'])
            for c in g['clients']:
                grObj.clients.append(c)
            self.groups.append(grObj)


lights = MoodLightingServer().run()
app = Flask(__name__)


@app.route("/lights/info")
def info():
    return json.dumps(lights.currentShow)


@app.route("/lights/start/FADE", methods=['POST'])
def start_fade():
    data = json.loads(request.data)
    data['startTime'] = time.time()
    lights.startFade(data)
    return str(time.time())


@app.route("/lights/stop")
def stop_fade():
    lights.stopShow()
    return str(time.time())


@app.route("/lights/setColor", methods=['POST'])
def set_colour():
    data = json.loads(request.data)
    c = data['color']
    lights.setColor(c)
    return str(time.time())


@app.route("/lights/clients")
def client_info():
    lights.updateIPS()
    return json.dumps([x.__dict__ for x in lights.clients])


@app.route("/lights/groups/addClient", methods=['POST'])
def add_client_to_group():
    print("Called")
    data = json.loads(request.data)
    id = data['clientID']
    groupID = data['groupID']
    lights.addToGroup(id, groupID)
    return str(time.time())


@app.route("/lights/groups/create", methods=['POST'])
def create_group():
    data = json.loads(request.data)
    name = data['groupName']
    lights.createGroup(name)
    return str(time.time())


@app.route("/lights/groups")
def list_groups():
    return json.dumps([x.__dict__ for x in lights.groups])


app.run('0.0.0.0', 2806, threaded=True)
