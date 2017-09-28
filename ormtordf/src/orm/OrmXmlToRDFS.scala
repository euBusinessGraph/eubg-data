package orm

import common.io.Io
import scala.util.{Try, Success, Failure}
import scala.xml.XML
import scala.collection.mutable.ArrayBuffer
import ormtordfs._
import java.io._
import java.nio.charset.CodingErrorAction
import scala.io.Codec
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

object OrmXmlToRDFS {

	var exampleXmlOrmInfile      = "example/ebg-ontology.orm"
  var exampleTurtleRdfsOutFile = "example/ebg-ontology.ttl"
 
  //-------------------------------------------------------------------------------------------|
  def main( args : Array[String] ) = {
   if( args.size == 2 ){
	   xmlORMtoTurtle( args(0), args(1) )         
   } else{
     println( "Expecting two input arguments of the form <infile> <outfile>, where <infile> is " + 
       " the input XML ORM model, and <outfile> is the file containing the converted RDFS " +
       "specifiction (in Turtle format)."  )
   }   
    //println( "args: " + (args mkString( " " ) ) )        
  }
  
  
  
  //-------------------------------------------------------------------------------------------|
  def xmlORMtoTurtle( xmlOrmInFile : String, turtleRdfsOutFile : String ) {
    val xml = XML.loadFile( xmlOrmInFile )
    
    var xmltoorm = new OrmXMLToOrmModel( xml )
    
    xmltoorm.toOrmModel match {
      case Success( orm ) => {
        var ormtoturtle = new OrmToTurtle( orm )
        var turtletext  = ormtoturtle.toTurtleText
        Io.writeToFile( turtleRdfsOutFile,  (turtletext mkString("\n" ) ) )
        println( "Output written to " + turtleRdfsOutFile )                
      }
      case Failure( f ) => {
        println( f )
      }
    }
  }
  
  
  
}