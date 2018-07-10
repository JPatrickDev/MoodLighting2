import random
import string
from socket import *


def getID(currentValues):
    id = ''.join(random.choices(string.ascii_uppercase + string.ascii_lowercase + string.digits, k=10))
    if id in currentValues:
        return getID(currentValues)
    else:
        return id

myIp = gethostbyname(gethostname())
print(getID([]))

s=socket(AF_INET, SOCK_DGRAM)
s.bind(('',7181))
while 1:
    m=s.recvfrom(1024)
    result = m[0].decode("utf8")
    print(result)
    if result.startswith("0xA91"):
        ip = result.split(":")[1]
        print("Discovered by: " + str(ip))
        soc = socket()
        soc.connect((ip,7182))
        soc.send(("0xA91:" + str(myIp)).encode("utf8"))