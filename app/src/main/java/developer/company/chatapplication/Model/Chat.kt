package developer.company.chatapplication.Model

class Chat() {
    var message: String? = null
    var receiver: String? = null
    var sender: String? = null
    var isseen: Boolean? = null

    constructor(message: String, receiver: String, sender: String, isseen: Boolean) : this() {
        this.message = message
        this.receiver = receiver
        this.sender = sender
        this.isseen = isseen
    }
}