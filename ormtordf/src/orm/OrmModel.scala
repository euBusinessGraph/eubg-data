package orm

/*
 * Simplified ORM domain model used as an intermediate step in generating RDFS from the
 * XML ORM model
 */

class OrmModel {

  var types        = Set[Type]()  
  var facts        = Set[Fact]()
  var subTypeFacts = Set[SubtypeFact]()
    
}


abstract class Type {
  
	var annotations = Map[String,Seq[String]]()

}

abstract class NameType( n : String ) extends Type {
  
  def name : String = n
    
}

case class EntityType( n : String ) extends NameType( n ) 

case class ValueType( n : String ) extends NameType( n ) 

case class Fact( name : String, factRoles : Seq[Role] ){
  var annotations = Map[String,Seq[String]]()
}

case class SubtypeFact( name : String, factRoles : Seq[SubtypeFactRole] )

case class Role( isMandatory : Boolean, m : Multiplicity, rolePlayer : NameType )


abstract class SubtypeFactRole

case class SubTypeMetaRole( isMandatory : Boolean, m : Multiplicity, rolePlayer : NameType ) extends SubtypeFactRole

case class SuperTypeMetaRole( isMandatory : Boolean, m : Multiplicity, rolePlayer : NameType ) extends SubtypeFactRole


abstract class Multiplicity

case class ZeroToMany() extends Multiplicity

case class OneToMany() extends Multiplicity

case class ExactlyOne() extends Multiplicity

case class ZeroToOne() extends Multiplicity

case class Unspecified() extends Multiplicity

