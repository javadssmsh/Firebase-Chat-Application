package developer.company.chatapplication.Model

class User() {
    var id: String? = null
    var image: String? = null
    var username: String? = null
    var status: String? = null
    var search: String? = null

    constructor(id: String, image: String, username: String, status: String,search:String) : this() {
        this.id = id
        this.image = image
        this.username = username
        this.status = status
        this.search = search
    }
}