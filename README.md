# MoodLighting2
A new version of my MoodLighting program. Has changed to a Server-Client architecture, to allow for multiple LED strips to be controlled at the same time. 

## Technical Details:
### Server:
Server runs on port 2705 and waits for any connection attempts. Once it gets a connection, it adds the IP the connection came from to memory and then continues waiting.
At the same time, in another thread a Flask server runs, waiting for incoming commands and requests. 
When a request is received, it connects to all the Clients that have connected to it, sends the command and then closes the connection. 

### Client:
Client starts, connects to the Server at the given IP. Client than starts a ServerSocket, on port 1202, and waits for incoming connections. 
When the client receives a connection, it immediately reads from the new client. This data is then converted in to UTF8 and then processed as a command. 
These commands can turn the lights off, set the colour of the lights or start a fade show. 

### Fade:
An article on exactly how the Fade show allows for(in theory) unlimited LED strips to be controlled in sync wirelessly is coming soon.

You're obviously welcome to look at sourcecode yourself for now though, as it's pretty simple really. Just requires the server and all the clients to agree on the time as accurately as possible. 
It seems that just using the same NTP server for all devices is enough, but this needs further investigation.

## Controllers and other info:

More information, including on how to make controllers for the server is in progress. I hope to make a sample Android application and possibly a desktop client. The server - controller
interface is entirely based on HTTP so it is extremely cross-platform, the most basic type of Controller would be a browser that can bookmark HTTP requests(Including POST).

The long term goal of this project is a fully functioning LED controller system, with a very easily interface for controllers, and seamless support for a large number
of wirelessly connected LED strips. Also planned is a fully functioning sample Android application. 