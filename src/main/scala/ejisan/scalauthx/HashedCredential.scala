package ejisan.scalauthx

import java.util.Arrays

/** A [[ejisan.scalauthx.HashedCredential]]
 *
 *  @constructor create a new hashed credential.
 *  @param id the ID of generator factory
 *  @param iteration the iterated time of hash
 *  @param salt
 *  @param hash
 */
class HashedCredential private (
    val id: Int,
    val iteration: Int,
    val salt: Array[Byte],
    val hash: Array[Byte]) {
  require(id >= 0, "ID must be above 0.")
  require(iteration >= 5000, "Iteration times must be more than 5000.")
  require(salt.length >= 16, "Salt length must be more than 16.")
  require(hash.length >= 16, "Hash length must be more than 16.")

  def canEqual(other: Any) = other.isInstanceOf[HashedCredential]

  override def equals(credential: Any): Boolean = credential match {
    case c: HashedCredential =>
      c.canEqual(this) &&
      id == c.id &&
      iteration == c.iteration &&
      Arrays.equals(salt, c.salt) &&
      Arrays.equals(hash, c.hash)
    case _ => false
  }

  override def hashCode() = {
    @inline val p = 41
    p * (
      p * (
        p * (p + id.hashCode) +
        iteration.hashCode) +
      Arrays.hashCode(salt)) +
    Arrays.hashCode(hash)
  }

  override def toString: String = {
    @inline def hex(bytes: Array[Byte]): String =
      bytes.map("%02X".format(_)).mkString.toUpperCase
    s"$id:$iteration:${hex(salt)}:${hex(hash)}"
  }
}

object HashedCredential {
  val tupled = (apply _).tupled

  /** Creates a [[ejisan.scalauthx.HashedCredential]]. */
  def apply(
      id: Int,
      iteration: Int,
      salt: Array[Byte],
      hash: Array[Byte]): HashedCredential =
    new HashedCredential(id, iteration, salt, hash)

  def unapply(c: HashedCredential): Option[(Int, Int, Array[Byte], Array[Byte])] =
    Some((c.id, c.iteration, c.salt, c.hash))

  /** Creates a [[ejisan.scalauthx.HashedCredential]] from string. */
  def fromString(credential: String): HashedCredential =
    try {
      val (id, it, st, hs) = credential.split(':') match {
        case Array(id, it, st, hs) => (id.toInt, it.toInt, st, hs)
        case _ => throw new IllegalArgumentException(s"Illegal credential format.")
      }
      @inline def hex(hex: String): Array[Byte] =
        hex.toSeq.sliding(2, 2).toArray.map(x => Integer.parseInt(x.toString, 16).toByte)
      apply(id, it, hex(st), hex(hs))
    } catch {
      case e: NumberFormatException =>
        throw new IllegalArgumentException(s"Illegal number format.", e)
    }
}
