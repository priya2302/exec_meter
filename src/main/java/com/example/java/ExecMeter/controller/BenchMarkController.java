package com.example.java.ExecMeter.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.java.ExecMeter.dto.BenchMarkRequest;
import com.example.java.ExecMeter.dto.BenchMarkResult;
import com.example.java.ExecMeter.service.BenchMarkService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/execMeter")
@Slf4j
public class BenchMarkController {

	private final BenchMarkService service;
	ObjectMapper objectMapper = new ObjectMapper();

	public BenchMarkController(BenchMarkService benchmarkService) {
		this.service = benchmarkService;
	}

	@PostMapping("api")
	public Mono<BenchMarkResult> runMeter(@RequestBody BenchMarkRequest req) {
		try {
			log.info("Request received to run Meter:: " + objectMapper.writeValueAsString(req));
			return service.runMeter(req);
		} catch (Exception e) {

		}
	  return null;
	}

}
