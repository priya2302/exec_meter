package com.example.java.ExecMeter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BenchMarkResult {
	private double totalTimeMs;
	private double avgTimeMs;
	private double successRate;
	private long failedCount;
	private String status;
}
