package org.semachina.jena.binder

import com.hp.hpl.jena.ontology.{OntModel, Individual}
import impl.{ScalaBeansToIndividual, ScalaBeansToBean}
import org.semachina.jena.ontology.SemachinaIndividual

/**
 * Helper Object that defines a default <code>IndividualBinder</code> implementation
 */
object IndividualBinder {

  /**
   * Create an instance of the default <code>IndividualBinder</code> implementation
   * @param ontModel The ontModel to base the <code>IndividualBinder</code> on
   * @return The default <code>IndividualBinder</code>
   */
  def default(ontModel: OntModel) =
    new IndividualBinder with ScalaBeansToBean with ScalaBeansToIndividual {
      def getOntModel = ontModel
    }
}

/**
 * Trait that defines basic <code>IndividualBinder<code> behavior
 */
trait IndividualBinder extends ToIndividualBinder with ToBeanBinder

/**
 * Trait that defines Individual to Bean binding interface
 */
trait ToBeanBinder {

  /**
   * returns The ontModel to base the <code>IndividualBinder</code> on
   */
  def getOntModel: OntModel

  /**
   * Maps from <code>Individual</code> to bean of type [V]
   *
   * @param individual The <code>Individual</code> to marshall to a Bean
   * @param m The implicit Manifest that contains the erasure type of [V]
   * @return A mapped bean of type [V]
   */
  def toBean[V <: AnyRef](individual: Individual)(implicit m: Manifest[V]): V =
    toBean[V](individual, m.erasure.asInstanceOf[Class[V]])

  /**
   * Maps from <code>Individual</code> to bean of type [V]
   *
   * @param individual The <code>Individual</code> to marshall to a Bean
   * @param beanClass The <code>Class</code> of type [V]
   * @return A mapped bean of type [V]
   */
  def toBean[V <: AnyRef](individual: Individual, beanClass: Class[V]): V
}

/**
 * Trait that defines Bean to Individual binding interface
 */
trait ToIndividualBinder {

  /**
   * returns The ontModel to base the <code>IndividualBinder</code> on
   */
  def getOntModel: OntModel

  /**
   * Maps from bean to <code>Individual</code>
   *
   * @param bean The bean to map to <code>Individual</code>
   * @param delayIdStrategy Flag indicating if the IdStrategy execution should be delayed after
   * the bean has been mapped
   * @return The mapped <code>Individual</code>
   */
  def toIndividual(bean: AnyRef, delayIdStrategy: Boolean = false): SemachinaIndividual
}
