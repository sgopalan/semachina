package org.semachina.jena.dsl

import com.hp.hpl.jena.util.{iterator => jena}
import jena.Filter
import scala.collection.JavaConversions._
import com.hp.hpl.jena.util.iterator.WrappedIterator
import java.util.Comparator
import java.util.Collections


object ExtendedIteratorWrapper {
  implicit def apply[A](i: jena.ExtendedIterator[A]): ExtendedIteratorWrapper[A] = {
    new ExtendedIteratorWrapper(i);
  }
}

class ExtendedIteratorWrapper[A](val i: jena.ExtendedIterator[A]) {

  /**
  return a new iterator containing only the elements of _this_ which
  pass the filter _f_. The order of the elements is preserved. Does not
  copy _this_, which is consumed to the result is consumed.
   */
  def filterKeep(f: A => Boolean): jena.ExtendedIterator[A] = {
    i.filterKeep(new Filter[A] {
      def accept(o: A): Boolean = f(o)
    })
  }

  /**
  return a new iterator containing only the elements of _this_ which
  are rejected by the filter _f_. The order of the elements is preserved.
  Does not copy _this_, which is consumed to the result is consumed.
   */
  def filterDrop(f: A => Boolean): jena.ExtendedIterator[A] = {
    i.filterDrop(new Filter[A] {
      def accept(o: A): Boolean = f(o)
    })
  }

  def sort(sorter: (A, A) => Int): jena.ExtendedIterator[A] = {
    val sorted = i.toList
    Collections.sort(sorted, new Comparator[A] {
      def compare(o1: A, o2: A) = sorter(o1, o2)
    })
    WrappedIterator.create(sorted.iterator)
  }

  def apply(iterator: A => Unit) : Unit = {
    try {
      i.foreach(iterator)
    }
    finally {
      i.close
    }
  }
}
