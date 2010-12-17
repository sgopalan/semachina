package org.semachina.web.data

import reflect.BeanProperty
import org.joda.time.DateTime


/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: Sep 13, 2010
 * Time: 9:17:58 PM
 * To change this template use File | Settings | File Templates.
 */

class Article(val id: Long, val title: String, val desc: String, val created: DateTime) {
}