(function () {

    var myMeetings, invitations, personalMessage, creationWizard, registryWindow;

    window.addEventListener("load", () => {
        var creationForm = document.getElementById("creation-form");
        var creationErrorLabel = document.getElementById("creation-error");
        var personalMessageLabel = document.getElementById("name-label");
        var meetingsContainer = document.getElementById("meetings-container");
        var meetingsErrorLabel = document.getElementById("meetings-error");
        var invitationsContainer = document.getElementById("invitations-container");
        var invitationsErrorLabel = document.getElementById("invitations-error");
        var refreshInvitationsButton = document.getElementById("refresh-button");

        personalMessage = new PersonalMessage(sessionStorage.getItem("username"), personalMessageLabel);
        personalMessage.show();

        myMeetings = new MeetingsList(meetingsContainer, "GetMeetings", meetingsErrorLabel);
        myMeetings.reset();

        invitations = new MeetingsList(invitationsContainer, "GetInvitations", invitationsErrorLabel, refreshInvitationsButton);
        invitations.reset();

        creationWizard = new CreationWizard(creationForm, "CreateMeeting", creationErrorLabel);

    }, false)

    function MeetingsList(meetingsContainer, page, errorLabel, refreshButton) {

        var self = this;

        self.meetingsContainer = meetingsContainer;
        self.page = page
        self.errorLabel = errorLabel;
        self.refreshButton = refreshButton;

        if (self.refreshButton !== undefined) {
            self.refreshButton.addEventListener("click", () => {
                self.reset();
            });
        }

        self.reset = function () {
            self.meetingsContainer.style.visibility = "hidden";
            self.meetingsContainer.innerHTML = "";
            self.errorLabel.style.visibility = "hidden";
            self.show();
        }

        self.show = function () {
            makeGetCall(self.page, null,
                function (request) {
                    if (request.readyState === 4) {
                        var message = request.responseText;

                        if (request.status === 200) {
                            self.update(JSON.parse(message));
                        } else {
                            self.errorLabel.innerText = message;
                            self.errorLabel.style.visibility = "visible";
                        }
                    }
                });
        }

        self.update = function (arrayMeetings) {
            var length = arrayMeetings.length;
            var titleLabel, dateLabel, timeLabel, message, spaceMark;
            var startTime, endTime;

            if (length === 0) {
                message = document.createElement("p");
                message.innerText = "Still no meetings";
                self.meetingsContainer.appendChild(message);
            } else {
                arrayMeetings.forEach(
                    function (meeting) {
                        titleLabel = document.createElement("h3");
                        titleLabel.innerText = meeting.title;

                        dateLabel = document.createElement("p");
                        dateLabel.innerText = "Date: " + meeting.meetingDate;

                        timeLabel = document.createElement("p");
                        startTime = String(meeting.startTime).substring(0, 5) + " " + String(meeting.startTime).substring(9, 11);
                        endTime = String(meeting.endTime).substring(0, 5) + " " + String(meeting.endTime).substring(9, 11);
                        timeLabel.innerText = "Time: " + startTime + " - " + endTime;

                        spaceMark = document.createElement("hr");

                        self.meetingsContainer.appendChild(titleLabel);
                        self.meetingsContainer.appendChild(dateLabel);
                        self.meetingsContainer.appendChild(timeLabel);
                        self.meetingsContainer.appendChild(spaceMark);
                    })
            }
            self.meetingsContainer.style.visibility = "visible";
        }

    }

    function RegistryWindow() {
        var self = this;

        self.times = 0;
        self.maxParticipants = parseInt(sessionStorage.getItem("maxParticipants"));


        self.overlay = document.getElementById("page-overlay");
        self.dialog = document.getElementById("registry-dialog");
        self.dialogMain = self.dialog.querySelector(".dialog-main");
        self.form = document.getElementById("registry-form");
        self.error = document.getElementById("registry-error");
        self.addButton = document.getElementById("add-button");
        self.cancelButton = document.getElementById("cancel-button");

        self.init = function () {
            self.overlay.style.visibility = "visible";
            self.form.innerHTML = "";
            self.dialog.showModal();
            self.show();
        }

        self.show = function () {
            self.error.style.visibility = "hidden";
            self.cancelButton.style.visibility = "hidden";
            self.addButton.style.visibility = "hidden";
            self.form.style.visibility = "hidden";

            makeGetCall("GetRegistry", null,
                function (request) {
                    if (request.readyState === 4) {
                        var message = request.responseText;

                        if (request.status === 200) {
                            self.update(JSON.parse(message));
                        } else {
                            self.error.innerText = message;
                            self.error.style.visibility = "visible";
                            self.cancelButton.style.visibility = "visible";
                        }
                    }
                });
        }

        self.update = function (usersList) {
            var userDiv, nameLabel, checkBox;

            usersList.forEach(
                function (user) {
                    userDiv = document.createElement("div");

                    nameLabel = document.createElement("h3");
                    nameLabel.innerText = user.name + " " + user.surname;

                    checkBox = document.createElement("input");
                    checkBox.setAttribute("type", "checkbox");
                    checkBox.setAttribute("name", "checked");
                    checkBox.setAttribute("value", user.id);

                    userDiv.appendChild(nameLabel);
                    userDiv.appendChild(checkBox);
                    self.form.appendChild(userDiv);
                })
            self.form.style.visibility = "visible";
            self.addButton.style.visibility = "visible";
            self.cancelButton.style.visibility = "visible";
        }

        self.delete = function () {

            //self.error.style.visibility = "hidden";
            self.cancelButton.style.visibility = "hidden";
            self.addButton.style.visibility = "hidden";

            makeGetCall("DeleteMeeting", null,
                function (request) {
                    if (request.readyState === 4) {
                        var message = request.responseText;

                        if (request.status === 200) {
                            sessionStorage.removeItem("maxParticipants");
                            self.times = 0;
                            self.maxParticipants = 0;
                            alert("Meeting canceled");
                            self.addButton.removeEventListener("click", self.add);
                            self.cancelButton.removeEventListener("click", self.delete);
                            registryWindow = undefined;
                            self.hide();
                        } else {
                            self.error.innerText = message;
                            self.error.style.visibility = "visible";
                            self.cancelButton.style.visibility = "visible";
                            self.addButton.style.visibility = "visible";
                        }
                    }
                });
        }

        self.hide = function () {
            self.form.innerHTML = "";
            self.dialog.close();
            self.overlay.style.visibility = "hidden";
        }

        self.add = function(){
            self.times = self.times + 1;

            var allCheckBox = Array.from(self.form.querySelectorAll('input[type="checkbox"]'));
            var selected = 0;

            allCheckBox.forEach(
                function (box) {
                    if (box.checked) {
                        selected++;
                    }
                }
            )

            self.error.style.visibility = "hidden";

            if (selected > self.maxParticipants) {
                if (self.times < 3) {
                    self.error.innerText = "Too many participants. Deselect at least " + String(selected - self.maxParticipants) + " (" + self.times + ")";
                    self.error.style.visibility = "visible";
                } else {
                    self.error.innerText = "Too many attempts. Deleting";
                    self.error.style.visibility = "visible";
                    self.delete();
                }
            } else if (selected === 0) {
                if (self.times < 3) {
                    self.error.innerText = "You must select at least one participant (" + self.times + ")";
                    self.error.style.visibility = "visible";
                } else {
                    self.error.innerText = "Too many attempts. Deleting";
                    self.error.style.visibility = "visible";
                    self.delete();
                }
            } else {
                makePostCall("AddParticipants", self.form,
                    function (request) {
                        if (request.readyState === XMLHttpRequest.DONE) {
                            var message = request.responseText;

                            if (request.status === 200) {
                                sessionStorage.removeItem("maxParticipants");
                                self.times = 0;
                                alert("Meeting successful created");
                                self.hide();
                                myMeetings.reset();
                            } else {
                                self.error.innerText = message;
                                self.error.style.visibility = "visible";
                            }
                        }
                    }, false)
            }
        }

        self.cancelButton.addEventListener("click", self.delete);

        self.addButton.addEventListener("click", self.add);

    }

    function CreationWizard(form, page, errorLabel) {

        var self = this;

        self.form = form;
        self.errorLabel = errorLabel;
        self.page = page;

        var currentTime = new Date();

        self.form.querySelector('input[type="date"]').setAttribute("min", (currentTime.toISOString().substring(0, 10)));

        self.form.addEventListener("submit", (e) => {
            e.preventDefault();

            self.errorLabel.style.visibility = "hide";

            currentTime = new Date().getTime();

            var selectedDate = self.form.date.value;
            var selectedTime = self.form.start.value;

            var timeObtained = (new Date(Date.parse(selectedDate))).setHours(selectedTime.substring(0, 2), selectedTime.substring(3, 5), 0, 0);

            if (timeObtained < currentTime) {
                self.errorLabel.innerText = "Meeting must be later";
                self.errorLabel.style.visibility = "visible";
            } else {
                makePostCall(self.page, self.form,
                    function (request) {
                        if (request.readyState === XMLHttpRequest.DONE) {
                            var message = request.responseText;

                            if (request.status === 200) {
                                sessionStorage.setItem("maxParticipants", self.form.participants.value);
                                self.form.reset();
                                self.errorLabel.style.visibility = "hidden";
                                registryWindow = new RegistryWindow();
                                registryWindow.init();
                            } else {
                                self.errorLabel.innerText = message;
                                self.errorLabel.style.visibility = "visible";
                            }
                        }
                    }, false);
            }

        });
    }

    function PersonalMessage(username, messageLabel) {
        this.username = username;

        this.show = function () {
            messageLabel.style.visibility = "hidden";
            messageLabel.innerText = this.username;
            messageLabel.style.visibility = "visible";
        }
    }

})();