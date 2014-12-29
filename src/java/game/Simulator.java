package game;

import game.base.SimObject;

import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;

public class Simulator implements Runnable {
  
  private static Logger logger = Logger.getLogger(Simulator.class);
  
  Context context;
  
  Thread thread;
  volatile int    tickNumber = 0;
  volatile long   tickOrigin = System.currentTimeMillis();

  long   symInterval = 10;
  List<SimObject> simObjects = Lists.newArrayList();

  public Simulator(Context context) {
    this.context = context;
    thread = new Thread(this);
    thread.setDaemon(true);
  }
  
  public void start() {
    thread.start();
  }

  @Override
  public void run() {
    try {
      long lastSymCompleted = System.currentTimeMillis();
      while( ! context.getInputDevice().getQuit() ) {
        tickNumber += 1;
        tickOrigin = System.currentTimeMillis();
        synchronized(simObjects) {
          for(SimObject o: simObjects) {
            o.tick();
          }
        }
        long afterTime = System.currentTimeMillis();
        long sleepTime = lastSymCompleted + symInterval - afterTime; 
        lastSymCompleted = afterTime;
        if ( sleepTime > 0 ) {
          context.getLogPanel().setSleepTime(sleepTime);
          try { Thread.sleep(sleepTime); } catch(Exception e) { throw new RuntimeException(e); };
        }
        else {
          context.getLogPanel().setSleepTime(0);
        }
      }
    }
    catch(Exception e) {
      logger.error("Simulator exception", e);
    }
  }
  
  public int getCurrentTick() {
    return tickNumber;
  }
  
  public double getCurrentTickNibble() {
    return ((System.currentTimeMillis() - tickOrigin) / ( double ) symInterval);    
  }

  public void register(SimObject o) {
    synchronized(simObjects) {
      simObjects.add(o);
    }
  }
  
  public List<SimObject> getSimObjects() {
    return simObjects;
  }

  public void waitForStart() {
    try { Thread.sleep(1000); } catch(InterruptedException e) { };
    while(tickNumber == 0) {
      try { Thread.sleep(100); } catch(InterruptedException e) { };
    }
  }

}
