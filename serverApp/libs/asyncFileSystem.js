import Bluebird from 'bluebird'
import fs from 'fs'

const asyncFileSystem = Bluebird.promisifyAll(fs)
export default asyncFileSystem
