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
"response_url": "$input.params().querystring.response_url",
"token": "$input.params().querystring.token",
"team_id": "$input.params().querystring.team_id",
"team_domain": "$input.params().querystring.team_domain",
"channel_id": "$input.params().querystring.channel_id",
"channel_name": "$input.params().querystring.channel_name",
"user_id": "$input.params().querystring.user_id",
"user_name": "$input.params().querystring.user_name",
"command": "$input.params().querystring.command",
"text": "$input.params().querystring.text"
}
```

POST doesn't work because the gateway expects valid JSON, and slack POSTs plain text. Our solution is to use GET with query parameters and create JSON from the `response_url` query parameter.
