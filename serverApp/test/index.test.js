import { expect } from 'chai'
import express from 'express'
import supertest from 'supertest'
import indexRouter from '../routes/index'

describe('index router', () => {
    describe('GET /title', () => {
        it('should return title', async () => {
            const app = express()
            app.use(indexRouter)


            const res = await supertest(app).get('/title')


            expect(res.body.title).to.equal('Express')
        })
    })
})