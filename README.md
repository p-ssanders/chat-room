#   shoulda-server

Intention is to:
    * Publish "shouldas" to Amazon Kinesis
    * Process the shouldas by pushing them to a WebSocket
    * Display the shouldas on a web page

### Build
`clean install -Daws.access.key=... -Daws.secret.key=...`

### Run

`AWS_ACCESS_KEY=... AWS_SECRET_KEY=... mvn spring-boot:run ???`

