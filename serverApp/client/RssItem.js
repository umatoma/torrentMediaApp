export default class RssItem {
    constructor({ category, title, description, guid }) {
        this.category = category
        this.title = title
        this.description = description
        this.guid = guid
    }

    getMagnetUrl() {
        const results = this.description.match(/magnet:\?xt=urn:btih:\w{32}/)
        if (results) {
            return results[0]
        }
        return null
    }
}