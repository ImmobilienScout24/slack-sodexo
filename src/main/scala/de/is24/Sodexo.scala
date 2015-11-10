package de.is24


import com.amazonaws.services.lambda.runtime.Context

class Sodexo {
  def handleCall(param: Object, context: Context): String = {
    val logger = context.getLogger
    logger.log(s"received : $param")
    s"""{ "text": "${param.getClass()}", "attachments": [ {
      "image_url":"https://www.google.de/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png",
    "fallback": "Sodexo is a lie." } ] }"""
  }
}
