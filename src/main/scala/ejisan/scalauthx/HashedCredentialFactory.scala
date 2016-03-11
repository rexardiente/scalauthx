package ejisan.scalauthx

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import scala.concurrent.{ ExecutionContext, Future }

/** A [[ejisan.scalauthx.HashedCredentialFactory]]
 *
 *  @constructor create a new factory.
 *  @param id that is for identifying this factory
 *  @param keyAlgorithm the algorithm of [[javax.crypto.SecretKeyFactory]]
 *  @param rngAlgorithm the algorithm of [[java.security.SecureRandom]]
 *  @param saltLength the length of string
 *  @param hashLength the length of hash
 *  @param iteration the iteration time of hashing
 *  @param keyProvider the provider of [[javax.crypto.SecretKeyFactory]]
 *  @param rngProvider the provider of [[java.security.SecureRandom]]
 */
case class HashedCredentialFactory(
    id: Int,
    keyAlgorithm: String,
    rngAlgorithm: String,
    saltLength: Int,
    hashLength: Int,
    iteration: Int,
    keyProvider: Option[String] = None,
    rngProvider: Option[String] = None) {
  require(id >= 0, "ID must be above 0.")
  require(iteration >= 5000, "Iteration times must be more than 5000.")
  require(saltLength >= 16, "Salt length must be more than 16.")
  require(hashLength >= 16, "Hash length must be more than 16.")
  require(keyFactory.isInstanceOf[SecretKeyFactory])
  require(prng.isInstanceOf[SecureRandom])

  private def keyFactory: SecretKeyFactory = keyProvider
    .map(SecretKeyFactory.getInstance(keyAlgorithm, _))
    .getOrElse(SecretKeyFactory.getInstance(keyAlgorithm))

  private def prng: SecureRandom = keyProvider
    .map(SecureRandom.getInstance(rngAlgorithm, _))
    .getOrElse(SecureRandom.getInstance(rngAlgorithm))

  private def randomSalt: Array[Byte] = {
    val salt = Array.ofDim[Byte](saltLength)
    prng.nextBytes(salt)
    salt
  }

  /** Generates a [[ejisan.scalauthx.HashedCredential]] with given salt. */
  def generate(credential: String, salt: Array[Byte])(implicit ec: ExecutionContext)
    : Future[HashedCredential] = {
    require(
      salt.length == saltLength,
      s"Invalid salt length ${salt.length}. It must be $saltLength")
    Future {
      val hash = keyFactory
        .generateSecret(new javax.crypto.spec.PBEKeySpec(
          credential.toCharArray, salt, iteration, hashLength * 8))
        .getEncoded()
      HashedCredential(id, iteration, salt, hash)
    }
  }

  /** Generates a [[ejisan.scalauthx.HashedCredential]]. */
  def generate(credential: String)(implicit ec: ExecutionContext): Future[HashedCredential] =
    generate(credential, randomSalt)

  /** Verifies a credential for target [[ejisan.scalauthx.HashedCredential]]. */
  def verify(credential: String, target: HashedCredential)(implicit ec: ExecutionContext)
    : Future[Boolean] = {
    require(target.id == id, "Unsuited credential given. (id)")
    require(target.iteration == iteration, "Unsuited credential given. (iteration)")
    require(target.salt.length == saltLength, "Unsuited credential given. (salt length)")
    require(target.hash.length == hashLength, "Unsuited credential given. (hash length)")
    generate(credential, target.salt).map(c => java.util.Arrays.equals(c.hash, target.hash))
  }
}

object HashedCredentialFactory {
  val tupled = (apply _).tupled

  /** Creates default [[ejisan.scalauthx.HashedCredentialFactory]]. */
  def get: HashedCredentialFactory =
    apply(0, "PBKDF2WithHmacSHA512", "NativePRNGNonBlocking", 32, 32, 20000)
}
