package de.is24


import com.amazonaws.services.lambda.runtime.Context

class Sodexo {
  def handleCall(param: String, context: Context): String = {
    val logger = context.getLogger
    logger.log(s"received : $param")
    "Bauer!!!"
  }
}
