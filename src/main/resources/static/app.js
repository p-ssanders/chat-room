function openWebSocket() {
    var webSocket = new WebSocket("ws://localhost:8080/ws/shouldas");

    webSocket.onmessage = function(event) { newShoulda(event.data); }
}

function newShoulda(text) {
    var node = document.createElement("div");
    node.className = "shoulda";
    var textnode = document.createTextNode(text);
    node.append(textnode);

    document.getElementById("shouldas").appendChild(node);
}

