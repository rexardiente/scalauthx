package ejisan.scalauthx

import scala.language.higherKinds

trait SubjectLike[I] {
  def optId: Option[I]

  def optImpersonatorId: Option[I]

  def isAnonymous: Boolean = optId.isEmpty

  def isImpersonated: Boolean = optImpersonatorId.isEmpty
}

sealed class Subject[I](
    val optId: Option[I],
    val optImpersonatorId: Option[I])
  extends SubjectLike[I] {
  def impersonateAs(target: I): PresentSubject[I] = PresentSubject(target, optId)

  def impersonateAsAnPresent: Subject[I] = AnonymousSubject(optId)
}

object Subject {
  def apply[I](optId: Option[I], optImpersonatorId: Option[I]): Subject[I] =
    new Subject(optId, optImpersonatorId)

  def unapply[I](subject: SubjectLike[I]): Option[(Option[I], Option[I])] =
    Some((subject.optId, subject.optImpersonatorId))
}


final class PresentSubject[I](
    val id: I,
    optImpersonatorId: Option[I])
  extends Subject[I](Some(id), optImpersonatorId)

object PresentSubject {
  def apply[I](id: I, optImpersonatorId: Option[I]): PresentSubject[I] =
    new PresentSubject(id, optImpersonatorId)

  def unapply[I](subject: SubjectLike[I]): Option[(I, Option[I])] =
    Subject.unapply(subject) match {
      case Some((Some(id), optImpersonatorId)) => Some((id, optImpersonatorId))
      case _ => None
    }
}

object AnonymousSubject {
  def apply[I](optImpersonatorId: Option[I]): Subject[I] =
    new Subject(None, optImpersonatorId)

  def unapply[I](subject: SubjectLike[I]): Option[Option[I]] =
    Subject.unapply(subject) match {
      case Some((None, impersonatorId)) => Some(impersonatorId)
      case _ => None
    }
}
