class Message {
    constructor(id, messageText, user) {
        this.id = id;
        this.messageText = messageText;
        this.user = user;
    }

    get id() {
        return this._id;
    }

    set id(value) {
        this._id = value;
    }

    get messageText() {
        return this._messageText;
    }

    set messageText(value) {
        this._messageText = value;
    }

    get user() {
        return this._user;
    }

    set user(value) {
        this._user = value;
    }

    getUserId() {
        return this.user.id;
    }
}

export {Message};
