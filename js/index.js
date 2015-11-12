exports.handler = function(event, context) {
    console.log("got event: " + JSON.stringify(event))
    var d = new Date()
    var weekdays = new Array(7)
    weekdays[0]=  "sunday"
    weekdays[1] = "monday"
    weekdays[2] = "tuesday"
    weekdays[3] = "wednesday"
    weekdays[4] = "thursday"
    weekdays[5] = "friday"
    weekdays[6] = "saturday"

    var weekday = ""
    if(event.text !== undefined && event.text !== "" && event.text !== null) {
        weekday = event.text.toLowerCase()
    } else {
        weekday = weekdays[d.getDay()]
    }

    var attachment = {
        "attachments": [
        {
            "fallback": "IS24 canteen menu for " + weekday,
            "image_url": "https://s3-eu-west-1.amazonaws.com/sodexo-slack/" + weekday + ".png"
        }
        ],
        "response_type": "in_channel",
        "text": "Canteen menu for " + weekday + " (brought to you by S.H.I.E.L.D.)"
    }

    var invalidWeekday = {
        "response_type": "in_channel",
        "text": event.user_name + " fails at weekdays. '" + weekday + "' is not a valid weekday, please ask your colleagues about valid values."
    }

    var spammer = {
        "response_type": "ephemeral",
        "text": "Please don't use the sodexo command in #general."
    }

    if(event.channel_name === "general"){
        context.succeed(spammer)
    }else if(weekdays.indexOf(weekday) >= 0){
        context.succeed(attachment)
    } else context.succeed(invalidWeekday)
}
