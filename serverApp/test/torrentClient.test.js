import { expect } from 'chai'
import sinon from 'sinon'
import TorrentClient from '../libs/torrentClient'

describe('TorrentClient', () => {
    let sandbox, webTorrent

    beforeEach(() => {
        sandbox = sinon.createSandbox()

        webTorrent = {
            torrents: [],
            add: () => undefined,
        }
    })

    afterEach(() => {
        sandbox.restore()
    })

    describe('getTorrents()', () => {
        it('should return all torrents', () => {
            const torrentClient = new TorrentClient(webTorrent)
            sinon.stub(webTorrent, 'torrents').get(() => {
                return [
                    {
                        files: [
                            { name: 'FILE_A.txt', progress: 0.1 },
                            { name: 'FILE_B.txt', progress: 0.1 },
                        ],
                        progress: 0.5,
                    },
                    {
                        files: [
                            { name: 'FILE_C.txt', progress: 0.2 },
                        ],
                        progress: 0.6,
                    },
                ]
            })


            const torrents = torrentClient.getTorrents()


            expect(torrents).to.be.deep.equal([
                {
                    files: [
                        { name: 'FILE_A.txt', progress: 0.1 },
                        { name: 'FILE_B.txt', progress: 0.1 },
                    ],
                    progress: 0.5,
                },
                {
                    files: [
                        { name: 'FILE_C.txt', progress: 0.2 },
                    ],
                    progress: 0.6,
                },
            ])
        })
    })

    describe('addTorrent()', () => {
        it('should call the add function with torrentId, path and callback', () => {
            const downloadedFileDirPath = '/path/to/download'
            const torrentClient = new TorrentClient(webTorrent, downloadedFileDirPath)
            const addSpy = sandbox.spy(webTorrent, 'add')


            torrentClient.addTorrent(
                'magnet:?xt=urn:btih:08ada5a7a6183aae1e09d831df6748d566095a10'
            )


            sinon.assert.calledWith(addSpy,
                'magnet:?xt=urn:btih:08ada5a7a6183aae1e09d831df6748d566095a10',
                sinon.match({ path: '/path/to/download' }),
                sinon.match.func,
            )
        })
    })
})