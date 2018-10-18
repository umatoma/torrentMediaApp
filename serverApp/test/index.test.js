import { expect } from 'chai'
import express from 'express'
import sinon from 'sinon'
import supertest from 'supertest'
import asyncFileSystem from '../libs/asyncFileSystem'
import IndexRouter from '../routes/index'

describe('index router', () => {
    describe('GET /files', () => {
        let sandbox, indexRouter, app, readdirAsyncStub, res

        beforeEach(() => {
            sandbox = sinon.createSandbox()

            readdirAsyncStub = sandbox.stub(asyncFileSystem, 'readdirAsync')

            indexRouter = new IndexRouter({
                downloadedFileDirPath: '/path/to/download',
                asyncFileSystem,
            })

            app = express()
            app.use(indexRouter.getRouter())
        })

        afterEach(() => {
            sandbox.restore()
        })

        context('when there are some files in the downloaded file directory', () => {
            beforeEach(async () => {
                readdirAsyncStub.resolves([
                    { isDirectory: () => false, name: 'FILE_A.txt' },
                    { isDirectory: () => false, name: 'FILE_B.txt' },
                ])


                res = await supertest(app).get('/files')
            })

            it('should retrieve the files from the directory', () => {
                sinon.assert.calledWith(readdirAsyncStub,
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
                readdirAsyncStub.resolves([
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
                readdirAsyncStub.resolves([])


                res = await supertest(app).get('/files')
            })

            it('should return empty array', async () => {
                expect(res.body).to.deep.equal([])
            })
        })
    })
})