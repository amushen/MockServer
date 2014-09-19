package com.shennan.mockserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.crypto.URIDereferencer;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
/**
 * @author shennan
 */
public class FileHandler implements HttpHandler {

	private final static Logger log=Logger.getLogger(FileHandler.class);
	private String mockPath;
	
	private static Map<String,Boolean> byteFileType;
	
	static{
		byteFileType=new HashMap<String,Boolean>();
		byteFileType.put("jpg", true);
		byteFileType.put("jpeg", true);
		byteFileType.put("gif", true);
		byteFileType.put("png", true);
		byteFileType.put("xls", true);
		byteFileType.put("xlsx", true);
		byteFileType.put("zip", true);
		byteFileType.put("pdf", true);
	}
	
	public FileHandler(String mockPath){
		this.mockPath=mockPath;
		
	}
	
	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		String uri=httpExchange.getRequestURI().toString();
		uri=URLDecoder.decode(uri, "UTF-8");
		log.debug("uri:"+uri);
		Map<String,String> getPara=null;
		if(uri.indexOf("?")>-1)getPara=getParameter(uri);
		
		InputStream in = httpExchange.getRequestBody();
		Map<String,String> postPara=getParameter(in);
		final Map<String,String> reqMap=mergeMap(getPara,postPara);
		
		String fullname=uri;
		fullname=uri.split("\\?")[0];
		fullname=fullname.replaceAll("/", "\\\\");
		File file=new File(getFilePath(mockPath+fullname));
		int ind=fullname.lastIndexOf("\\")+1;
		final String filename=fullname.substring(ind);
		log.debug("fullname,filename="+fullname+","+filename);
		File[] files=file.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				if(filename==null||filename.length()<1)return false;
				if(name.indexOf(filename)<0)return false;
				String para=null;
				Map<String,String> params=new HashMap<String,String>();
				if(name.indexOf("_")>-1){
					para=name.split("\\_")[1];
					params=getParameter(para);
				}
				if(judgeMapEqual(params, reqMap)){
					log.debug("match:"+name);
					return true;
				}else 
					return false;
			}
		});
		
		if(files==null || files.length==0){
			httpExchange.sendResponseHeaders(404, 0);
			httpExchange.close();
			return;
		}
		
		if(files.length>0){
			File certainfile=files[0];
			if(certainfile.isDirectory()){
				httpExchange.sendResponseHeaders(404, 0);
				httpExchange.close();
				return; 
			}
			httpExchange.sendResponseHeaders(200, getFileSize(certainfile));
			//set content-type
			String fn=file.getName();
			String fileExt=fn.substring(fn.lastIndexOf(".")+1);
			fileExt=fileExt.toLowerCase();
			if(byteFileType.containsKey(fn) || fullname.indexOf("type=byte")>-1){
				httpExchange.getResponseHeaders().set("Content-Type", "application/octet-stream");
			}else{
				httpExchange.getResponseHeaders().set("Content-Type", "text/plain");
			}
			response(httpExchange,certainfile);
		}
        
        httpExchange.close();  
	}
	
	
	/**
	 * get parameters from uri
	 * @param uri
	 * @return
	 */
	private Map<String,String> getParameter(String uri){
		Map<String,String> ret=new HashMap<String,String>();
		if(uri==null)return ret;
		String para=uri;
		if(para.indexOf("?")>-1){
			para=uri.split("\\?")[1];
		}
		if(para==null || para.length()<1)return ret;
		String[] token=para.split("&");
		for(String one:token){
			if(one==null||one.length()<1)continue;
			String two[]=one.split("=");
			if(two.length==1){
				ret.put(two[0], "");
			}else if(two.length==2){
				ret.put(two[0], two[1]);
			}
		}
		return ret;
	}

	/**
	 * get parameter from body
	 * @param in
	 * @return
	 */
	private Map<String,String> getParameter(InputStream in){
		byte buf[]=new byte[255];
		ByteOutputStream bos=new ByteOutputStream();
		int len;
		try{
			while((len=in.read(buf,0,255))>-1){
				bos.write(buf,0,len);
			}
		}catch(IOException e){
			log.warn(e,e);
		}
		if(bos.size()>0){
			try {
				return getParameter(new String(bos.getBytes(),"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				log.warn(e,e);
			}
		}
		return new HashMap<String,String>();
	}
	
	/**
	 * write file content to output stream
	 * @param out
	 * @param file
	 */
	private void response(HttpExchange exchange,File file){
		try{
			FileInputStream fis=new FileInputStream(file);
			byte buf[]=new byte[255];
			int len=-1;
			while((len=fis.read(buf, 0, 255))>-1){
				exchange.getResponseBody().write(buf, 0, len);
			}
			fis.close();
			exchange.getResponseBody().flush();
			
		}catch(Exception e){
			log.warn(e,e);
		}
	}
	
	private String getFilePath(String path){
		return path.substring(0,path.lastIndexOf("\\"));
	}
	
	private boolean judgeMapEqual(Map<String,String> map1,Map<String,String> map2){
		if(map1==null && map2==null)return true;
		if(map1==null || map2==null)return false;
		if(map1.keySet().size()!=map2.keySet().size())return false;
		Iterator<String> keys=map1.keySet().iterator();
		while(keys.hasNext()){
			String key=keys.next();
			if(!map1.get(key).equals(map2.get(key)))return false;
		}
		
		return true;
	}
	
	
	private int getFileSize(File file){
		int size=0;
		try{
			FileInputStream fis=new FileInputStream(file);
			size=fis.available();
			fis.close();
		}catch(Exception e){
			log.warn(e,e);
		}
		return size;
	}
	
	private Map<String,String> mergeMap(Map<String,String> map1,Map<String,String> map2){
		HashMap<String,String> ret=new HashMap<String,String>();
		Iterator<String> i;
		if(map1!=null && map1.size()>0){
			i=map1.keySet().iterator();
			while(i.hasNext()){
				String key=i.next();
				ret.put(key, map1.get(key));
			}
		}
		if(map2!=null && map2.size()>0){
			i=map2.keySet().iterator();
			while(i.hasNext()){
				String key=i.next();
				ret.put(key, map2.get(key));
			}
		}
		return ret;
	}
}
