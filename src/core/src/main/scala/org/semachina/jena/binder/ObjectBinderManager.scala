package org.semachina.jena.binder

import collection.mutable.HashMap

trait ObjectBinderManager {

  protected val converters = new HashMap[Class[_], HashMap[Class[_], ObjectBinder]]

  def addObjectBinder(converter: ObjectBinder) = {
    val sourceConverters =
      converters.getOrElseUpdate(converter.sourceClass, new HashMap[Class[_], ObjectBinder])

    sourceConverters(converter.destClass) = converter
    this
  }

  def getObjectBinder[B](sourceClass: Class[_], destClass: Class[B]): Option[ObjectBinder] = {
    for {
      sourceConverters <- converters.get(sourceClass)
      converter <- sourceConverters.get(destClass)
    } yield {
      return Option(converter)
    }

    //To take care of Numbers (e.g. Int, Long, etc.)!
    if (!sourceClass.equals(classOf[Number]) && classOf[Number].isAssignableFrom(sourceClass)) {
      return getObjectBinder[B](classOf[Number], destClass)
    }

    None
  }

  def getObjectBinder[B](source: AnyRef)(implicit b: Manifest[B]): Option[ObjectBinder] =
    getObjectBinder[B](source.getClass, b.erasure.asInstanceOf[Class[B]])


  def getObjectBinderOption[B](source: AnyRef)(implicit b: Manifest[B]): Option[B] = {
    return getObjectBinder[B](source.getClass)(b)
      .flatMap {
      converter => Option(converter.apply(source)(b))
    }
  }

  def getObjectBinderOption[B](source: AnyRef, destClass: Class[B]): Option[B] = {
    return getObjectBinder(source.getClass, destClass: Class[B])
      .flatMap {
      converter => Option(converter.apply[B](source, destClass))
    }
  }
}