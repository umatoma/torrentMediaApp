import React from 'react'
import RssItem from './RssItem'

export default class Search extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            searchText: '',
            rssItems: [],
            isLoading: false,
            modal: null,
        }
    }

    componentDidMount() {
        this.search()
    }

    search() {
        const urlSearchParams = new URLSearchParams()
        urlSearchParams.set('terms', this.state.searchText)
        urlSearchParams.set('entries', '100')

        this.setState({ rssItems: [], isLoading: true }, () => {
            fetch(`/proxy/tokyotosho/rss.php?${urlSearchParams.toString()}`)
            .then((res) => res.text())
            .then((text) => new DOMParser().parseFromString(text, 'text/xml'))
            .then((xmlDocument) => {
                const rssItems = []
                for (const item of Array.from(xmlDocument.getElementsByTagName('item'))) {
                    rssItems.push(new RssItem({
                        category: item.getElementsByTagName('category')[0].textContent.trim(),
                        title: item.getElementsByTagName('title')[0].textContent.trim(),
                        description: item.getElementsByTagName('description')[0].textContent.trim(),
                        guid: item.getElementsByTagName('guid')[0].textContent.trim(),
                    }))
                }
                this.setState({ rssItems, isLoading: false })
            })
        })
    }

    download(rssItem) {
        fetch('/torrent', {
            method: 'POST',
            body: JSON.stringify({ torrentId: rssItem.getMagnetUrl() }),
            headers: { 'Content-Type': 'application/json' },
            credentials: 'same-origin',
        }).then(() => console.log('Started downloading...', rssItem))
    }

    showConfirmModal(rssItem) {
        this.setState({
            modal: { rssItem }
        })
    }

    hideConfirmModal() {
        this.setState({ modal: null })
    }

    render() {
        return (
            <div>
                <div className="field has-addons">
                    <div className="control is-expanded">
                        <input
                            className="input"
                            type="text"
                            placeholder="enter text"
                            onChange={(e) => this.setState({ searchText: e.target.value })}
                        />
                    </div>
                    <div className="control">
                        <button
                            className="button is-info"
                            onClick={this.search.bind(this)}
                        >
                            Search
                        </button>
                    </div>
                </div>

                {this.state.isLoading && (
                    <div>Now Loading...</div>
                )}

                {(this.state.isLoading === false) && this.state.rssItems.map((rssItem) => (
                    <div
                        key={rssItem.guid}
                        className="button is-fullwidth"
                        style={{ marginBottom: 8, justifyContent: 'left', overflowX: 'hidden' }}
                        onClick={() => this.showConfirmModal(rssItem)}
                    >
                        <span
                            className="tag is-primary"
                            style={{ marginRight: 8 }}
                        >
                            {rssItem.category}
                        </span>
                        {rssItem.title}
                    </div>
                ))}

                {this.state.modal && (
                    <div className="modal is-active">
                        <div className="modal-background"/>
                        <div className="modal-content">
                            <div className="card">
                                <header className="card-header">
                                    <p className="card-header-title">
                                        {this.state.modal.rssItem.title}
                                    </p>
                                </header>
                                <div className="card-content">
                                    <p>Would you like to download this file???</p>
                                </div>
                                <footer className="card-footer">
                                    <div className="card-footer-item">
                                        <div
                                            className="button is-white is-fullwidth"
                                            onClick={() => this.hideConfirmModal()}
                                        >
                                            No
                                        </div>
                                    </div>
                                    <div className="card-footer-item">
                                        <div
                                            className="button is-white is-fullwidth"
                                            onClick={() => {
                                                this.download(this.state.modal.rssItem)
                                                this.hideConfirmModal()
                                            }}
                                        >
                                            Yes
                                        </div>
                                    </div>
                                </footer>
                            </div>
                        </div>
                        <button
                            className="modal-close is-large"
                            onClick={() => this.hideConfirmModal()}
                        />
                    </div>
                )}
            </div>
        )
    }
}