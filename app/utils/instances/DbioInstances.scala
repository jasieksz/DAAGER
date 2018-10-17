package utils.instances

import cats.{Monad, Semigroup}
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext

object DbioInstances {

  implicit def dbioMonad(implicit ec: ExecutionContext): Monad[DBIO] =
    new Monad[DBIO] {

      override def product[A, B](fa: DBIO[A], fb: DBIO[B]): DBIO[(A, B)] =
        fa.zip(fb)

      override def map[A, B](fa: DBIO[A])(f: (A) => B): DBIO[B] = fa.map(f)

      override def pure[A](x: A): DBIO[A] = DBIO.successful(x)

      override def flatMap[A, B](fa: DBIO[A])(f: (A) => DBIO[B]): DBIO[B] =
        fa.flatMap(f)(ec)

      override def tailRecM[A, B](a: A)(f: (A) => DBIO[Either[A, B]]): DBIO[B] =
        f(a).flatMap {
          case Left(nextA) => tailRecM(nextA)(f)
          case Right(b)    => pure(b)
        }(ec)
    }

  implicit def dbioSemigroup[A : Semigroup](implicit ec: ExecutionContext): Semigroup[DBIO[A]] =
    new Semigroup[DBIO[A]] {
      override def combine(fx: DBIO[A], fy: DBIO[A]): DBIO[A] = {
        import cats.syntax.semigroup._
        (fx zip fy).map { case (x, y) => x |+| y }
      }
    }

}
