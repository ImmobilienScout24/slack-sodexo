#Sodexo command
[![Build Status](https://travis-ci.org/ImmobilienScout24/slack-sodexo.svg?branch=master)](https://travis-ci.org/ImmobilienScout24/slack-sodexo)
[![FlatMap](https://img.shields.io/badge/flatmap-certified-brightgreen.svg)](https://github.com/ImmobilienScout24/slack-sodexo)
[![NoSpring](https://img.shields.io/badge/spring-absent-brightgreen.svg)](https://github.com/ImmobilienScout24/slack-sodexo)

# Slack integration for ImmobilienScout24 Lounge by Sodexo™
Running

```irc
/sodexo
```

in a channel will attach a picture with today's menu.

# Technical details
A scheduled lambda functions downloads the menu PDF once a week, splits it into daily menus and uploads those to S3.

Another lambda functions sits behind an AWS API GET method gateway (with template mapping because slack's API sucks) and responds to slack slash command requests for sodexo.

## Important notice
In AWS API Gateway, we recommend the following mapping for your GET method:

```javascript
{
"response_url": "$input.params.response_url",
"token": "$input.params.token",
"team_id": "$input.params.team_id",
"team_domain": "$input.params.team_domain",
"channel_id": "$input.params.channel_id",
"channel_name": "$input.params.channel_name",
"user_id": "$input.params.user_id",
"user_name": "$input.params.user_name",
"command": "$input.params.command",
"text": "$input.params.text"
}
```

POST doesn't work because the gateway expects valid JSON, and slack POSTs plain text. Our solution is to use GET with query parameters and create JSON from the `response_url` query parameter.
