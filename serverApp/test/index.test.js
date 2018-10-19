import { expect } from 'chai'
import express from 'express'
import sinon from 'sinon'
import supertest from 'supertest'
import asyncFileSystem from '../libs/asyncFileSystem'
import fileStreaming from '../libs/fileStreaming'
import IndexRouter from '../routes/index'

describe('index router', () => {

    let sandbox, indexRouter, app, testDouble, res

    beforeEach(() => {
        sandbox = sinon.createSandbox()

        const torrentClient = {
            add: () => {},
        }

        testDouble = {
            readdirAsyncStub: sandbox.stub(asyncFileSystem, 'readdirAsync'),
            sendStub: sandbox.stub(fileStreaming, 'send'),
            torrentClientAddStub: sandbox.stub(torrentClient, 'add')
        }

        indexRouter = new IndexRouter({
            downloadedFileDirPath: '/path/to/download',
            asyncFileSystem,
            fileStreaming,
            torrentClient,
        })

        app = express()
        app.use(express.json())
        app.use(indexRouter.getRouter())
    })

    afterEach(() => {
        sandbox.restore()
    })

    describe('GET /files', () => {
        context('when there are some files in the downloaded file directory', () => {
            beforeEach(async () => {
                testDouble.readdirAsyncStub.resolves([
                    { isDirectory: () => false, name: 'FILE_A.txt' },
                    { isDirectory: () => false, name: 'FILE_B.txt' },
                ])


                res = await supertest(app).get('/files')
            })

            it('should retrieve the files from the directory', () => {
                sinon.assert.calledWith(testDouble.readdirAsyncStub,
                    '/path/to/download',
                    sinon.match({ withFileTypes: true })
                )
            })

            it('should return the file objects in the directory', () => {
                expect(res.body).to.deep.equal([
                    { name: 'FILE_A.txt', type: 'file' },
                    { name: 'FILE_B.txt', type: 'file' },
                ])
            })
        })

        context('when there are some directories in the downloaded file directory', () => {
            beforeEach(async () => {
                testDouble.readdirAsyncStub.resolves([
                    { isDirectory: () => true, name: 'DIR_X' },
                    { isDirectory: () => true, name: 'DIR_Y' },
                ])


                res = await supertest(app).get('/files')
            })

            it('should return the file objects with type directory', () => {
                expect(res.body).to.deep.equal([
                    { name: 'DIR_X', type: 'directory' },
                    { name: 'DIR_Y', type: 'directory' },
                ])
            })
        })

        context('when there are no files in the downloaded file directory', () => {
            beforeEach(async () => {
                testDouble.readdirAsyncStub.resolves([])


                res = await supertest(app).get('/files')
            })

            it('should return empty array', async () => {
                expect(res.body).to.deep.equal([])
            })
        })
    })

    describe('GET /file/streaming/:fileName', () => {
        beforeEach(async () => {
            testDouble.sendStub.returns({
                pipe: (res) => res.send('OK'),
            })


            await supertest(app).get('/file/streaming/FILE_A.txt')
        })

        it('should stream the file in the directory', () => {
            sinon.assert.calledWith(testDouble.sendStub,
                sinon.match.object,
                '/path/to/download/FILE_A.txt'
            )
        })
    })

    describe('POST /torrent', () => {
        context('when requesting with magnet uri', () => {
            beforeEach(async () => {
                res = await supertest(app).post('/torrent')
                    .send({ torrentId: 'magnet:?xt=urn:btih:08ada5a7a6183aae1e09d831df6748d566095a10' })
            })

            it('should return 200', () => {
                expect(res.status).to.equal(200)
            })

            it('adds the torrentId to downloading torrent list', () => {
                sinon.assert.calledWith(testDouble.torrentClientAddStub,
                    'magnet:?xt=urn:btih:08ada5a7a6183aae1e09d831df6748d566095a10',
                    sinon.match({ path: '/path/to/download' }),
                )
            })
        })

        context('when requesting with torrent url', () => {
            beforeEach(async () => {
                res = await supertest(app).post('/torrent')
                    .send({ torrentId: 'http://example.com/abc.torrent' })
            })

            it('should return 200', () => {
                expect(res.status).to.equal(200)
            })
        })

        context('when requesting with invalid torrentId', () => {
            it('should return 400', async () => {
                res = await supertest(app).post('/torrent')
                    .send({ torrentId: 'TEST_TORRENT_ID' })


                expect(res.status).to.equal(400)
            })

            it('should return invalid type error message', async () => {
                res = await supertest(app).post('/torrent')
                    .send({ torrentId: 12345 })


                expect(res.body.message).to.equal('torrentId should be a string')
            })

            it('should return format error message', async () => {
                res = await supertest(app).post('/torrent')
                    .send({ torrentId: 'ID:INVALID_FORMAT' })


                expect(res.body.message).to.equal('torrentId should be one of magnet uri, http/https url')
            })
        })
    })
})