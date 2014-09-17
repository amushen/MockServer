package com.shennan.mockserver;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import com.sun.net.httpserver.HttpServer;

public class MockServer {

	private final static Logger log=Logger.getLogger(MockServer.class);
	private String mockPath,port;
	
	public MockServer(String mockPath,String port){
		this.mockPath=mockPath;
		this.port=port;
	}
	
	public void start(){
		HttpServer server;
		try {
			server = HttpServer.create(new InetSocketAddress("127.0.0.1", Integer.parseInt(port)), 0);
			server.createContext("/", new FileHandler(mockPath));
			server.setExecutor(null);
			server.start();
		} catch (IOException e) {
			log.warn(e,e);
		}
	}
}
