exports.handler = function(event, context) {
    var d = new Date();
    var weekdays = new Array(7);
    weekdays[0]=  "sunday";
    weekdays[1] = "monday";
    weekdays[2] = "tuesday";
    weekdays[3] = "wednesday";
    weekdays[4] = "thursday";
    weekdays[5] = "friday";
    weekdays[6] = "saturday";

    var weekday = weekdays[d.getDay()];

    var attachment = {
        "attachments": [
        {
            "fallback": "No food today",
            "image_url": "https://s3-eu-west-1.amazonaws.com/sodexo-slack/" + weekday + ".png"
        }
        ],
        "response_type": "in_channel",
        "text": "Aktuelles Menü (brought to you by S.H.I.E.L.D.)"
    }
    
    context.succeed(attachment);
};
