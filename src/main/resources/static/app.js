function openWebSocket() {
    var webSocket = new WebSocket("ws://localhost:8080/websockets/chat-messages");

    webSocket.onmessage = function(event) { processNewMessage(event.data); }
}

function processNewMessage(messageText) {
    var node = document.createElement("div");
    node.className = "message";
    var textnode = document.createTextNode(messageText);
    node.append(textnode);

    document.getElementById("messages").prepend(node);
}

function sendMessage() {
    var chatMessage = document.getElementById('messageText').value;

    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", "http://localhost:8080/chat-messages");
    xmlhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    xmlhttp.send(JSON.stringify({
        "text": chatMessage
    }));

    document.getElementById('messageText').value = '';
}

