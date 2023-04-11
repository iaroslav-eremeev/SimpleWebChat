import {Message} from "./model/message.js";
import {User} from "./model/user.js";

const chatMessages = document.querySelector('#chat-messages');
function addChatMessage(message) {
    const messageDiv = document.createElement('div');
    const userLogin = message.user.login;
    const messageText = message.messageText;
    messageDiv.innerText = `${userLogin}: ${messageText}`;
    chatMessages.appendChild(messageDiv);
}

const onlineUsers = document.querySelector('#online-users');
function updateOnlineUsers() {
    $.ajax({
        url: "login",
        method: "GET",
        success: function (data) {
            onlineUsers.innerHTML = 'ONLINE:';
            if (data && data.length) {
                data.forEach(user => {
                    const userLogin = user.login;
                    const userDiv = document.createElement('div');
                    userDiv.innerText = userLogin;
                    onlineUsers.appendChild(userDiv);
                });
            } else {
                console.error("Unexpected response data:", data);
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.error(textStatus + " - " + errorThrown);
        }
    });
}
// Call the function initially
updateOnlineUsers();
// Call the function every 60 seconds
setInterval(updateOnlineUsers, 60000);

if (!!window.EventSource) {
    function isFunction(functionToCheck) {
        return functionToCheck && {}.toString.call(functionToCheck) === '[object Function]';
    }

    function debounce(func, wait) {
        let timeout;
        let waitFunc;

        return function () {
            if (isFunction(wait)) {
                waitFunc = wait;
            } else {
                waitFunc = function () {
                    return wait
                };
            }
            let context = this, args = arguments;
            const later = function () {
                timeout = null;
                func.apply(context, args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, waitFunc());
        };
    }
    // reconnectFrequencySeconds doubles every retry
    let reconnectFrequencySeconds = 1;
    let evtSource;

    let reconnectFunc = debounce(function () {
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
            const msg = JSON.parse(e.data);
            // add message to chat

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
