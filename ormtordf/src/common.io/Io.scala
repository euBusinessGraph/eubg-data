package common.io

import scala.util.{Failure, Success, Try}
import java.io._
import java.nio.charset.CodingErrorAction
import scala.io.Codec
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.collection.mutable.ArrayBuffer

object Io {

  //---------------------------------------------------------------------
  def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) :
   Try[Boolean] = {
    //TO DO: Update this to make it fast/understandable
    val p = new java.io.PrintWriter(f)
    try { 
      op(p)
    } catch {
      case e : Exception => return Failure( e )
    } 
    finally {
      p.close()      
    }
    Success( true )
  }
  
  
  //---------------------------------------------------------------------
  def writeToFile( filePath : String, contents : String ) :
    Try[Boolean] = {
    printToFile( new File( filePath) ) { p => p.write( contents ) }
    //File( filePath ).writeAll( contents )
  }
  
 
  //---------------------------------------------------------------------
  def writeArrayBufferToFile( filePath : String, contents : ArrayBuffer[String] ) :  
    Try[Boolean] = {
    printToFile( new File( filePath) ) { p => 
      for( l <- contents )
        p.write( l )
    }    
    //val writer = new PrintWriter( new File( filePath ) )
    //for( l <- contents )
    //  writer.write( l )
    //writer.close
    
    //printToFile( new File( filePath) ) { p => p.write( contents ) }
    //File( filePath ).writeAll( contents )
  }
  
  
  //---------------------------------------------------------------------
  def readFromFile( filePath : String ) : Try[String] = {
    try{
      val source = scala.io.Source.fromFile( filePath )
      val lines = source.mkString
      source.close()
      Success( lines )
    } catch {
      case e : Exception => Failure( e )
    }     
  }
  
 
  //---------------------------------------------------------------------
  def readFromFileL( filePath : String ) : Try[List[String]] = {
    //implicit val codec = Codec("UTF-8")
    //codec.onMalformedInput(CodingErrorAction.REPLACE)
    //codec.onUnmappableCharacter(CodingErrorAction.REPLACE)
    //println( System.getProperty("file.encoding") )
    
    try{
      val source = scala.io.Source.fromFile( filePath, "UTF-8" )
      //println( "CODEC: " + source.codec )
     
      var lst = source.getLines().toList
      //for( l <- lst )
      //  println( "LINE: " + l)
      
      source.close()
      Success( lst )     
    } catch {
      case e : Exception => Failure( e )
    }     
  }
  
 //---------------------------------------------------------------------
  def readFromFileEnc( filePath : String, encoding: String ) : Try[String] = {
    //implicit val codec = Codec("UTF-8")
    //codec.onMalformedInput(CodingErrorAction.REPLACE)
    //codec.onUnmappableCharacter(CodingErrorAction.REPLACE)
    try{
      val bytes = Files.readAllBytes( Paths.get( filePath ) )
      var str = scala.io.Source.fromBytes( bytes, encoding ).mkString
      //println( "STR: " + str )
      return Success(str)
    } catch {
      case e : Exception => Failure( e )
    }  
       
  }
  
  
  //---------------------------------------------------------------------
  def serializeObject( filePath : String, obj : Serializable ) : Try[Boolean] = {
    try{
      val fos = new FileOutputStream( filePath )
      val oos = new ObjectOutputStream( fos )
      oos.writeObject(obj)
      oos.close
      fos.close()
      Success( true )
    } catch {
      case e : Exception => Failure( e )
    }
  }
  
  
  //---------------------------------------------------------------------
  def deserializeObject( filePath : String ) : Try[Object] = {
    try{
      val fin = new FileInputStream( filePath )
      val ois = new ObjectInputStream( fin )
      val obj = ois.readObject()
      ois.close()
      fin.close()
      Success( obj )
    } catch {
      case e : Exception => Failure( e )
    }
  }
  
  
}