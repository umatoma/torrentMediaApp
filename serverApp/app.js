import cookieParser from 'cookie-parser'
import express from 'express'
import proxy from 'express-http-proxy'
import basicAuth from 'express-basic-auth'
import logger from 'morgan'
import path from 'path'
import WebTorrent from 'webtorrent'
import asyncFileSystem from './libs/asyncFileSystem'
import fileStreaming from './libs/fileStreaming'
import TorrentClient from './libs/torrentClient'
import IndexRouter from './routes/index'

const downloadedFileDirPath = path.resolve(__dirname, 'download')
const torrentClient = new TorrentClient(new WebTorrent(), downloadedFileDirPath)

const indexRouter = new IndexRouter({
    downloadedFileDirPath,
    asyncFileSystem,
    fileStreaming,
    torrentClient,
}).getRouter()

const app = express()
app.use(logger('dev'))
app.use(basicAuth({
    users: { [process.env.BASIC_USER]: process.env.BASIC_PASS },
    challenge: true,
}))
app.use(express.json())
app.use(express.urlencoded({ extended: false }))
app.use(cookieParser())
app.use(express.static(path.join(__dirname, 'public')))
app.use('/file/download', express.static(path.join(__dirname, 'download')))
app.use('/proxy/tokyotosho', proxy('www.tokyotosho.info'))
app.use(indexRouter)

console.log({ [process.env.BASIC_USER]: process.env.BASIC_PASS })

export default app
