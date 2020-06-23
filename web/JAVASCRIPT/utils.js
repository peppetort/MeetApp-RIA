function makePostCall(url, formElement, callback, reset = true) {
    var request = new XMLHttpRequest();

    request.onreadystatechange = function () {
        callback(request)
    }

    request.open("POST", url);
    if(formElement == null){
        request.send();
    }else {
        request.send(new FormData(formElement));
    }

    if(formElement !== null && reset === true){
        formElement.reset();
    }
}

function makeGetCall(url, attributes, callback) {
    var request = new XMLHttpRequest();
    var formattedUrl

    if(attributes != null) {
        formattedUrl = url + "?" + attributes;
    }else {
        formattedUrl = url;
    }


    request.onreadystatechange = function () {
        callback(request)
    }

    request.open("GET", formattedUrl);
    request.send();
}