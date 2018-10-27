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
            asyncWrapper(this.getFilesHandler.bind(this))
        )
        this.router.get(
            '/file/streaming/:fileName',
            asyncWrapper(this.getFileStreaming.bind(this))
        )
        this.router.get(
            '/torrents',
            asyncWrapper(this.getTorrents.bind(this))
        )
        this.router.post(
            '/torrent',
            asyncWrapper(this.postTorrent.bind(this))
        )
    }

    getRouter() {
        return this.router
    }

    async getFilesHandler(req, res) {
        const dirents = await this.asyncFileSystem.readdirAsync(
            this.downloadedFileDirPath,
            { withFileTypes: true }
        )
        const files = dirents.map(dirent => ({
            name: dirent.name,
            type: dirent.isDirectory() ? 'directory' : 'file',
        }))

        res.json(files)
    }

    async getFileStreaming(req, res) {
        const fileName = req.params.fileName
        const filePath = path.resolve(this.downloadedFileDirPath, fileName)
        this.fileStreaming.send(req, filePath).pipe(res)
    }

    async getTorrents(req, res) {
        const torrents = this.torrentClient.getTorrents()
        res.json(torrents)
    }

    async postTorrent(req, res) {
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