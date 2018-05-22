package common.io

import scala.util.{Try, Success, Failure}
import java.io.InputStreamReader
import scala.collection.mutable.ArrayBuffer

object Log {

  val forceError   = true
  val doTasks      = true
  val printWarning = true
  val bug          = true
  val shouldLog    = false
  var prevTime  : Long = 0
  var totalTime : Double = 0.0
  //val 
  
  var debugLog = scala.collection.mutable.Map[Object,ArrayBuffer[String]]()
  
  var logBuffer = ArrayBuffer[String]()

 
  //--------------------------------------------------------------- 
  def log( msg : String ) = {
    if( shouldLog )
      logBuffer += msg
  }
  
  
  //---------------------------------------------------------------
  def clearLog = {
    logBuffer.clear()
  }
  
  
  //---------------------------------------------------------------
  def fail[T]( o : Object, msg : String ) : Failure[T] =
    fail( o, msg, forceError )
  
  
    
    
  //---------------------------------------------------------------  
  def fail[T]( o : Object, msg : String, force : Boolean ) : Failure[T] = {
    if( force ){
      println( o.getClass + ": " + msg )
      var s = null
      s.toString
    }
    
    Failure( new Exception( o.getClass + ": " + msg ) )
  }
  

  //---------------------------------------------------------------
  def warning( o : Object, msg : String ) =
    if( printWarning )
      println( "[WARNING] " + o.getClass + ":" + msg )
      

  //---------------------------------------------------------------
  def debug( o : Object, msg : String ) {
    if( bug )
  	  println( "[DEBUG] " + o.getClass + ":" + msg )
  }
      
  
  //---------------------------------------------------------------
  def debug( o : Object, msg : String, log : Boolean ) {
    if( bug ){
      println( "[DEBUG] " + o + ":" + msg )
      if( log ){
        if( !debugLog.contains( o ) )
          debugLog += ((o, new ArrayBuffer[String]()))
        debugLog( o ) +=  "[DEBUG] " + o + ":" + msg
      }
    }       
  }
  
  //--------------------------------------------------------------- 
  def startTask( msg : String ) = {
    if( doTasks ){      
      prevTime = System.currentTimeMillis();
      println( "[TASK] " + msg );
      /*if( shouldLog ){
        log( "[TASK] " + str );
        if( shouldStoreLogOften )
          storeLog();
      }*/
    }
  }
  
  //-------------------------------------------------------------------------------------------
  def endTask( msg : String ){
    if( doTasks ){   
      val elapsedTimeSec = (System.currentTimeMillis() - prevTime) / 1000.0;
      totalTime = totalTime + elapsedTimeSec;
      val elapsedTimeMin = elapsedTimeSec / 60.0;
      val totalTimeMin   = totalTime / 60.0;
      
      var taskTime = "";
      if( elapsedTimeMin < 1.0 )
        taskTime = elapsedTimeSec + " secs"; 
      else
        taskTime = elapsedTimeMin + " mins";
  
      var tTaskTime = "";
      if( totalTimeMin < 1.0 )
        tTaskTime = totalTime + " secs";
      else
        tTaskTime = totalTimeMin + " mins";
      
      println( "[TASK] Task finished in " + taskTime + ". Total time: " + tTaskTime + ". " + msg );
      /*if( shouldLog ){
        log( "[TASK] Task finished in " + taskTime + ". Total time: " + tTaskTime + "." );
        if( shouldStoreLogOften )
          storeLog();
      }*/
    }
  }
  
   //-------------------------------------------------------------------------------------------
  def forceStop {
    var reader = new InputStreamReader( System.in );

    var input = "";
    try {
      reader.read()
      //input = reader.read()
      //input = reader.readLine();
    }
    catch {
      case e : Exception => e.printStackTrace()
    }
  }
  
  
}