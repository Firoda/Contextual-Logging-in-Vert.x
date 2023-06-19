package com.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.slf4j.MDC;


public class Listener extends AbstractVerticle {
	final static Logger logger = LoggerFactory.getLogger(Listener.class);


	EventBus eb;
  @Override
  public void start(Future<Void> fut) {

  	  logger.info("Listener thread current MDC Value at start: {}", MDC.get(Constants.EVENTBUS_HEADER));
      eb = vertx.eventBus();
	    eb.consumer("ping", message -> {
			logger.info("Listener thread current MDC Value after a message is received: {}", MDC.get(Constants.EVENTBUS_HEADER));
			MultiMap headers = message.headers();
			if (headers.contains(Constants.EVENTBUS_HEADER)) {
				MDC.put(Constants.EVENTBUS_HEADER, headers.get(Constants.EVENTBUS_HEADER));
			}
	    	System.out.println("Received message from event bus: " + message.body());

			logger.info("task completed now, so clearing MDC");
			MDC.clear();
			logger.info("listener thread final call after listening to message on event bus");
	      message.reply("pong!+" + message.body());

	    });

	    System.out.println("Listener started to listen on eventbus address: ping");
	}
}
