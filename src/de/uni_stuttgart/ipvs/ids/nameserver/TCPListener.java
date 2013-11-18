/* 
 * TCPListener.java
 * - implement a TCP listener thread for Nameserver instances
 *
 * Distributed Systems Exercise
 * Assignment 2 Part II
 */

package de.uni_stuttgart.ipvs.ids.nameserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

// part a) listen for TCP connections for recursive lookup
public class TCPListener extends Thread {

	private int port;
	private Nameserver nameserver; // for issuing callbacks to owner

	public TCPListener(Nameserver nameserver, int port) { // DO NOT MODIFY!
		this.nameserver = nameserver;
		this.port = port;
	}

	public void run() {

		/*
		 * TODO: implement TCP listener here - pass requests to owner using
		 * nameserver.lookupRecursive(...)
		 */

		try {
			
			// Create a socket
			
			ServerSocket server = new ServerSocket(port);

			while (true) {

				// Accept connections
				
				Socket client = server.accept();

				System.out.println("connection accepted "
						+ nameserver.getDomain());

				BufferedReader in = new BufferedReader(new InputStreamReader(
						client.getInputStream()));
				OutputStreamWriter out = new OutputStreamWriter(
						client.getOutputStream());

				String line = in.readLine();

				System.out.println("request received " + line);


				// Call to the recursive resolution
				
				String name = nameserver.lookupRecursive(line);

				// Write the response
				
				out.write(name + "\r\n");

				// Close all the buffers and connections.
				out.close();
				in.close();
				client.close();

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
