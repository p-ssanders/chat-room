function openWebSocket() {
    var webSocket = new WebSocket("ws://localhost:8080/ws/shouldas");

    webSocket.onopen = function() { logMessage("Opened the Websocket Connection!");};

    webSocket.onmessage = function(event) { logMessage("Result: " + event.data); }
}

function logMessage(msg) {
    document.getElementById("result").appendChild(document.createTextNode(msg + "\n"));
}

