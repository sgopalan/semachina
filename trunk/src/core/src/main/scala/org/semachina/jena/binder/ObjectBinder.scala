package org.semachina.jena.binder

import com.hp.hpl.jena.util.iterator.Map1

object ObjectBinder {
  def apply[A, B](bind: (A => B))(implicit a: Manifest[A], b: Manifest[B]) = {
    val binder = new ObjectBinder {
      def sourceClass = a.erasure

      def destClass = b.erasure

      def apply[C](source: AnyRef, bindClass: Class[C]) =
        bind(source.asInstanceOf[A]).asInstanceOf[C]
    }
    binder
  }

  def apply[A, B](bindMap: Map1[A, B])(implicit a: Manifest[A], b: Manifest[B]) = {
    val binder = new ObjectBinder {
      def sourceClass = a.erasure

      def destClass = b.erasure

      def apply[C](source: AnyRef, bindClass: Class[C]) =
        bindMap.map1(source.asInstanceOf[A]).asInstanceOf[C]
    }
    binder
  }
}

/**
 * Mixin for a Java/Scala Object to Object Mapper
 *
 * Based on what is out there on the internet (see Stack Overflow link below), there are
 * many options for these types of frameworks
 *
 * {@link http://stackoverflow.com/questions/4113823/framework-for-converting-java-objects Stack Overflow}
 */
trait ObjectBinder {
  def sourceClass: Class[_]

  def destClass: Class[_]

  def apply[B](source: AnyRef)(implicit m: Manifest[B]): B = {
    val outputClass = m.erasure.asInstanceOf[Class[B]]
    require(
      destClass.equals(outputClass),
      "DestClass: " + destClass.toString + " MUST equal outputClass: " + outputClass.toString)

    apply[B](source, outputClass)
  }

  def apply[B](source: AnyRef, destClass: Class[B]): B
}