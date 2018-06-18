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
        self.currentShow = {}
        self.loadGroups()
        self.knownClients = []
        self.loadClients()

    def waitForConnections(self):
        serversocket = util.getServerSocket("0.0.0.0", 2705)
        while 1:
            (clientsocket, address) = serversocket.accept()
            print("Connected: " + str(address))
            data = clientsocket.recv(2048).decode("utf8")
            data = data.split(":")
            id = data[0]
            name = data[1]
            if id not in self.knownClients:
                self.knownClients.append(id)
                self.saveClients()
            print(self.knownClients)
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
        self.checkFlash()

    def checkFlash(self):
        toGo = []
        for group, showData in self.currentShow.items():
            if showData['type'] is "FLASH":
                dT = (time.time()) - showData['data']['startTime']
                if dT > float(showData['data']['duration']) and showData['data']['repeat'] == "False":
                    print("Stopping flash")
                    toGo.append(group)
                    self.removeDead(self.sendToGroup("STOP", group))
        for group in toGo:
            del self.currentShow[group]

    def startFade(self, data):
        self.updateIPS()
        if data['group'] in self.currentShow and self.currentShow[data['group']]['type'] is not "NONE":
            return
        self.currentShow[data['group']] = {"type": "FADE", "data": data}
        self.removeDead(self.sendToGroup("FADE", data['group']))

    def startFlash(self, data):
        self.updateIPS()
        if data['group'] in self.currentShow and self.currentShow[data['group']]['type'] is not "NONE":
            return
        self.currentShow[data['group']] = {"type": "FLASH", "data": data}
        self.removeDead(self.sendToGroup("FLASH", data['group']))

    def setColor(self, data):
        self.updateIPS()
        self.currentShow[data['group']] = {"type": "NONE", "color": str(data['color'])}
        self.removeDead(self.sendToGroup("COLOUR " + str(data['color']), data['group']))

    def stopShow(self, data):
        self.updateIPS()
        if "group" not in data:
            data['group'] = "all"

        if data['group'] == "all":
            self.currentShow = {}
        else:
            if data['group'] in self.currentShow:
                del self.currentShow[data['group']]
        self.removeDead(self.sendToGroup("STOP", data['group']))

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

    def saveClients(self):
        with open('clients.json', 'w') as outfile:
            json.dump(lights.knownClients, outfile)

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

    def loadClients(self):
        if not os.path.exists('clients.json'):
            return
        with open('clients.json') as f:
            data = json.load(f)
        for knownClient in data:
            self.knownClients.append(knownClient)

    def getIPByID(self, clientID):
        for c in self.clients:
            if c.id == clientID:
                return c.address
        return None

    def getIPSByGroup(self, group):
        g = self.getGroup(group)
        ips = []
        if g is not None:
            g = g.clients
            for client in g:
                ip = self.getIPByID(client)
                ips.append(ip)
        if group is "all":
            for c in self.clients:
                ips.append(c.address)
        print(ips)
        return ips

    def sendToGroup(self, message, group):
        ips = self.getIPSByGroup(group)
        alive = util.sentToIPS(message, ips, 1202)
        known = []
        for c in self.clients:
            if c.address in ips and c.address in alive:
                known.append(c.address)
            if c.address not in ips:
                known.append(c.address)
        return known

    def removeFromGroup(self, clientID, groupID):
        for g in self.groups:
            if g.groupID == groupID:
                if clientID in g.clients:
                    g.clients.remove(clientID)
        self.saveGroups()


lights = MoodLightingServer().run()
app = Flask(__name__)


@app.route("/lights/info")
def info():
    lights.updateIPS()
    return json.dumps(lights.currentShow)


@app.route("/lights/start/fade", methods=['POST'])
def start_fade():
    data = request.json
    data['startTime'] = time.time()
    if "group" not in data:
        data['group'] = "all"
    stop_show()
    lights.startFade(data)
    return getJSONResponse()


@app.route("/lights/stop", methods=['POST'])
def stop_show():
    data = request.json
    if data is None:
        data = {"group": "all"}
    lights.stopShow(data)
    return getJSONResponse()


@app.route("/lights/setColor", methods=['POST'])
def set_colour():
    data = request.json
    if "group" not in data:
        data['group'] = "all"
    lights.setColor(data)
    return getJSONResponse()


@app.route("/lights/clients")
def client_info():
    lights.updateIPS()
    return json.dumps({"connected": [x.__dict__ for x in lights.clients], "all": lights.knownClients})


@app.route("/lights/groups/addClient", methods=['POST'])
def add_client_to_group():
    data = request.json
    id = data['clientID']
    groupID = data['groupID']
    lights.addToGroup(id, groupID)
    return getJSONResponse()


@app.route("/lights/groups/removeClient", methods=['POST'])
def remove_client_from_group():
    data = request.json
    clientID = data['clientID']
    groupID = data['groupID']
    lights.removeFromGroup(clientID, groupID)
    return getJSONResponse()


@app.route("/lights/groups/create", methods=['POST'])
def create_group():
    data = request.json
    name = data['groupName']
    lights.createGroup(name)

    return getJSONResponse()


@app.route("/lights/start/flash", methods=['POST'])
def flash():
    data = request.json
    data['startTime'] = time.time()
    if "group" not in data:
        data['group'] = "all"
    lights.startFlash(data)
    return getJSONResponse()


@app.route("/lights/groups")
def list_groups():
    return json.dumps([x.__dict__ for x in lights.groups])


@app.route("/lights/getGroups")
def get_groups_by_id():
    cID = request.args.get('id')
    groups = []
    for g in lights.groups:
        for client in g.clients:
            if client == cID:
                groups.append(g.groupID)
    return json.dumps(groups)


# TODO: Put something useful here.
def getJSONResponse():
    return json.dumps({"status": "ok"})


app.run('0.0.0.0', 2806, threaded=True)
