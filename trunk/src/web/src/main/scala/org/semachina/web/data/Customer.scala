package org.semachina.web.data

import reflect.BeanProperty

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Sep 13, 2010
 * Time: 9:17:58 PM
 * To change this template use File | Settings | File Templates.
 */

class Customer() {
  var id: Long = 0

  @BeanProperty
  var name: String = null;
}