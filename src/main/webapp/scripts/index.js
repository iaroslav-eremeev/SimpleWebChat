import {Message} from "./model/message.js";
import {User} from "./model/user.js";

const chatMessages = document.querySelector('#chat-messages');
const onlineUsers = document.querySelector('#online-users');

function updateChatMessages() {
    $.ajax({
        type: "GET",
        url: "messages",
        success: function (data) {
            chatMessages.innerHTML = '';
            data.forEach(message => {
                const messageDiv = document.createElement('div');
                const userLogin = message.user.login;
                const messageText = message.messageText;
                messageDiv.innerText = `${userLogin}: ${messageText}`;
                chatMessages.appendChild(messageDiv);
            });
        }
    });
}

function updateOnlineUsers() {
    $.ajax({
        type: "GET",
        url: "login",
        success: function (data) {
            onlineUsers.innerHTML = 'ONLINE:';
            data.forEach(user => {
                const userLogin = user.login;
                const userDiv = document.createElement('div');
                userDiv.innerText = userLogin;
                onlineUsers.appendChild(userDiv);
            });
        }
    });
}

updateChatMessages();
updateOnlineUsers();

const messageForm = document.querySelector('#message-form');
messageForm.addEventListener('submit', (event) => {
    event.preventDefault();
    const messageText = document.querySelector('#message-input').value;
    const userId = localStorage.getItem('userId');
    $.ajax({
        type: "POST",
        url: "messages",
        data: {"messageText": messageText, "userId": userId},
        success: function () {
            updateChatMessages();
            document.querySelector('#message-text').value = '';
        }
    });
});

setInterval(() => {
    updateOnlineUsers();
}, 1000);




if (!!window.EventSource) {
    function isFunction(functionToCheck) {
        return functionToCheck && {}.toString.call(functionToCheck) === '[object Function]';
    }

    function debounce(func, wait) {
        var timeout;
        var waitFunc;

        return function () {
            if (isFunction(wait)) {
                waitFunc = wait;
            } else {
                waitFunc = function () {
                    return wait
                };
            }
            var context = this, args = arguments;
            var later = function () {
                timeout = null;
                func.apply(context, args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, waitFunc());
        };
    }
    // reconnectFrequencySeconds doubles every retry
    var reconnectFrequencySeconds = 1;
    var evtSource;

    var reconnectFunc = debounce(function () {
        setupEventSource();
        // Double every attempt to avoid overwhelming server
        reconnectFrequencySeconds *= 2;
        // Max out at ~1 minute as a compromise between userId experience and server load
        if (reconnectFrequencySeconds >= 64) {
            reconnectFrequencySeconds = 64;
        }
    }, function () {
        return reconnectFrequencySeconds * 1000
    });

    function setupEventSource() {
        evtSource = new EventSource('messages');
        evtSource.onmessage = function (e) {
            var msg = JSON.parse(e.data);
            //TODO обработать объект
        };
        evtSource.onopen = function () {
            // Reset reconnect frequency upon successful connection
            reconnectFrequencySeconds = 1;
        };
        evtSource.onerror = function () {
            evtSource.close();
            reconnectFunc();
        };
    }
    setupEventSource();
} else {
    alert("Your browser does not support EventSource!");
}
