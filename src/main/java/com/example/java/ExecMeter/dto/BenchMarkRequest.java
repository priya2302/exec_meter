package com.example.java.ExecMeter.dto;

public class BenchMarkRequest {
	private int iterations;
	private int concurrency;
	private String type; // "http" or "method"
	private String url; // only for "http"
	private String methodName; // optional
	
	


	public int getIterations() {
		return iterations;
	}


	public int getConcurrency() {
		return concurrency;
	}


	public void setConcurrency(int concurrency) {
		this.concurrency = concurrency;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public String getMethodName() {
		return methodName;
	}


	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}


	public void setIterations(int iterations) {
		this.iterations = iterations;
	}
	
	
	

}
