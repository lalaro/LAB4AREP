function loadGetMsg() {
    let nameVar = document.getElementById("name").value;
    const xhttp = new XMLHttpRequest();

    xhttp.onload = function() {
        document.getElementById("getrespmsg").innerHTML = this.responseText;
    }

    xhttp.open("GET", "/hello?name=" + nameVar);
    xhttp.send();
}

function loadPostMsg(name) {
    let url = "/hellopost?name=" + name.value;

    fetch(url, {method: 'POST'})
        .then(response => response.text())
        .then(data => document.getElementById("postrespmsg").innerHTML = data);
}
