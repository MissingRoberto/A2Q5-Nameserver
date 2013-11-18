/* 
 * Client.java
 * - perform name resolution query
 *
 * Distributed Systems Exercise
 * Assignment 2 Part II
 */

package de.uni_stuttgart.ipvs.ids.nameserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

	// part a) start recursive lookup
	public String recursiveNameResolution(String name, String serverAddress,
			int serverPort) {

		// TODO: complete method (using TCP sockets)
		String response = "not response";

		try {
			
			// Open the socket
			
			InetAddress ip_host = InetAddress.getByName(serverAddress);

			Socket server = new Socket(ip_host.getHostAddress(), serverPort);

			// Open buffers

			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					server.getOutputStream()));

			BufferedReader in = new BufferedReader(new InputStreamReader(
					server.getInputStream()));

			System.out.println("request sent " + name);

			// Write the request
			
			out.write(name + "\r\n");
			out.flush();

			// Read the address
			
			response = in.readLine();

			out.close();
			in.close();
			server.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;
	}

	// part b) start iterative lookup
	public String iterativeNameResolution(String name, String serverAddress,
			int serverPort) {

		try {
			
			// Open the socket
			
			DatagramSocket client = new DatagramSocket(10000,
					InetAddress.getByName("localhost"));

			// TODO: complete method (using UDP sockets)

			String s_ip = serverAddress;
			int s_port = serverPort;
			
			// Make all the request
			
			for (String nextname : getListNames(name)) {

				
				// Send the request
				
				byte[] out = nextname.getBytes();

				DatagramPacket packet = new DatagramPacket(out, out.length,
						InetAddress.getByName(s_ip), s_port);

				client.send(packet);

				// Receive the response
				
				DatagramPacket in_packet = new DatagramPacket(new byte[100],
						100);
				client.receive(in_packet);

				String ds = new String(in_packet.getData(), 0,
						in_packet.getLength());
				System.out.println("Received server info:" + ds);
				if (ds.contains(":")) {
					String[] d = ds.split(":");

					s_ip = d[0];
					s_port = Integer.parseInt(d[1]);

				} else {
					s_ip = ds.trim();
					s_port = 0;

				}
				if (s_port == 0) {
					client.close();
					return s_ip;
				}

			}
			client.close();
			return s_ip;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private String[] getSubDomains(String name) {
		return name.split("\\.");
	}
	
	
	// Method to extract all the subdomains.

	private String[] getListNames(String name) {

		String[] subdomains = getSubDomains(name);

		int numDomains = subdomains.length;

		String[] list = new String[numDomains];

		for (int i = numDomains - 1; i >= 0; i--) {

			for (int j = 0; j < (numDomains - i); j++) {
				if (list[i] == null)
					list[i] = subdomains[j];
				else
					list[i] = list[i] + "." + subdomains[j];

			}

		}
		return list;
	}
}
