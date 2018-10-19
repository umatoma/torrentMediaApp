import express from 'express'
import path from 'path'

export default class IndexRouter {
    constructor({ downloadedFileDirPath, asyncFileSystem, fileStreaming }) {
        this.downloadedFileDirPath = downloadedFileDirPath
        this.asyncFileSystem = asyncFileSystem
        this.fileStreaming = fileStreaming
        this.router = express.Router()

        const asyncWrapper = fn => (...args) => fn(...args).catch(args[2])

        this.router.get(
            '/files',
            asyncWrapper(this.getFilesHandler.bind(this))
        )
        this.router.get(
            '/file/streaming/:fileName',
            asyncWrapper(this.getFileStreaming.bind(this))
        )
    }

    getRouter() {
        return this.router
    }

    async getFilesHandler(req, res) {
        const dirents = await this.asyncFileSystem
            .readdirAsync(this.downloadedFileDirPath, { withFileTypes: true })
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
}