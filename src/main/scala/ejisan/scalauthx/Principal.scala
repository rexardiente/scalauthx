package ejisan.scalauthx

trait Principal[I] {
  def id: I

  def toSubject: PresentSubject[I] =
    PresentSubject(id, None)

  def toSubject(impersonatorId: I): PresentSubject[I] =
    PresentSubject(id, Some(impersonatorId))
}
