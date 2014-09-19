package com.shennan.mockserver;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * @author shennan
 */
public class Main {
	
	private static final Logger log=Logger.getLogger(Main.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Using -? or -h to see help.");
		String mocksPath=System.getProperty("user.dir")+File.separator+"mocks";
		String port="8080";
		if(args.length>0){
			if("-h".equals(args[0]) || "/?".equals(args[0])){
				printHelp();
				System.exit(0);
			}
			mocksPath=args[0];
		}
		if(args.length>1){
			port=args[1];
		}
		log.debug("mocks path,port:"+mocksPath+","+port);
		MockServer ms=new MockServer(mocksPath, port);
		ms.start();
		log.debug("MockServer started.");
	}
	
	private static void printHelp(){
		System.out.println("java -jar MockServer.jar [mock path] [port]");
		System.out.println("e.g.");
		System.out.println("java -jar MockServer.jar d:\\mocks 808");
		System.out.println("copyright amushen@qq.com 2014, all rights reserved.");
	}

}
