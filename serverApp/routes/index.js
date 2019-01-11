import express from 'express'
import path from 'path'

function asyncWrapper(fn) {
    return (...args) => fn(...args).catch(args[2])
}

class ParamsValidator {
    static validateTorrentId(torrentId) {
        if (typeof torrentId !== 'string') {
            throw new Error('torrentId should be a string')
        }

        if (
            torrentId.startsWith('magnet:') === false &&
            torrentId.startsWith('http:') === false &&
            torrentId.startsWith('https:') === false
        ) {
            throw new Error('torrentId should be one of magnet uri, http/https url')
        }
    }
}

export default class IndexRouter {
    constructor({ downloadedFileDirPath, asyncFileSystem, fileStreaming, torrentClient }) {
        this.downloadedFileDirPath = downloadedFileDirPath
        this.asyncFileSystem = asyncFileSystem
        this.fileStreaming = fileStreaming
        this.torrentClient = torrentClient
        this.router = express.Router()

        this.router.get(
            '/files',
            asyncWrapper(this.getFilesRouteHandler.bind(this))
        )
        this.router.get(
            '/files/*',
            asyncWrapper(this.getFilesRouteHandler.bind(this))
        )
        this.router.get(
            '/file/streaming/:fileName',
            asyncWrapper(this.getFileStreamingRouteHandler.bind(this))
        )
        this.router.get(
            '/torrents',
            asyncWrapper(this.getTorrentsRouteHandler.bind(this))
        )
        this.router.post(
            '/torrent',
            asyncWrapper(this.postTorrentRouteHandler.bind(this))
        )
    }

    getRouter() {
        return this.router
    }

    async getFilesRouteHandler(req, res) {
        const directoryPath = path.join(
            this.downloadedFileDirPath,
            req.path.replace(/^\/files\/?/, '')
        )

        const dirents = await this.asyncFileSystem.readdirAsync(
            directoryPath,
            { withFileTypes: true }
        )
        const files = dirents.map(dirent => ({
            name: dirent.name,
            type: dirent.isDirectory() ? 'directory' : 'file',
        }))

        res.json(files)
    }

    async getFileStreamingRouteHandler(req, res) {
        const fileName = req.params.fileName
        const filePath = path.resolve(this.downloadedFileDirPath, fileName)
        this.fileStreaming.send(req, filePath).pipe(res)
    }

    async getTorrentsRouteHandler(req, res) {
        const torrents = this.torrentClient.getTorrents()
        res.json(torrents)
    }

    async postTorrentRouteHandler(req, res) {
        const torrentId = req.body.torrentId

        try {
            ParamsValidator.validateTorrentId(torrentId)
        } catch (e) {
            res.status(400)
            res.json({ message: e.message })
            return
        }

        this.torrentClient.addTorrent(torrentId)

        res.status(200)
        res.end()
    }
}
