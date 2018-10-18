import express from 'express'

export default class IndexRouter {
    constructor({ downloadedFileDirPath, asyncFileSystem }) {
        this.downloadedFileDirPath = downloadedFileDirPath
        this.asyncFileSystem = asyncFileSystem
        this.router = express.Router()

        const asyncWrapper = fn => (...args) => fn(...args).catch(args[2])
        this.router.get('/files', asyncWrapper(this.getFilesHandler.bind(this)))
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
}