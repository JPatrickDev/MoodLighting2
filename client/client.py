import json
import select
import socket
import threading

import requests

import util
from shows import FadeShow, ColorResult
import pigpio


class Client():
    rPin = 17
    gPin = 22
    bPin = 24

    pi = pigpio.pi()

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
            if i.startswith("FADE"):
                print("Starting Fade Show")
                r = requests.get(url='http://' + self.ip + ':2806/lights/info')
                r = (r.json())
                print(r)
                self.t = threading.Thread(target=self.startFade, args={json.dumps(r)})
                self.t.daemon = False
                self.t.start()
            if i.startswith("STOP"):
                print("Stopping")
                if self.show is not None:
                    self.show.stop()
            if i.startswith("COLOUR"):
                cD = i.split(" ")[1].split(",")
                r = ColorResult(int(cD[0]), int(cD[1]), int(cD[2]))
                self.updateColor(r)
            i = util.waitForData(serversocket)

    def startFade(self, r):
        r = json.loads(r)
        print(r)
        fade = FadeShow(self)
        self.show = fade
        c = []
        for col in r['data']['colours']:
            cD = col.split(",")
            c.append(ColorResult(int(cD[0]), int(cD[1]), int(cD[2])))
        fade.run(float(r['data']['startTime']), float(r['data']['pauseTime']), float(r['data']['fadeTime']), c)

    # Set the LEDs to the given colour result
    def updateColor(self, result):
        self.pi.set_PWM_dutycycle(self.rPin, result.r)
        self.pi.set_PWM_dutycycle(self.gPin, result.g)
        self.pi.set_PWM_dutycycle(self.bPin, result.b)


Client().connect("192.168.0.100")
