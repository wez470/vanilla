import socketserver
import subprocess
import struct
import time

class MyTCPHandler(socketserver.StreamRequestHandler):
    """
    The RequestHandler class for our server.

    It is instantiated once per connection to the server, and must
    override the handle() method to implement communication to the
    client.
    """

    def handle(self):
        startTime = time.time()
        sizeStruct = self.request.recv(4)
        size = struct.unpack("<I", sizeStruct)[0]

        print("Incoming message. size: " + str(size))
        recvd = b''
        while True:
            # self.request is the TCP socket connected to the client

            self.data = self.request.recv(8192)
            recvd += self.data
            if len(recvd) == size:
                break
            # Timeout after 60 seconds
            if time.time() - startTime > 60:
                print("Timeout")
                return
        print("Received all")
        
        filename = "server_out"
        newFile = open(filename + ".mp3", "wb")
        newFile.write(recvd)
        subprocess.call(["moodbar", filename + ".mp3", "-o", filename + ".mood"])
        
        with open(filename + ".mood", mode='rb') as file: # b is important -> binary
            moodFile = file.read()
        self.request.sendall(moodFile)
        print("Sent moodbar\n")

        #subprocess.call(["rm", "-f", filename + ".mp3", filename + ".mood"])


if __name__ == "__main__":
    # Replace with open port number
    HOST, PORT = "", 1234
    socketserver.TCPServer.allow_reuse_address = True
    server = socketserver.TCPServer((HOST, PORT), MyTCPHandler)

    # Activate the server; this will keep running until you
    # interrupt the program with Ctrl-C
    server.serve_forever()
