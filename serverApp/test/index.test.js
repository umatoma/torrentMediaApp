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

        testDouble = {
            readdirAsyncStub: sandbox.stub(asyncFileSystem, 'readdirAsync'),
            sendStub: sandbox.stub(fileStreaming, 'send'),
        }

        indexRouter = new IndexRouter({
            downloadedFileDirPath: '/path/to/download',
            asyncFileSystem,
            fileStreaming,
        })

        app = express()
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
})