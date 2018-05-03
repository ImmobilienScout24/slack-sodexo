exports.handler = function(event, context) {
  console.log("got event: " + JSON.stringify(event))
  Date.prototype.getWeek = function () {
    // Create a copy of this date object
    var target  = new Date(this.valueOf());

    // ISO week date weeks start on monday
    // so correct the day number
    var dayNr   = (this.getDay() + 6) % 7;

    // ISO 8601 states that week 1 is the week
    // with the first thursday of that year.
    // Set the target date to the thursday in the target week
    target.setDate(target.getDate() - dayNr + 3);

    // Store the millisecond value of the target date
    var firstThursday = target.valueOf();

    // Set the target to the first thursday of the year
    // First set the target to january first
    target.setMonth(0, 1);
    // Not a thursday? Correct the date to the next thursday
    if (target.getDay() != 4) {
      target.setMonth(0, 1 + ((4 - target.getDay()) + 7) % 7);
    }

    // The weeknumber is the number of weeks between the
    // first thursday of the year and the thursday in the target week
    return 1 + Math.ceil((firstThursday - target) / 604800000); // 604800000 = 7 * 24 * 3600 * 1000
  }
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
        "image_url": "https://s3-eu-west-1.amazonaws.com/sodexo-slack/" + d.getWeek() + "_" + weekday + ".png"
      }
    ],
    "response_type": "in_channel",
    "text": "Canteen menu for " + weekday + " (brought to you by Shield)"
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
