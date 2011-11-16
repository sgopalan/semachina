package org.semachina.resume.geogoogle

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 2/13/11
 * Time: 12:15 PM
 * To change this template use File | Settings | File Templates.
 */

case class Address (
  formattedAddress : String,
  locality : String,
  postalCode : String,
  country : String,
  lat : String,
  long : String)