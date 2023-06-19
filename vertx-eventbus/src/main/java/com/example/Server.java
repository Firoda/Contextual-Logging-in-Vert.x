package com.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.Handler;
import org.slf4j.MDC;

import java.util.HashMap;


public class Server extends AbstractVerticle {

    EventBus eb;
    final static Logger logger = LoggerFactory.getLogger(Server.class);
    private DeliveryOptions localDeliveryOptions;
    private Integer mdcIndex = 0;

  @Override
  public void start(Future<Void> fut) {
      localDeliveryOptions = new DeliveryOptions().setLocalOnly(true);
      vertx.deployVerticle(new Listener());
      registerInterceptor();
      eb = vertx.eventBus();

      Router router = Router.router(vertx);

      router.route("/ping").handler(this::ping);


    vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    System.out.println("Server verticle deployed!");
  }

    private void ping(RoutingContext routingContext) {
      String currentMdcValue = getUniqueValue();
      MDC.put(Constants.EVENTBUS_HEADER, currentMdcValue);
      logger.info("Placed MDC value in thread context: {}", currentMdcValue);

//      MultiMap multiMap = (MultiMap) new HashMap<Object, Object>();
//      multiMap.add(Constants.EVENTBUS_HEADER, MDC.get(Constants.EVENTBUS_HEADER));
      String msg = routingContext.request().getParam("msg");

      logger.info("Sending this msg to eventBus : " + msg);
      eb.send("ping",msg,localDeliveryOptions,reply -> {
        if (reply.succeeded()) {
          String out = reply.result().body().toString();
          System.out.println("Received reply in server: " + out);
          routingContext
          .response()
          .putHeader("content-type", "application/json; charset=utf-8")
          .end(out);

        } else {
          System.out.println("No reply in server");

          routingContext
          .response()
          .putHeader("content-type", "application/json; charset=utf-8")
          .end("Failed");
        }

    });
   

  }

    private String getUniqueValue() {
      mdcIndex++;
      return Integer.toString(mdcIndex);
    }


    private void registerInterceptor() {
        vertx.eventBus().addInboundInterceptor(deliveryContext -> {
            try {
                logger.info("Inside inbound interceptor");
                MultiMap headers = deliveryContext.message().headers();
                if (headers.contains(Constants.EVENTBUS_HEADER)) {
                    MDC.put(Constants.EVENTBUS_HEADER, headers.get(Constants.EVENTBUS_HEADER));
                    logger.info("Inbound: Set header value in eventBus ");
                }
            }catch(Exception e){
                logger.error("addInboundInterceptor error occurred {}",e.fillInStackTrace());
                e.printStackTrace();
            }
            deliveryContext.next();
        }).addOutboundInterceptor(deliveryContext -> {
            try{
                logger.info("Inside outbound interceptor");
                deliveryContext.message().headers().add(Constants.EVENTBUS_HEADER, MDC.get(Constants.EVENTBUS_HEADER));
                logger.info("Outbound: Set header value in eventBus ");
            }catch(Exception e){
                logger.error("addOutboundInterceptor error occurred {}",e.fillInStackTrace());
                e.printStackTrace();
            }
            deliveryContext.next();
        });
    }

}
