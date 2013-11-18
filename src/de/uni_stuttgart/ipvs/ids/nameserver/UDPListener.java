/* 
 * UDPListener.java
 * - implement a UPD listener thread for Nameserver instances
 *
 * Distributed Systems Exercise
 * Assignment 2 Part II
 */

package de.uni_stuttgart.ipvs.ids.nameserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

// part b) listen for UDP datagrams for iterative lookup
public class UDPListener extends Thread {

	private int port;
	private Nameserver nameserver; // for issuing callbacks to owner

	public UDPListener(Nameserver nameserver, int port) { // DO NOT MODIFY!
		this.nameserver = nameserver;
		this.port = port;
	}

	public void run() {

		/*
		 * TODO: implement UDP listener here - pass requests to owner using
		 * nameserver.lookupIterative(...)
		 */
		try {

			// Create a socket 
			
			DatagramSocket client = new DatagramSocket(port,
					InetAddress.getByName(nameserver.getAddress()));

			DatagramPacket in_packet = new DatagramPacket(new byte[1024], 100);
			
			// receive the request
			
			client.receive(in_packet);
			String name = new String(in_packet.getData());

			// Call to lookupIterative
			
			String address = nameserver.lookupIterative(name);
			byte[] out = address.getBytes();

			DatagramPacket packet = new DatagramPacket(out, out.length,
					in_packet.getAddress(), in_packet.getPort());

			client.send(packet);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
