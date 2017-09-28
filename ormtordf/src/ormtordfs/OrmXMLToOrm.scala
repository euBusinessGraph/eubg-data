package ormtordfs

import scala.util.{Try, Success, Failure}
import scala.xml.{XML,Elem,Node}
import scala.collection.mutable.ArrayBuffer
import orm._
import common.io.Log

class OrmXMLToOrmModel( xml : Elem ) {

  var parseEntityTypeNotes     = true
  var parseValueTypeNotes      = true
  var parseFactNotes           = true 
  
  val XML_ID                   = "id"
  val XML_REF                  = "ref"
  val XML_ENTITY_TYPE          = "EntityType"
  val XML_ENTITY_TYPE_NAME     = "Name"
  val XML_VALUE_TYPE           = "ValueType"
  val XML_VALUE_TYPE_NAME      = "Name"
  val XML_FACT                 = "Fact"
  val XML_SUB_TYPE_FACT        = "SubtypeFact"
  val XML_FACT_NAME            = "_Name"
  val XML_ROLE                 = "Role"
  val XML_MODEL_NOTE           = "ModelNote"
  val XML_NOTE                 = "Note"
  val XML_NOTE_TEXT            = "Text"
  val XML_SUB_TYPE_META_ROLE   = "SubtypeMetaRole"
  val XML_SUPER_TYPE_META_ROLE = "SupertypeMetaRole"
  val XML_ROLE_PLAYER          = "RolePlayer"
  val XML_ROLE_IS_MANDATORY    = "_IsMandatory"
  val XML_ROLE_MULTIPLICITY    = "_Multiplicity"
  val XML_ROLE_SEQUENCE        = "RoleSequence"
  val XML_REFERENCED_BY        = "ReferencedBy"
  val XML_OBJECT_TYPE          = "ObjectType"
  val XML_FACT_TYPE            = "FactType"  
  
  var model : OrmModel         = null
  
  var types        = scala.collection.mutable.Map[String,Type]()
  var roles        = scala.collection.mutable.Map[String,Role]()
  var facts        = scala.collection.mutable.Map[String,Fact]()
  var subtypeFacts = scala.collection.mutable.Map[String,SubtypeFact]()
    
  
  //-------------------------------------------------------------------------------------------|
  /*
   * Translates an ORM model (in XML) into a simplified internal domain model used as in
   * intermediary step in generating the RDFS
   */
  def toOrmModel : Try[OrmModel] = {
    model = new OrmModel()
    
    generateOrmObjects match {
      case Success( b ) => //skip
      case Failure( f ) => return Failure( f )
    }
    model.types        = types.values.toSet
    model.facts        = facts.values.toSet
    model.subTypeFacts = subtypeFacts.values.toSet    
    
    Success( model )
  } 
  
  
  //-------------------------------------------------------------------------------------------|
  def generateOrmObjects : Try[Boolean] = {
 
    // Generate entity type objects
    var xmlEntityTypes = xml \\ XML_ENTITY_TYPE
    for( et <- xmlEntityTypes )
      xmlToEntityType( et )
      
    // Generate value type objects
    var xmlValueTypes = xml \\ this.XML_VALUE_TYPE   
    for( et <- xmlValueTypes )
      xmlToValueType( et )
      
    // Generate facts
    var xmlFacts = xml \\ XML_FACT    
    for( et <- xmlFacts )
      xmlToFact( et )
      
    // Generate subTypeFacts
    var xmlSubtypeFacts = xml \\ XML_SUB_TYPE_FACT    
    for( et <- xmlSubtypeFacts )
      xmlToSubtypeFact( et )
    
    //Generate annotations
    var xmlModelAnnotations = xml \\ XML_MODEL_NOTE
    for( mn <- xmlModelAnnotations )
      xmlToAnnotation( mn )
             
    Success( true )
  }
  

  //-------------------------------------------------------------------------------------------|
  def xmlToAnnotation( xet : Node ) {
    var objectRef = getModelNoteRef( xet, XML_OBJECT_TYPE )
    if( objectRef != null ){
      if( !types.contains( objectRef ) ){
        Log.warning( this, "Could not find elmement " + objectRef + " referenced by model note " + xet )
      } else{
        types( objectRef ).annotations = mergeAnnotations( types( objectRef ).annotations, parseModelNote( xet ) )
      }           
    } else{    
      var factRef =  getModelNoteRef( xet, XML_FACT_TYPE )    
      if( factRef != null ){
        if( !facts.contains( factRef ) ){
          Log.warning( this, "Could not find elmement " + objectRef + " referenced by model note " + xet )
        }  else{
          facts( factRef ).annotations = mergeAnnotations( facts( factRef ).annotations, parseModelNote( xet ) )
        }
      }
    }    
  }

  
  //-------------------------------------------------------------------------------------------|
  def mergeAnnotations( a1 : Map[String,Seq[String]], a2 : Map[String,Seq[String]] ) :
    Map[String,Seq[String]] = {
    var result = Map[String,Seq[String]]( )
    for( k <- a1 ) result += k
    for( k <- a2 ){
      if( result.contains( k._1 ) ) 
        result += ((k._1, result(k._1) ++ a2(k._1)))
      else result += k          
    }
    result
  }
  
  
  //-------------------------------------------------------------------------------------------|
  def parseModelNote( xet : Node ) : Map[String,Seq[String]] = {
    var notes   = Map[String,Seq[String]]()
    var xmlText = xet \\ XML_NOTE_TEXT
    for( xt <- xmlText ){
      var strl = xt.text.split( ";" )
      for( str <- strl ){    
        var nstr = str.trim()
        if( nstr != "" ){        
          var strs = nstr.split( "->" )
          if( strs.size != 2 ){
            Log.warning( this, "Comment \"" + str + "\" could not be parsed." )
          } else{
            var key   = strs(0).trim()
            var value = ((1 until strs.size) map { i => strs(i)}) mkString( " " )
            if( !notes.contains( key ) ) notes += ((key, Seq[String]()))
            notes += ((key, notes(key) :+ value.trim().replace( "\n", " ") ) )            
          }
        }
      }
    }    
    notes
  }
  

  //-------------------------------------------------------------------------------------------|
  def getModelNoteRef( xet : Node, target : String ) : String = {
    var xmlRefBy = xet \\ XML_REFERENCED_BY
    if( xmlRefBy.size == 1 ){
      var xmlType = xmlRefBy(0) \\ target
      if( xmlType.size == 1 ){
          xmlType(0).attribute( XML_REF ) match {
            case Some( x ) => return x.toString
            case None => return null
          }  
      } else{
         //Log.warning( this, "The model note " + xet + " is not refenced by exactly one elemement" )
      }          	
    } else{
        Log.warning( this, "The model note " + xet +
          " is not refenced by exactly one elemement, found " + xmlRefBy.size )
    }
    null
  }
  
  
  //-------------------------------------------------------------------------------------------|
  def xmlToEntityType( xet : Node ) {
    var id = ""
    xet.attribute( XML_ID ) match {
      case Some( x ) => id = x.toString
      case None => return
    }
    var name = getName( xet.attribute( XML_ENTITY_TYPE_NAME ) )
    if( name == null )
       Log.fail( this, "Entity type with id " + id + " has no name", true )
           
    types += ((id, EntityType( name )))
    
    if( parseEntityTypeNotes ){
      var xmlNotes = xet \\ XML_NOTE
      for( mn <- xmlNotes ){
        types(id).annotations = mergeAnnotations( types(id).annotations, parseModelNote( xet ) )
      }
    }
    
  }
  
  
  //-------------------------------------------------------------------------------------------|
  def getName( opt : Option[Seq[Node]] ) : String = {       
    opt match {
      case Some( x ) => {
        var str = x.toString.trim()
        if( !str.matches( "[a-zA-Z0-9\\-_]+" ) ){
          //Log.warning( this, "The name \"" + x + "\" contains special characters which have been replaced by _" )
          str.replaceAll( "[^a-zA-Z0-9\\-_]+", "_")
        } else
          str
      }
      case None => null
    }
  }
  
  
  //-------------------------------------------------------------------------------------------|
  def xmlToValueType( xet : Node ) {
    var id = ""
    xet.attribute( XML_ID ) match {
      case Some( x ) => id = x.toString
      case None => return
    }
    var name = getName( xet.attribute( this.XML_VALUE_TYPE_NAME ) )
    if( name == null )
      Log.fail( this, "Value type with id " + id + " has no name", true )   
    
    types += ((id, ValueType( name)))
    
    if( parseValueTypeNotes ){
      var xmlNotes = xet \\ XML_NOTE
      for( mn <- xmlNotes ){
        types(id).annotations = mergeAnnotations( types(id).annotations, parseModelNote( xet ) )
      }
    }
    
  }
  

  //-------------------------------------------------------------------------------------------|
  def xmlToFact( xf : Node ) {
    var id = ""
    xf.attribute( XML_ID ) match {
      case Some( x ) => id = x.toString
      case None      => return
    }
   
    var name = getName( xf.attribute( XML_FACT_NAME ) )
    if( name == null )
      Log.fail( this, "Unexpcted", true )
        
    var xmlRoles = xf \\ XML_ROLE
    for( xmlr <- xmlRoles )
      xmlToRole( xmlr )
    
    var xmlRoleSequence = xf \\ XML_ROLE_SEQUENCE
    if( xmlRoleSequence.size != 1 ){
      //Log.warning( this, "Unexpected number of role sequence elements " + xmlRoleSequence.size + " for Fact " + id )
    }
    var factRoles = Seq[Role]()
    var xmlRefRoles = xmlRoleSequence(0) \\ XML_ROLE
    for( xmlrefrole <- xmlRefRoles ){
      var ref : String = ""
      xmlrefrole.attribute( XML_REF ) match {
        case Some( x ) => ref = x.toString                  
        case None => {
           Log.fail( this, "Could not find role reference for role in rolesequence of fact " +  id )
        }
      }
      if( !roles.contains( ref ) )
        Log.fail( this, "Could not find role with reference " +  ref )
      factRoles = factRoles :+ roles( ref )          
    }
    
    facts += ((id, Fact( name, factRoles ) ) )
    
    if( parseFactNotes ){
      var xmlNotes = xf \\ XML_NOTE
      for( mn <- xmlNotes ){
        facts(id).annotations = mergeAnnotations( facts(id).annotations, parseModelNote( xf ) )
      }
    }
    
  }
  
  
  //-------------------------------------------------------------------------------------------|
  def xmlToSubtypeFact( xf : Node ) {
    var id = ""
    xf.attribute( XML_ID ) match {
      case Some( x ) => id = x.toString
      case None => return
    }
   
    var name = getName( xf.attribute( XML_FACT_NAME ) )
    if( name == null )
      Log.fail( this, "Unexpected", true )
        
    var subtypeFactRoles = Seq[SubtypeFactRole]()
    var xmlFactRoles = xf \\ XML_SUB_TYPE_META_ROLE
   
    for( xmlr <- xmlFactRoles ){
      var stfr = xmlToSubtypeFactRole( xmlr, false )
      if( stfr != null ) subtypeFactRoles = subtypeFactRoles :+ stfr
    }
    xmlFactRoles = xf \\ XML_SUPER_TYPE_META_ROLE
   
    for( xmlr <- xmlFactRoles ){
      var stfr = xmlToSubtypeFactRole( xmlr, true )
      if( stfr != null ) subtypeFactRoles = subtypeFactRoles :+ stfr
    }
    
    subtypeFacts += ((id, SubtypeFact( name, subtypeFactRoles)))       
  }
  
  
  //-------------------------------------------------------------------------------------------|
  def xmlToSubtypeFactRole( xf : Node, isSuperType : Boolean ) : SubtypeFactRole = {
    var id = ""
    xf.attribute( XML_ID ) match {
      case Some( x ) => id = x.toString
      case None      => return null
    }
    
    var isMandatory = false
    xf.attribute( this.XML_ROLE_IS_MANDATORY ) match {
      case Some( x ) => {
        if( x.toString == "true" ) isMandatory = true
        else if( x.toString() == "false" ) {}
        else{
          Log.fail( this, "Unexpected mandatory value " + x + " for Role with id " + id )
        }                          
      }
      case None      => Log.fail( this, "Did not find mandatory value for Role with id", true )
    }
    
    var multiplicity : Multiplicity = xmlToMultiplicity( xf.attribute( this.XML_ROLE_MULTIPLICITY ) )

    var rolePlayer : Type = null
    var xmlRolePlayer     = xf \\ XML_ROLE_PLAYER
    if( xmlRolePlayer.size != 1 ){
       Log.warning( this, "Unexpected number of role players " + xmlRolePlayer.size + " for Role with id " + id )     
       return null
    }
    
    var rolePlayerRef : String = ""
    xmlRolePlayer(0).attribute( XML_REF ) match {
      case Some( x ) => rolePlayerRef = x.toString
      case None => {
        println( xmlRolePlayer(0) )
        Log.fail( this, "Could not find role player ref for Role with id " + id )
      }
    }
    
    if( !types.contains( rolePlayerRef ) )
      Log.fail( this, "Could not find type with role player ref " + rolePlayerRef )
    
    if( isSuperType )
      SuperTypeMetaRole( isMandatory, multiplicity, types( rolePlayerRef ).asInstanceOf[NameType] )
    else
      SubTypeMetaRole( isMandatory, multiplicity, types( rolePlayerRef ).asInstanceOf[NameType] )  
  }
  
  
  //-------------------------------------------------------------------------------------------|
  def xmlToMultiplicity( xml : Option[Seq[Node]] ) : Multiplicity = {
    xml match {
      case Some( x ) => {
        if( x.toString == "ZeroToMany" )         ZeroToMany()
        else if( x.toString() == "OneToMany" )   OneToMany()
        else if( x.toString() == "ExactlyOne" )  ExactlyOne()
        else if( x.toString() == "ZeroToOne" )   ZeroToOne()
        else if( x.toString() == "Unspecified" ) Unspecified()
        else{
          Log.fail( this, "Unexpected multiplicity value " + x  )
          null
        }                          
      }
      case None      => {
        Log.fail( this, "Did not find multiplicity value for Role with id", true )
        null
      }
    }
  }
  
  
  //-------------------------------------------------------------------------------------------|
  def xmlToRole( xf : Node ) {
    var id = ""
    xf.attribute( XML_ID ) match {
      case Some( x ) => id = x.toString
      case None      => return
    }
    
    var isMandatory = false
    xf.attribute( this.XML_ROLE_IS_MANDATORY ) match {
      case Some( x ) => {
        if( x.toString == "true" ) isMandatory = true
        else if( x.toString() == "false" ) {}
        else{
          Log.fail( this, "Unexpected mandatory value " + x + " for Role with id " + id )
        }                          
      }
      case None      => Log.fail( this, "Did not find mandatory value for Role with id", true )
    }
    
    var multiplicity : Multiplicity = xmlToMultiplicity( xf.attribute( this.XML_ROLE_MULTIPLICITY ) )

    var rolePlayer : Type = null
    var xmlRolePlayer     = xf \\ XML_ROLE_PLAYER
    if( xmlRolePlayer.size != 1 ){
       Log.warning( this, "Unexpected number of role players " + xmlRolePlayer.size + 
           " for Role with id " + id )
       roles += ((id, Role( isMandatory, multiplicity, null ) ) )
       return
    }
    
    var rolePlayerRef : String = ""
    xmlRolePlayer(0).attribute( XML_REF ) match {
      case Some( x ) => rolePlayerRef = x.toString
      case None => {
        println( xmlRolePlayer(0) )
        Log.fail( this, "Could not find role player ref for Role with id " + id )
      }
    }
    
    if( !types.contains( rolePlayerRef ) )
      Log.fail( this, "Could not find type with role player ref " + rolePlayerRef )
    
    roles += ((id, Role( isMandatory, multiplicity, types( rolePlayerRef ).asInstanceOf[NameType] ) ) )      
  }
    
}

