import express from 'express'

const router = express.Router()

router.get('/title', function (req, res, next) {
    res.json({ title: 'Express' })
})

export default router
