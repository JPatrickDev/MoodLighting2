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
        self.presets = []
        self.loadPresets()

    def waitForDiscoveryRequests(self):
            myIp = socket.gethostbyname(socket.gethostname())
            s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
            s.bind(('', 7181))
            while True:
                data = s.recvfrom(1024)
                result = data[0].decode("utf8")
                if result.startswith("0xA91"):
                    ip = result.split(":")[1]
                    print("Discovered by" + str(ip))
                    soc = socket.socket()
                    soc.connect((ip, 7182))
                    soc.send(("0xA91:" + str(myIp)).encode("utf8"))

    def waitForConnections(self):
        serversocket = util.getServerSocket("0.0.0.0", 2705)
        while 1:
            print("Waiting for connection")
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
        
        d = threading.Thread(target=self.waitForDiscoveryRequests)
        d.daemon = False
        d.start()
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
            json.dump([x.__dict__ for x in self.groups], outfile)

    def saveClients(self):
        with open('clients.json', 'w') as outfile:
            json.dump(self.knownClients, outfile)

    def savePresets(self):
        with open('presets.json', 'w') as outfile:
            json.dump(self.presets, outfile)

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

    def loadPresets(self):
        if not os.path.exists('presets.json'):
            return
        with open('presets.json') as f:
            data = json.load(f)
        for preset in data:
            self.presets.append(preset)

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

    def getPreset(self,id):
        for p in self.presets:
            if p['id'] == id:
                return p
        return None

    def getDataFromPreset(self,id):
        p = self.getPreset(id)
        copy = p.copy()
        del copy['id']
        copy['startTime'] = time.time()
        return copy


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
   # stop_show()
    lights.startFade(data)
    return getJSONResponse()


# TODO: Allow a get request that assumes group all
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


@app.route("/lights/clients/groups")
def get_groups_by_id():
    cID = request.args.get('id')
    groups = []
    for g in lights.groups:
        for client in g.clients:
            if client == cID:
                groups.append(g.groupID)
    return json.dumps(groups)


@app.route("/lights/groups")
def list_groups():
    return json.dumps([x.__dict__ for x in lights.groups])


@app.route("/lights/groups/add", methods=['POST'])
def add_client_to_group():
    data = request.json
    id = data['clientID']
    groupID = data['groupID']
    lights.addToGroup(id, groupID)
    return getJSONResponse()


@app.route("/lights/groups/remove", methods=['POST'])
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


@app.route("/lights/presets")
def list_presets():
    return json.dumps(lights.presets)


@app.route("/lights/presets/create", methods={"POST"})
def add_preset():
    data = request.json
    data['id'] = getID([preset['id'] for preset in lights.presets])
    lights.presets.append(data)
    lights.savePresets()
    return json.dumps({"id" : data['id']})


@app.route("/lights/presets/delete", methods={"POST"})
def delete_preset():
    data = request.json
    id = data['id']
    p = lights.getPreset(id)
    if p is None:
        return json.dumps({"status" : "error"})
    else:
        lights.presets.remove(p)
        lights.savePresets()
        return getJSONResponse()


@app.route("/lights/start/preset", methods=['POST'])
def start_preset():
    data = request.json
    data = lights.getDataFromPreset(data['id'])
    if "group" not in data:
        data['group'] = "all"
    #stop_show()
    if data['type'] == "fade":
        print(data)
        lights.startFade(data)
        return getJSONResponse()
    elif data['type'] == "flash":
        print(data)
        lights.startFlash(data)
        return getJSONResponse()
    else:
        json.dumps({"status": "error"})


@app.route("/lights/presets/update", methods=['POST'])
def update_preset():
    data = request.json
    id = data['id']
    p = lights.getPreset(id)
    if p is None:
        return json.dumps({"status": "error"})
    else:
        del data['id']
        p.update(data)
        return getJSONResponse()

@app.route("/lights/status")
def status():
    lights.updateIPS()
    return "{\"clients\" : " + client_info() + ",\"show\" : " + info() + "}"

# TODO: Put something useful here.
def getJSONResponse():
    return json.dumps({"status": "ok"})


def getID(currentValues):
    id = ''.join(random.choices(string.ascii_uppercase + string.ascii_lowercase + string.digits, k=10))
    if id in currentValues:
        return getID(currentValues)
    else:
        return id


app.run('0.0.0.0', 2806, threaded=True)
