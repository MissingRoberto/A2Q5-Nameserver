/* 
 * Nameserver.java
 * - implement a name server process
 *
 * Distributed Systems Exercise
 * Assignment 2 Part II
 */

package de.uni_stuttgart.ipvs.ids.nameserver;

import java.util.HashMap;

public class Nameserver {

	private final static String IP = "localhost";

	private HashMap<String, Nameserver> children;
	private HashMap<String, String> savedNames;
	private int tcpPort;
	private int udpPort;
	private String domain;

	private TCPListener tcpListener;
	private UDPListener udpListener;

	public Nameserver(String domain, Nameserver parent, int tcpPort, int udpPort) { // DO
																					// NOT
																					// MODIFY!
		this.domain = domain;
		this.tcpPort = tcpPort;
		this.udpPort = udpPort;
		children = new HashMap<String, Nameserver>();
		savedNames = new HashMap<String, String>();

		tcpListener = new TCPListener(this, tcpPort);
		udpListener = new UDPListener(this, udpPort);
		tcpListener.start();
		udpListener.start();
	}

	public void addChild(String domain, Nameserver nameserver) { // DO NOT
																	// MODIFY!
		children.put(domain, nameserver);
	}

	public void addNameAddressPair(String name, String address) { // DO NOT
																	// MODIFY!
		savedNames.put(name, address);
	}

	public int getUdpPort() { // DO NOT MODIFY!
		return udpPort;
	}

	public int getTcpPort() { // DO NOT MODIFY!
		return tcpPort;
	}

	public String getAddress() { // DO NOT MODIFY!
		return IP;
	}

	public String getDomain() { // DO NOT MODIFY!
		return domain;
	}

	 
	// part a) recursive lookup
	public String lookupRecursive(String name) {
		// diagnostic output
		System.err.println("[INFO] lookupRecursive(\"" + name + "\")"
				+ " on Nameserver \"" + this.domain + "\"");

		String nextname = getNextLookup(name);

		if (savedNames.containsKey(name)) {
			// If we have the address
			return savedNames.get(name);
		} else if (savedNames.containsKey(nextname)) {
			// if the ask for the address without the name of this server
			return savedNames.get(nextname);
		} else if (name.equals(this.domain)) {
			// If it is the server
			return IP;
		} else {

			// Get the child
			String childName = getChildName(name);
			Nameserver child = children.get(childName);
			
			// Call to next child in the chain 
			
			String resp = child.lookupRecursive(nextname);

			// Store the response
			
			savedNames.put(nextname, resp);

			return resp;
		}

		// TODO: implement recursive lookup

	}

	// part b) iterative lookup
	public String lookupIterative(String name) {
		// diagnostic output
		System.err.println("[INFO] lookupIterative(\"" + name + "\")"
				+ " on Nameserver \"" + this.domain + "\"");

		String nextname = getNextLookup(name);

		if (savedNames.containsKey(name)) {
			// If we have the address
			return savedNames.get(name);
		} else if (savedNames.containsKey(nextname)) {
			// if the ask for the address without the name of this server
			return savedNames.get(nextname);
		} else if (name.equals(this.domain)) {
			// If it is the server
			return IP;
		}else {
			
			// Get the child
			String childName = getChildName(name);
			Nameserver child = children.get(childName);
			
			return child.getAddress() + ":" + child.getUdpPort();
		}

	}

	// TODO: add additional methods if necessary...

	// Extract the name of the child from the address
	
	private String getChildName(String name) {
		String[] domains = (name.trim()).split("\\.");

		int numDomains = domains.length;
		if (numDomains > 0) {
			String firstDomain = domains[numDomains - 1];

			if (firstDomain.equals(this.domain)) {

				if (numDomains - 2 >= 0) {
					return domains[numDomains - 2];

				} else {
					return null;
				}

			} else {
				return firstDomain;
			}

		} else {
			return null;
		}

	}

	// Extract the next request address
	
	private String getNextLookup(String name) {
		String response = name.trim();
		if (name.endsWith("." + this.domain)) {

			int lst = name.length() - 1 - domain.length();

			response = name.substring(0, lst);
		}
		return response;
	}

}
