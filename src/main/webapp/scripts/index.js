import {Message} from "./model/message";
import {User} from "./model/user";

const chatMessages = document.querySelector('#chat-messages');
const onlineUsers = document.querySelector('#online-users');

function updateChatMessages() {
    fetch('messages', {method: 'GET'})
        .then(response => response.json())
        .then(data => {
            chatMessages.innerHTML = '';
            data.forEach(message => {
                const messageDiv = document.createElement('div');
                const userLogin = message.user.login;
                const messageText = message.messageText;
                messageDiv.innerText = `${userLogin}: ${messageText}`;
                chatMessages.appendChild(messageDiv);
            });
        });
}

function updateOnlineUsers() {
    fetch('users', {method: 'GET'})
        .then(response => response.json())
        .then(data => {
            onlineUsers.innerHTML = 'ONLINE:';
            data.forEach(user => {
                const userLogin = user.login;
                const userDiv = document.createElement('div');
                userDiv.innerText = userLogin;
                onlineUsers.appendChild(userDiv);
            });
        });
}

updateChatMessages();
updateOnlineUsers();

const messageForm = document.querySelector('#message-form');
messageForm.addEventListener('submit', (event) => {
    event.preventDefault();
    const messageText = document.querySelector('#message-text').value;
    const userId = document.querySelector('#user-id').value;
    fetch('messages', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: `messageText=${messageText}&userId=${userId}`
    }).then(() => {
        updateChatMessages();
        document.querySelector('#message-text').value = '';
    });
});

setInterval(() => {
    updateOnlineUsers();
}, 1000);
