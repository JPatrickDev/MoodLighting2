import json
import select
import socket
import threading

import requests

import util
from shows import FadeShow, ColorResult


class Client():
    def connect(self, ip, port=2705):
        self.ip = ip
        self.socket = socket.socket()
        # now connect to the web server on port 80
        # - the normal http port
        self.socket.connect((ip, 2705))
        waiting = True
        while waiting:
            read, write, error = select.select([self.socket], [], [], 0)
            for s in read:
                if s is self.socket:
                    data = s.recv(4096)
                    if not data:
                        print("Disconnected From MoodLightingServer")
                        waiting = False;
                    else:
                        out = data.decode("utf8")
                        print(out)
                        waiting = False;
                        self.run()


    def run(self):
        print("Connection Established, running")
        serversocket = util.getServerSocket("0.0.0.0", 1202)
        i = ""
        r = requests.get(url='http://' + self.ip + ':2806/lights/info')
        r = (r.json())
        i = r['type']
        while 1:
            print(i)
            if i == "FADE":
                print("Starting Fade Show")
                r = requests.get(url='http://' + self.ip + ':2806/lights/info')
                r = (r.json())
                print(r)
                self.t = threading.Thread(target=self.startFade, args={json.dumps(r)})
                self.t.daemon = False
                self.t.start()
            if i == "STOP":
                print("Stopping")
                if self.show is not None:
                    self.show.stop()

            i = util.waitForData(serversocket)

    def startFade(self, r):
        r = json.loads(r)
        print(r)
        fade = FadeShow()
        self.show = fade
        c = []
        for col in r['data']['colours']:
            cD = col.split(",")
            c.append(ColorResult(int(cD[0]),int(cD[1]),int(cD[2])))
        fade.run(float(r['data']['startTime']), float(r['data']['pauseTime']), float(r['data']['fadeTime']),c)


Client().connect("192.168.0.100")
