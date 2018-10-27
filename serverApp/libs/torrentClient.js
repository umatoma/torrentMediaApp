export default class TorrentClient {
    constructor(webTorrent, downloadedFileDirPath) {
        this.webTorrent = webTorrent;
        this.downloadedFileDirPath = downloadedFileDirPath
    }

    addTorrentHandler(torrent) {
        torrent.on('done', () => {
            console.log('Done downloading')
            torrent.destroy((error) => {
                if (error) {
                    console.log(error)
                } else {
                    console.log('Destroy a torrent instance')
                }
            })
        })
    }

    addTorrent(torrentId) {
        const options = {
            path: this.downloadedFileDirPath
        }
        this.webTorrent.add(torrentId, options, this.addTorrentHandler.bind(this))
    }

    getTorrents() {
        return this.webTorrent.torrents.map(torrent => ({
            files: torrent.files.map(file => ({
                name: file.name,
                progress: file.progress,
            })),
            progress: torrent.progress,
        }))
    }
}