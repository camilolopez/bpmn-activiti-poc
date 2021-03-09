package com.oc.pc.bpmn.integrationtests;

import java.util.stream.IntStream;

import org.activiti.engine.RuntimeService;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.oc.pc.bpmn.BpmnApplication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {BpmnApplication.class})
public class IntegrationTests {
	@Autowired private RuntimeService runtimeService;
	
	@Test
	public void loadTesting() {
		StopWatch watch = new StopWatch();
		watch.start();
		IntStream.range(0, 50000)
				.forEach(i -> {
					runtimeService.startProcessInstanceByKey("pqr_nacional");
				});
		watch.stop();
		log.info("Time Elapsed creating 50.000 instances: " + getTime(watch));
		watch.reset();
		
		IntStream.range(0, 50000)
			.filter(i -> i % 1000 == 0)
			.filter(i -> i != 0) 
			.forEach(i -> {
				StopWatch watch2 = new StopWatch();
				watch2.start();
				runtimeService.createProcessInstanceQuery()
						.listPage(i - 1000, i);
				watch2.stop();
				log.info("Time Elapsed querying by 1.000 instances: " + getTime(watch2));
		
			});
		
		watch.start();
		runtimeService.createProcessInstanceQuery().listPage(0, 50000);
		watch.stop();
		log.info("Time Elapsed querying 50.000 instances: " + getTime(watch));
	}

	private String getTime(StopWatch watch) {
		return DurationFormatUtils.formatDuration(watch.getTime(), "mm:ss.SSS");
	}
}
