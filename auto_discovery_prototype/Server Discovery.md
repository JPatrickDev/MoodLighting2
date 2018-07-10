# Server Discovery
Mood Lighting Server's support discovery without knowing their IP.

1. Controllers
- A controller can broadcast a UDP packet on port 7181 in the format utf8["0xA91:IP_ADDRESS"].
- Once this packet is broadcast, the Controller should then listen for TPC connections on port 7182
- Upon receiving a connection, the controller should read data from the socket. This data will be in the format utf8["0xA91:IP_ADDRESS"]
- The IP after the ":" is the IP of a Moodlighting server.
