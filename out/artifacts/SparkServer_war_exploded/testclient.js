/**
 * Created by haw on 17/06/15.
 */
/*
 @author: Hesham Adel
 @Date: 9th June, 2015
 */

// Temporary div to show the output
var output = document.getElementById("output");

// function to write to the output div
function writeToScreen(message) {
    output.innerHTML += message + "<br>";
}

// creating the JavaScript Web Socket
var wsUri = "ws://" + document.location.host + document.location.pathname + "messageendpoint";
var websocket = new WebSocket(wsUri);

// onerror socket event function
websocket.onerror = function(evt) { onError(evt) };

function onError(evt) {
    writeToScreen('<span style="color: red;">ERROR:</span> ' + evt.data);
}

// onopen socket event function
websocket.onopen = function(evt) { onOpen(evt) };

function onOpen() {
    writeToScreen("Connected to " + wsUri);
}
// onmessage socket event function
websocket.onmessage = function(evt) { onMessage(evt) };

function onMessage(evt) {
    console.log("received: " + evt.data);
    writeToScreen(evt.data);
}

//send text to the server socket
function sendText(message) {
    console.log("sending text: " + message);
    websocket.send(message);
}

var send_button = document.getElementById("send_button");

send_button.onclick = function(){
    var message_input = document.getElementById("my_message").value;
    //sendText(message_input);
}

//////
function handleFileSelect(evt) {
    evt.stopPropagation();
    evt.preventDefault();
    var f;
    if (evt.type == "drop"){
        f = evt.dataTransfer.files[0]; // FileList object.
    }else{
        f = evt.target.files[0]; // FileList object
    }

    // files is a FileList of File objects. List some properties.
    var output = [];

    //to list the file proerties to the list div
    output.push('<li><strong>', escape(f.name), '</strong> (', f.type || 'n/a', ') - ',
        f.size, ' bytes, last modified: ',
        f.lastModifiedDate ? f.lastModifiedDate.toLocaleDateString() : 'n/a',
        '</li>');
    document.getElementById('list').innerHTML = '<ul>' + output.join('') + '</ul>';

    var fr = new FileReader();
    fr.onload = function () {
        // 'this' references 'fr', our 'FileReader' instance
        var fileContents = this.result;

        // Do someting with 'fileContents'...
        console.log(fileContents);
        var lines = fileContents.split('\n');
        for(var line = 0; line < lines.length; line++){
            console.log(lines[line]);
            sendText(lines[line]);
        }
    };
    fr.readAsText( f );

}

// to handle the draging of file over the drop zone div
function handleDragOver(evt) {
    evt.stopPropagation();
    evt.preventDefault();
    evt.dataTransfer.dropEffect = 'copy'; // Explicitly show this is a copy.
}

// Setup the dnd listeners and the input file listener.
var dropZone = document.getElementById('drop_zone');
dropZone.addEventListener('dragover', handleDragOver, false);
dropZone.addEventListener('drop', handleFileSelect, false);

document.getElementById('files').addEventListener('change', handleFileSelect, false);
