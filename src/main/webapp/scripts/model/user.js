class User {
    constructor(id, login, password, hash, isOnline, messages) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.hash = hash;
        this.isOnline = isOnline;
        this.messages = messages || [];
    }

    getId() {
        return this.id;
    }

    setId(id) {
        this.id = id;
    }

    getLogin() {
        return this.login;
    }

    setLogin(login) {
        this.login = login;
    }

    getPassword() {
        return this.password;
    }

    setPassword(password) {
        this.password = password;
    }

    getHash() {
        return this.hash;
    }

    setHash(hash) {
        this.hash = hash;
    }

    getIsOnline() {
        return this.isOnline;
    }

    setIsOnline(isOnline) {
        this.isOnline = isOnline;
    }

    getMessages() {
        return this.messages;
    }

    setMessages(messages) {
        this.messages = messages;
    }

    addMessage(message) {
        this.messages.push(message);
    }

    deleteMessage(messageId) {
        this.messages = this.messages.filter(message => message.id !== messageId);
    }

    deleteAllMessages() {
        this.messages = [];
    }
}

export { User };
