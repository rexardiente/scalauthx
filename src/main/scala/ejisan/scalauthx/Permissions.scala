package ejisan.scalauthx

trait Permissions extends Enumeration {
  type Permission = Value
}

object Permissions extends Permissions {
  val View = Value("VIEW")
  val Edit = Value("EDIT")
  val Create = Value("CREATE")
  val Delete = Value("DELETE")
  val Undelete = Value("UNDELETE")
}
