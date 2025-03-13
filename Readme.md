##### Useful commands

```
docker run -p 8080:8080 xp-springboot-apigateway
```

#### 1. Setup AuthO

Fill out AuthO Application Settings / Credentials in auth_config.json

It should be located in the client/src folder

Note the audience value is found in the "APIs" tab when you are looking at your application in the AuthO dashboard

```json
{
  "domain": "<APPLICATION_DOMAIN>",
  "clientId": "<APPLICATION_CLIENTID>",
  "audience": "<APPLICATION_API_IDENTIFIER>"
}
```

When you are looking at your application in the AuthO dashboard, go to the "Credentials" tab and iin the Authentication Method secion, make sure "None" is selected. Other options say "mTLS (CA-signed)", "Client Secret (Post)" , etc.
