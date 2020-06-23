(function () {

    var errorLabel = document.getElementById("login-reg-message");

    /*LOGIN*/
    var loginForm = document.getElementById("login-form");

    loginForm.addEventListener("submit", (e) => {
        e.preventDefault();

        errorLabel.style.visibility = "hidden";

        makePostCall("Login", loginForm,
            function (request) {
                if (request.readyState === XMLHttpRequest.DONE) {
                    var message = request.responseText;

                    if (request.status === 200) {
                        sessionStorage.setItem("username", message);
                        window.location.href = "home.html";
                    } else {
                        errorLabel.innerText = message;
                        errorLabel.style.visibility = "visible";
                    }
                }
            });
    });

    /*REGISTRATION*/
    var registrationForm = document.getElementById("registration-form");

    registrationForm.addEventListener("submit", (e) => {
        e.preventDefault();

        errorLabel.style.visibility = "hidden";

        if (registrationForm.password1.value === registrationForm.password2.value) {
            makePostCall("Registration", registrationForm,
                function (request) {
                    if (request.readyState === XMLHttpRequest.DONE) {
                        var message = request.responseText;

                        if (request.status === 200) {
                            sessionStorage.setItem("username", message);
                            window.location.href = "home.html";
                        } else {
                            errorLabel.innerText = message;
                            errorLabel.style.visibility = "visible";
                        }
                    }
                })
        } else {
            errorLabel.innerText = "Passwords do not match";
            registrationForm.password1.value = "";
            registrationForm.password2.value = "";
            errorLabel.style.visibility = "visible";
        }
    });

})();