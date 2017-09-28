package ormtordfs


//import scala.util.{Try, Success, Failure}
//import scala.xml.XML
import scala.collection.mutable.ArrayBuffer

import common.io.{Log, Io}
import orm._

class OrmToTurtle( model : OrmModel ) {
 
   val ANNOTATION_KEY_RDFS = "rdfs"  
   val DOMAIN_PROPERTY     = "schema:domainIncludes"
   val RANGE_PROPERTY      = "schema:rangeIncludes"
   
   var turtleText       = ArrayBuffer[String]()
   var generatedClasses = Set[String]()
  
   
   //-------------------------------------------------------------------------------------------|
   /*
    * Translates the ORM model (given as argument to the class) into turtle text defining the
    * corresponding RDFS
    */
   def toTurtleText : ArrayBuffer[String] = {   
    turtleText = ArrayBuffer[String]()
    
    //Helper function used to sort types so that they are printed in alphabetical order
    val leqType : (Type,Type) => Boolean = { (x,y) =>
      var n1 = getRDFSName( x.annotations )
      var n2 = getRDFSName( y.annotations )
      if( n1 == null && n2 == null ) true
      else if( n1 == null && n2 != null ) true
      else if( n1 != null && n2 == null ) false      
      else  n1 <= n2
    }
    
    //Helper function used to sort facts so that they are printed in alphabetical order
    val leqFact : (Fact,Fact) => Boolean = { (x,y) =>
      var dn1 = getFactDomainRDFSName( x )
      var dn2 = getFactDomainRDFSName( y )
      if( dn1 != null && dn2 != null && dn1 < dn2 )      true
      else if( dn1 != null && dn2 != null && dn1 > dn2 ) false
      else{
        var n1 = getRDFSName( x.annotations )
        var n2 = getRDFSName( y.annotations )
        if( n1 == null && n2 == null )      true
        else if( n1 == null && n2 != null ) true
        else if( n1 != null && n2 == null ) false      
        else  n1 <= n2
      }      
    }     
    
    turtleText += "\n#### Generated Classes\n"    
    var sortedTypeList = model.types.toList.sortWith( leqType )
           
    for( t <- sortedTypeList )
      ormTypeToText( t  )
      
    turtleText += "\n#### Generated Properties\n"
    var sortedFactList = model.facts.toList.sortWith( leqFact )
        
    for( f <- sortedFactList )
      ormFactToText( f )
              
    turtleText
  }
  
  
  //-------------------------------------------------------------------------------------------|
  def getFactDomainRDFSName( f : Fact ) : String = {
    if( f.factRoles.size > 0 ) getRDFSName( f.factRoles(0).rolePlayer.annotations )
    else null
  }

   
  //-------------------------------------------------------------------------------------------|
  def ormTypeToText( t : Type ){
    t match {
      case vt : ValueType  => ormValueTypeToText( vt )
      case et : EntityType => ormEntityTypeToText( et )
      case x               => Log.fail( this, "Unexpected orm type " + x, true )
    }
  }
  

  //-------------------------------------------------------------------------------------------|
  def ormValueTypeToText( vt : ValueType ){
    //Conversion of value types not needed yet  
  }

    
  //-------------------------------------------------------------------------------------------|
  def getRDFSName( annotations : Map[String,Seq[String]] ) : String = {
   if( annotations.contains( ANNOTATION_KEY_RDFS ) )
      annotations( ANNOTATION_KEY_RDFS )(0)
    else
      null
  }
  
  
  //-------------------------------------------------------------------------------------------|
  def ormEntityTypeToText( et : EntityType ){
    var name = getRDFSName( et.annotations )
    if( name == null )
      Log.warning( this, "Could not find the rdfs name of " + et )
    else if( !generatedClasses.contains( name ) ){
      var annos = annotationsToText( et.annotations )
      var subs  = getAllSuperTypeNames( et ).toSeq
      var str   = name + " a rdfs:Class"
      if( annos.isEmpty && subs.isEmpty ) str = str + " ."      
      else str = str + " ;"
      turtleText += str
      
  		for( i <- 0 until subs.size ){
        if( i == subs.size -1 && annos.isEmpty )
          turtleText += "  rdfs:subClassOf " + subs(i) + " ."
        else
          turtleText += "  rdfs:subClassOf " + subs(i) + " ;"
      }
      
      for( i <- 0 until annos.size ){
        if( i == annos.size - 1 ) turtleText += annos(i) + " ."
        else turtleText += annos(i) + " ;"
      }    
          	
      generatedClasses = generatedClasses + name
      
    	turtleText += ""          
    }      
  }
  
  
  //-------------------------------------------------------------------------------------------|
  def getAllSuperTypeNames( et : EntityType ) : Set[String] = {
    var supertypeNames = Set[String]()
    for( stf <- model.subTypeFacts ){
      var subtype   : NameType = null
      var supertype : NameType = null
      
      for( r <- stf.factRoles ){
        r match {
          case SubTypeMetaRole( i, m, rp )   => subtype   = rp
          case SuperTypeMetaRole( i, m, rp ) => supertype = rp
          case x => Log.fail( this, "Unexpected type name", true )
        }
      }
      if( subtype == et ){
        var sname = getRDFSName( supertype.annotations )
        if( sname != null )
          supertypeNames = supertypeNames + sname      
      }
    }
    
    supertypeNames
  }
  
 
  //-------------------------------------------------------------------------------------------|
  def ormFactToText( f : Fact ){    
    if( f.factRoles.size == 1 ){
      ormUnaryFactToText( f : Fact )
    } else if( f.factRoles.size == 2 )
      ormBinaryFactToText( f )
    else{
      Log.warning( this, "Translation for non-binary facts has no been defined yet. Fact id: " + f.name )     
    }
  }
  

  //-------------------------------------------------------------------------------------------|
  // Unary facts are always translated into into properties whose range is a boolean
  def ormUnaryFactToText( f : Fact ){
    var propertyName = getRDFSName( f.annotations )
    if( propertyName == null )
      Log.warning( this, "Could not find the rdfs name of " + f )
    else{
    	var domainName = getRDFSName( f.factRoles(0).rolePlayer.annotations )
      var annos      = annotationsToText( f.annotations )
      
      var str = propertyName + " a owl:DatatypeProperty;"
      turtleText += str      
      
      if( domainName != null ){        
        turtleText += "  " + DOMAIN_PROPERTY + " " + domainName + " ;"        
      } else{
        Log.warning( this, "Could not find the rdfs name of domain of " +  f )
      }
      str = "  " + RANGE_PROPERTY + " xsd:boolean"
      if( annos.isEmpty ) str = str + " ."
      else str = str + ";"
      turtleText += str      
    
      for( i <- 0 until annos.size ){
        if( i == annos.size - 1 ) turtleText += annos(i) + " ."
        else turtleText += annos(i) + " ;"
      }    
      turtleText += ""
    }
  }

  
  //-------------------------------------------------------------------------------------------|
  def ormBinaryFactToText( f : Fact ){
    if( f.factRoles(0).rolePlayer.isInstanceOf[ValueType] ){    
      Log.fail( this, "Fact " + f + " has value type " + f.factRoles(0).rolePlayer + " at index 0", true )
    }
      
    if( f.factRoles(1).rolePlayer.isInstanceOf[ValueType] )
      ormBinaryFactWValueTypeToText( f )
    else if( f.factRoles(1).rolePlayer.isInstanceOf[EntityType] )
      ormBinaryFactWEntityTypeToText( f )
    else if( f.factRoles(0).rolePlayer == null || f.factRoles(1).rolePlayer == null ){
      Log.warning( this, "Did not translate fact " + f + " since some player roles are missing" ) 
    }
    else{      
      Log.fail( this, "Unexpected", true )
    }
  }
  
  
  //-------------------------------------------------------------------------------------------|
  def ormBinaryFactWValueTypeToText( f : Fact ){
    var propertyName = getRDFSName( f.annotations )
    if( propertyName == null )
      Log.warning( this, "Could not find the rdfs name of " + f )
    else{      
    	var domainName = getRDFSName( f.factRoles(0).rolePlayer.annotations )
 			var rangeName  = getRDFSName( f.factRoles(1).rolePlayer.annotations )
      var annos      = annotationsToText( f.annotations )
      
      //NOTE: translate to an object property without range, if range cannot be found
      var str : String = null
      if( rangeName == null )
        str = propertyName + " a owl:ObjectProperty"
      else
        str = propertyName + " a owl:DatatypeProperty"
        
      if( domainName == null && rangeName == null && annos.isEmpty ) str = str + " ."
      else str = str + " ;"   
      turtleText += str
        
      if( domainName != null ){
        str = "  " + DOMAIN_PROPERTY + " " + domainName    	 
        if( rangeName == null && annos.isEmpty ) str = str + " ."
        else str = str + " ;"
        turtleText += str
      } else{
        Log.warning( this, "Could not find the rdfs name of " +  f )
      }
      
      if( rangeName != null ){
        str = "  " + RANGE_PROPERTY + " " + rangeName
        if( annos.isEmpty ) str = str + " ."
        else str = str + " ;"
        turtleText += str
      } else{
        Log.warning( this, "Could not find the rdfs name of " +  f )
      }           
          
      for( i <- 0 until annos.size ){
        if( i == annos.size - 1 ) turtleText += annos(i) + " ."
        else turtleText += annos(i) + " ;"
      }      
    
      turtleText += ""
    }
  }
  

  //-------------------------------------------------------------------------------------------|
  def ormBinaryFactWEntityTypeToText( f : Fact ){
    var propertyName = getRDFSName( f.annotations )
    if( propertyName == null )
      Log.warning( this, "Could not find the rdfs name of " + f )
    else{      
    	var domainName = getRDFSName( f.factRoles(0).rolePlayer.annotations )
 			var rangeName  = getRDFSName( f.factRoles(1).rolePlayer.annotations )
      var annos      = annotationsToText( f.annotations )
      
      var str =  propertyName + " a owl:ObjectProperty"
      if( domainName == null && rangeName == null && annos.isEmpty ) str = str + " ."
      else str = str + " ;"
      
      turtleText += str
      
      if( domainName != null ){
        str = "  " + DOMAIN_PROPERTY + " " + domainName 
        if( rangeName == null && annos.isEmpty ) str = str + " ."
        else str = str + " ;"
        turtleText += str        
      } else{
        Log.warning( this, "Could not find the rdfs name of " +  f )
      }
      
      if( rangeName != null ){
        str = "  " + RANGE_PROPERTY + " " + rangeName        
        if( annos.isEmpty ) str = str + " ."
        else str = str + " ;"
        turtleText += str        
      } else{
        Log.warning( this, "Could not find the rdfs name of " + f )
      }           
    
      for( i <- 0 until annos.size ){
        if( i == annos.size - 1 ) turtleText += annos(i) + " ."
        else turtleText += annos(i) + " ;"
      }

      
      turtleText += ""
    }   
  }
  
  
  //-------------------------------------------------------------------------------------------|
  def annotationsToText( map : Map[String,Seq[String]] ) : Seq[String] = {
    var al = Seq[String]()
    for( k <- map ){
      if( k._1 != ANNOTATION_KEY_RDFS ){
        for( v <- k._2 )
          al = al :+ ("  " + k._1 + " " + v )
      }
    }    
    al
  }
  
  
}


