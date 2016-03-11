package ejisan.scalauthx

import java.util.UUID

trait RoleLike[I] {
  /** Role ID */
  def id: I
  /** Role name */
  def name: String
  /** Role description */
  def description: String
}

case class Role(id: UUID, name: String, description: String) extends RoleLike[UUID]
