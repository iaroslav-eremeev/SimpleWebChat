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
    const messageText = document.querySelector('#message-text').value;
    const userId = document.querySelector('#user-id').value;
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
